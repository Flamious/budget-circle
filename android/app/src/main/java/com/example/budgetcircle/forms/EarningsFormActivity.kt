package com.example.budgetcircle.forms

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.core.widget.doOnTextChanged
import com.example.budgetcircle.R
import com.example.budgetcircle.databinding.ActivityEarningsFormBinding
import com.example.budgetcircle.dialogs.Dialogs
import com.example.budgetcircle.viewmodel.items.BudgetType

class EarningsFormActivity : AppCompatActivity() {
    lateinit var binding: ActivityEarningsFormBinding
    lateinit var budgetTypes: Array<BudgetType>
    lateinit var chosenBudgetType: BudgetType
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityEarningsFormBinding.inflate(layoutInflater)
        budgetTypes = intent.extras?.getParcelableArray("types")?.filterIsInstance<BudgetType>()?.toTypedArray()!!
        chosenBudgetType = budgetTypes[0].copy()
        binding.earnSelectBudgetType.text = chosenBudgetType.title
        setButtons()
        setContentView(binding.root)
    }

    private fun setButtons() {
        binding.earnSum.doOnTextChanged { _, _, _, _ ->
            check()
        }
        binding.earnTitle.doOnTextChanged { _, _, _, _ ->
            check()
        }
        binding.earnDateLayout.setOnClickListener {
            Dialogs().pickDate(
                this,
                binding.earnDate,
                R.style.blueColorDatePicker
            )
        }
        binding.earnSelectBudgetType.setOnClickListener {
            Dialogs().chooseOneBudgetType(
                this,
                "Account",
                budgetTypes,
                chosenBudgetType,
                binding.earnSelectBudgetType
            )
        }
        binding.earnKindLayout.setOnClickListener {
            Dialogs().chooseOne(
                this,
                resources.getString(R.string.kind),
                resources.getStringArray(R.array.earning_titles),
                binding.earnSelectKind
            )
        }
        binding.earnAddButton.setOnClickListener {
            add()
        }
        binding.backButton.setOnClickListener {
            exit()
        }
    }

    private fun check() {
        var sum = binding.earnSum.text.toString().toFloatOrNull()
        binding.earnAddButton.isEnabled =
            !(sum == null || sum <= 0f || binding.earnTitle.text.isNullOrBlank())
    }

    private fun add() {
        val intent = Intent()
        intent.putExtra("sum", binding.earnSum.text.toString().toFloat())
        intent.putExtra("type", binding.earnSelectKind.text.toString())
        intent.putExtra("isRep", binding.earnRepSwitch.isChecked)
        intent.putExtra("date", binding.earnDate.text.toString())
        intent.putExtra("title", binding.earnTitle.text.toString())
        intent.putExtra("budgetTypeId", chosenBudgetType.id)
        setResult(RESULT_OK, intent)
        finish()
    }

    private fun exit() {
        val intent = Intent()
        setResult(RESULT_CANCELED, intent)
        finish()
    }
}