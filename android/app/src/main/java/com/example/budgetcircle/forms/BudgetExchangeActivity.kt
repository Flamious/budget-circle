package com.example.budgetcircle.forms

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.InputFilter
import com.example.budgetcircle.R
import com.example.budgetcircle.databinding.ActivityBudgetExchangeBinding
import com.example.budgetcircle.dialogs.Dialogs
import com.example.budgetcircle.dialogs.Index
import com.example.budgetcircle.settings.SumInputFilter

class BudgetExchangeActivity : AppCompatActivity() {
    lateinit var binding: ActivityBudgetExchangeBinding
    private var chosenBudgetTypeFrom: Index = Index(0)
    private var chosenBudgetTypeTo: Index = Index(1)
    lateinit var budgetTypes: Array<String>
    private lateinit var budgetTypesSums: Array<Double>
    private var isEdit: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityBudgetExchangeBinding.inflate(layoutInflater)
        setInitialValues()
        setButtons()
        setContentView(binding.root)
    }

    override fun onBackPressed() {
        exit()
    }

    //region Setting
    private fun setButtons() {
        binding.budgetExchangeAddButton.setOnClickListener {
            add()
        }
        binding.backButton.setOnClickListener {
            exit()
        }
        binding.listFrom.setOnClickListener {
            Dialogs().chooseOne(
                this,
                resources.getString(R.string.account),
                budgetTypes,
                binding.listFrom,
                chosenBudgetTypeFrom,
                R.style.greenEdgeEffect
            )
        }
        binding.listTo.setOnClickListener {
            Dialogs().chooseOne(
                this,
                resources.getString(R.string.account),
                budgetTypes,
                binding.listTo,
                chosenBudgetTypeTo,
                R.style.greenEdgeEffect
            )
        }
    }

    private fun setEditPage() {
        binding.budgetSum.setText(intent.getDoubleExtra("exchangeSum", 0.0).toString())
        chosenBudgetTypeFrom.value = intent.extras?.getInt("fromIndex")!!
        chosenBudgetTypeTo.value = intent.extras?.getInt("toIndex")!!
        binding.budgetExchangeAddButton.text = resources.getText(R.string.edit)
        binding.listFrom.text = budgetTypes[chosenBudgetTypeFrom.value]
        binding.listTo.text = budgetTypes[chosenBudgetTypeTo.value]
        isEdit = true
    }

    private fun setInitialValues() {
        budgetTypes = intent.extras?.getStringArray("budgetTypes")!!
        if (intent.extras?.getBoolean("isEdit", false) == false) {
            binding.listFrom.text = budgetTypes[0]
            binding.listTo.text = budgetTypes[1]
            budgetTypesSums =
                (intent.extras?.getSerializable("budgetTypesSums")!! as Array<*>).filterIsInstance<Double>()
                    .toTypedArray()
        } else {
            setEditPage()
            budgetTypesSums = arrayOf()
        }
        binding.budgetSum.filters = arrayOf<InputFilter>(SumInputFilter())
    }

    //endregion
    //region Methods
    private fun checkFields(): Boolean {
        val sum = binding.budgetSum.text.toString().toDoubleOrNull()
        var isValid = true
        binding.budgetSum.apply {
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
                else -> if (!isEdit) {
                    if (sum > budgetTypesSums[chosenBudgetTypeFrom.value]) {
                        error =
                            "${resources.getString(R.string.insufficient_funds)} (${budgetTypesSums[chosenBudgetTypeFrom.value]})"
                        isValid = false
                    }
                }
            }
        }

        return isValid
    }

    private fun add() {
        if (checkFields()) {
            val intent = Intent()
            if(isEdit) {
                intent.putExtra("budgetTypeIndex", chosenBudgetTypeFrom.value)
                intent.putExtra("typeIndex", chosenBudgetTypeTo.value)
            }
            else {
                intent.putExtra("fromIndex", chosenBudgetTypeFrom.value)
                intent.putExtra("toIndex", chosenBudgetTypeTo.value)
            }
            intent.putExtra("type", "exchange")
            intent.putExtra("sum", binding.budgetSum.text.toString().toDouble())
            setResult(RESULT_OK, intent)
            finish()
        }
    }

    private fun exit() {
        val intent = Intent()
        setResult(RESULT_CANCELED, intent)
        finish()
    }
    //endregion
}