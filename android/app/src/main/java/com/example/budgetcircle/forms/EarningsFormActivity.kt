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
import android.view.ContextThemeWrapper

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
        if(intent.extras?.getBoolean("isEdit", false) == false){
            binding.earnSelectBudgetType.text = budgetTypes[0]
            binding.earnSelectKind.text = earningTypes[0]
        }
        else {
            setEditPage()
        }
        binding.earnSum.filters = arrayOf<InputFilter>(SumInputFilter())
        setButtons()
        setContentView(binding.root)
    }

    private fun setButtons() {
        binding.earnSelectBudgetType.setOnClickListener {
            Dialogs().chooseOne(
                this,
                resources.getString(R.string.account),
                budgetTypes,
                binding.earnSelectBudgetType,
                chosenBudgetType,
                R.style.blueEdgeEffect
            )
        }
        binding.earnKindLayout.setOnClickListener {
            Dialogs().chooseOne(
                this,
                resources.getString(R.string.kind),
                earningTypes,
                binding.earnSelectKind,
                chosenEarningType,
                R.style.blueEdgeEffect
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

    private fun setEditPage() {
        val a = intent.extras?.getInt("typeIndex")!!
        val b = intent.extras?.getInt("budgetTypeIndex")!!
        binding.earningFromTitle.text = resources.getText(R.string.edit_earn)
        binding.earnAddButton.text = resources.getText(R.string.edit_earn)
        binding.earnTitle.setText(intent.extras?.getString("title")!!)
        binding.earnSum.setText(intent.extras?.getDouble("sum")!!.toString())
        binding.earnCommentaryField.setText(intent.extras?.getString("commentary")!!)
        binding.earnRepSwitch.isChecked = intent.extras?.getBoolean("isRep")!!
        binding.earnRepSwitch.isEnabled = false
        chosenEarningType.value = intent.extras?.getInt("typeIndex")!!
        chosenBudgetType.value = intent.extras?.getInt("budgetTypeIndex")!!
        binding.earnSelectKind.text = earningTypes[chosenEarningType.value]
        binding.earnSelectBudgetType.text = budgetTypes[chosenBudgetType.value]
    }

    private fun add() {
        if (checkFields()) {
            val intent = Intent()
            intent.putExtra("sum", binding.earnSum.text.toString().toDouble())
            intent.putExtra("typeIndex", chosenEarningType.value)
            intent.putExtra("isRep", binding.earnRepSwitch.isChecked)
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