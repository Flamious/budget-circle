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
/*import com.example.budgetcircle.viewmodel.items.BudgetType*/
import com.google.android.material.dialog.MaterialAlertDialogBuilder

class BudgetExchangeActivity : AppCompatActivity() {
    lateinit var binding: ActivityBudgetExchangeBinding
    var chosenBudgetTypeFrom: Index = Index(0)
    var chosenBudgetTypeTo: Index = Index(1)
    lateinit var budgetTypes: Array<String>
    lateinit var budgetTypesSums: Array<Double>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityBudgetExchangeBinding.inflate(layoutInflater)
        budgetTypes = intent.extras?.getStringArray("budgetTypes")!!
        budgetTypesSums = (intent.extras?.getSerializable("budgetTypesSums")!! as Array<Double>)
        binding.listFrom.text = budgetTypes[0]
        binding.listTo.text = budgetTypes[1]
        binding.budgetSum.filters = arrayOf<InputFilter>(SumInputFilter())
        setButtons()
        setContentView(binding.root)
    }

    private fun setButtons() {
        binding.budgetExchangeAddButton.setOnClickListener {
            add()
        }
        binding.backButton.setOnClickListener {
            exit()
        }
        binding.listFrom.setOnClickListener {
            Dialogs().chooseTwoWithNoRepeat(
                this,
                resources.getString(R.string.account),
                budgetTypes,
                binding.listFrom,
                binding.listTo,
                chosenBudgetTypeFrom,
                chosenBudgetTypeTo
            )
        }
        binding.listTo.setOnClickListener {
            val budgetTypesCut = budgetTypes.toMutableList()
            budgetTypesCut.removeAt(chosenBudgetTypeFrom.value)
            Dialogs().chooseOne(
                this,
                resources.getString(R.string.account),
                budgetTypesCut.toTypedArray(),
                binding.listTo,
                chosenBudgetTypeTo
            )
        }
    }

    private fun checkFields(): Boolean {
        var sum = binding.budgetSum.text.toString().toDoubleOrNull()
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
                sum > budgetTypesSums[chosenBudgetTypeFrom.value] -> {
                    error =
                        "${resources.getString(R.string.insufficient_funds)} (${budgetTypesSums[chosenBudgetTypeFrom.value]})"
                    isValid = false
                }
            }
        }

        return isValid
    }

    private fun add() {
        if (checkFields()) {
            val intent = Intent()
            intent.putExtra("type", "exchange")
            intent.putExtra("sum", binding.budgetSum.text.toString().toDouble())
            intent.putExtra("fromIndex", chosenBudgetTypeFrom.value)
            intent.putExtra("toIndex", chosenBudgetTypeTo.value)
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