package com.example.budgetcircle.forms

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.InputFilter
import com.example.budgetcircle.R
import com.example.budgetcircle.databinding.ActivityExpensesFormBinding
import com.example.budgetcircle.dialogs.Dialogs
import com.example.budgetcircle.dialogs.Index
import com.example.budgetcircle.settings.SumInputFilter

class ExpensesFormActivity : AppCompatActivity() {
    lateinit var binding: ActivityExpensesFormBinding
    private var chosenBudgetType: Index = Index(0)
    private var chosenExpenseType: Index = Index(0)
    lateinit var budgetTypes: Array<String>
    private lateinit var budgetTypesSums: Array<Double>
    private lateinit var expenseTypes: Array<String>
    private var isEdit = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityExpensesFormBinding.inflate(layoutInflater)
        budgetTypes = intent.extras?.getStringArray("budgetTypes")!!
        expenseTypes = intent.extras?.getStringArray("expenseTypes")!!
        if (intent.extras?.getBoolean("isEdit", false) == false) {
            budgetTypesSums =
                (intent.extras?.getSerializable("budgetTypesSums")!! as Array<*>).filterIsInstance<Double>()
                    .toTypedArray()
            binding.expSelectBudgetType.text = budgetTypes[0]
            binding.expSelectKind.text = expenseTypes[0]
        } else {
            setEditPage()
            budgetTypesSums = arrayOf()
        }
        binding.expSum.filters = arrayOf<InputFilter>(SumInputFilter())
        setButtons()
        setContentView(binding.root)
    }

    private fun setButtons() {
        binding.expSelectBudgetType.setOnClickListener {
            Dialogs().chooseOne(
                this,
                resources.getString(R.string.account),
                budgetTypes,
                binding.expSelectBudgetType,
                chosenBudgetType,
                R.style.redEdgeEffect
            )
        }
        binding.expKindLayout.setOnClickListener {
            Dialogs().chooseOne(
                this,
                resources.getString(R.string.kind),
                expenseTypes,
                binding.expSelectKind,
                chosenExpenseType,
                R.style.redEdgeEffect
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
        val sum = binding.expSum.text.toString().toDoubleOrNull()
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
                else -> {
                    if (!isEdit) {
                        if (sum > budgetTypesSums[chosenBudgetType.value]) {
                            error =
                                "${resources.getString(R.string.insufficient_funds)} (${budgetTypesSums[chosenBudgetType.value]})"
                            isValid = false
                        }
                    }
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

    private fun setEditPage() {
        binding.expenseFormTitle.text = resources.getText(R.string.edit_earn)
        binding.expAddButton.text = resources.getText(R.string.edit_earn)
        binding.expTitle.setText(intent.extras?.getString("title")!!)
        binding.expSum.setText(intent.extras?.getDouble("sum")!!.toString())
        binding.expCommentaryField.setText(intent.extras?.getString("commentary")!!)
        binding.expRepSwitch.isChecked = intent.extras?.getBoolean("isRep")!!
        binding.expRepSwitch.isEnabled = false
        chosenExpenseType.value = intent.extras?.getInt("typeIndex")!!
        chosenBudgetType.value = intent.extras?.getInt("budgetTypeIndex")!!
        binding.expSelectKind.text = expenseTypes[chosenExpenseType.value]
        binding.expSelectBudgetType.text = budgetTypes[chosenBudgetType.value]
        isEdit = true
    }

    private fun add() {
        if (checkFields()) {
            val intent = Intent()
            intent.putExtra("sum", binding.expSum.text.toString().toDouble())
            intent.putExtra("typeIndex", chosenExpenseType.value)
            intent.putExtra("isRep", binding.expRepSwitch.isChecked)
            intent.putExtra("title", binding.expTitle.text.toString())
            intent.putExtra("budgetTypeIndex", chosenBudgetType.value)
            intent.putExtra("commentary", binding.expCommentaryField.text.toString())
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