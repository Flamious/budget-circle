package com.example.budgetcircle.forms

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.core.widget.doOnTextChanged
import com.example.budgetcircle.R
import com.example.budgetcircle.databinding.ActivityEarningsFormBinding
import com.example.budgetcircle.dialogs.Dialogs
import com.example.budgetcircle.dialogs.Index
import com.example.budgetcircle.settings.SumInputFilter
import android.text.InputFilter


/*import com.example.budgetcircle.viewmodel.items.BudgetType*/

class EarningsFormActivity : AppCompatActivity() {
    lateinit var binding: ActivityEarningsFormBinding
    var chosenBudgetType: Index = Index(0)
    var chosenEarningType: Index = Index(0)
    lateinit var budgetTypes: Array<String>
    lateinit var earningTypes: Array<String>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityEarningsFormBinding.inflate(layoutInflater)
        budgetTypes = intent.extras?.getStringArray("budgetTypes")!!
        earningTypes = intent.extras?.getStringArray("earningTypes")!!
        binding.earnSelectBudgetType.text = budgetTypes[0]
        binding.earnSelectKind.text = earningTypes[0]
        binding.earnSum.filters = arrayOf<InputFilter>(SumInputFilter())
        setButtons()
        setContentView(binding.root)
    }

    private fun setButtons() {
        binding.earnDateLayout.setOnClickListener {
            Dialogs().pickDate(
                this,
                binding.earnDate,
                R.style.blueColorDatePicker
            )
        }
        binding.earnSelectBudgetType.setOnClickListener {
            Dialogs().chooseOne(
                this,
                resources.getString(R.string.account),
                budgetTypes,
                binding.earnSelectBudgetType,
                chosenBudgetType
            )
        }
        binding.earnKindLayout.setOnClickListener {
            Dialogs().chooseOne(
                this,
                resources.getString(R.string.kind),
                earningTypes,
                binding.earnSelectKind,
                chosenEarningType
            )
        }
        binding.earnAddButton.setOnClickListener {
            add()
        }
        binding.backButton.setOnClickListener {
            exit()
        }
    }

    private fun checkFields(): Boolean {
        var sum = binding.earnSum.text.toString().toDoubleOrNull()
        var isValid = true
        binding.earnSum.apply {
            error = null
            if (sum == null) {
                error = resources.getString(R.string.empty_field)
                isValid = false
            } else if (sum <= 0.0) {
                error = resources.getString(R.string.zero_sum)
                isValid = false
            }
        }
        binding.earnTitle.apply {
            error = null
            if (text.isNullOrBlank()) {
                error = resources.getString(R.string.empty_field)
                isValid = false
            }
        }

        return isValid
    }

    private fun add() {
        if (checkFields()) {
            val intent = Intent()
            intent.putExtra("sum", binding.earnSum.text.toString().toDouble())
            intent.putExtra("earningTypeIndex", chosenEarningType.value)
            intent.putExtra("isRep", binding.earnRepSwitch.isChecked)
            intent.putExtra("date", binding.earnDate.text.toString())
            intent.putExtra("title", binding.earnTitle.text.toString())
            intent.putExtra("budgetTypeIndex", chosenBudgetType.value)
            intent.putExtra("commentary", binding.earnCommentaryField.text.toString())
            setResult(RESULT_OK, intent)
            finish()
        }
    }

    private fun exit() {
        val intent = Intent()
        setResult(RESULT_CANCELED, intent)
        finish()
    }
}