package com.example.budgetcircle.forms

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.InputFilter
import androidx.core.widget.doOnTextChanged
import com.example.budgetcircle.R
import com.example.budgetcircle.databinding.ActivityExpensesFormBinding
import com.example.budgetcircle.dialogs.Dialogs
import com.example.budgetcircle.dialogs.Index
import com.example.budgetcircle.settings.SumInputFilter

/*import com.example.budgetcircle.viewmodel.items.BudgetType*/

class ExpensesFormActivity : AppCompatActivity() {
    lateinit var binding: ActivityExpensesFormBinding
    var chosenBudgetType: Index = Index(0)
    var chosenExpenseType: Index = Index(0)
    lateinit var budgetTypes: Array<String>
    lateinit var budgetTypesSums: Array<Double>
    lateinit var expenseTypes: Array<String>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityExpensesFormBinding.inflate(layoutInflater)
        budgetTypes = intent.extras?.getStringArray("budgetTypes")!!
        budgetTypesSums = (intent.extras?.getSerializable("budgetTypesSums")!! as Array<Double>)
        expenseTypes = intent.extras?.getStringArray("expenseTypes")!!
        binding.expSelectBudgetType.text = budgetTypes[0]
        binding.expSelectKind.text = expenseTypes[0]
        binding.expSum.filters = arrayOf<InputFilter>(SumInputFilter())
        setButtons()
        setContentView(binding.root)
    }

    private fun setButtons() {
        binding.expDateLayout.setOnClickListener {
            Dialogs().pickDate(
                this,
                binding.expDate,
                R.style.redColorDatePicker
            )
        }
        binding.expSelectBudgetType.setOnClickListener {
            Dialogs().chooseOne(
                this,
                resources.getString(R.string.account),
                budgetTypes,
                binding.expSelectBudgetType,
                chosenBudgetType
            )
        }
        binding.expKindLayout.setOnClickListener {
            Dialogs().chooseOne(
                this,
                resources.getString(R.string.kind),
                expenseTypes,
                binding.expSelectKind,
                chosenExpenseType
            )
        }
        binding.expAddButton.setOnClickListener {
            add()
        }
        binding.backButton.setOnClickListener {
            exit()
        }
    }

    private fun checkFields(): Boolean {
        var sum = binding.expSum.text.toString().toDoubleOrNull()
        var isValid = true
        binding.expSum.apply {
            error = null
            when {
                sum == null -> {
                    error = resources.getString(R.string.empty_field)
                    isValid = false
                }
                sum <= 0.0 -> {
                    error = resources.getString(R.string.zero_sum)
                    isValid = false
                }
                sum > budgetTypesSums[chosenBudgetType.value] -> {
                    error =
                        "${resources.getString(R.string.insufficient_funds)} (${budgetTypesSums[chosenBudgetType.value]})"
                    isValid = false
                }
            }
        }
        binding.expTitle.apply {
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
            intent.putExtra("sum", binding.expSum.text.toString().toDouble())
            intent.putExtra("expenseTypeIndex", chosenExpenseType.value)
            intent.putExtra("isRep", binding.expRepSwitch.isChecked)
            intent.putExtra("date", binding.expDate.text.toString())
            intent.putExtra("title", binding.expTitle.text.toString())
            intent.putExtra("budgetTypeIndex", chosenBudgetType.value)
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