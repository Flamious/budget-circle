package com.example.budgetcircle.forms

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.budgetcircle.R
import com.example.budgetcircle.databinding.ActivityBudgetExchangeBinding
import com.example.budgetcircle.dialogs.Dialogs
import com.example.budgetcircle.viewmodel.items.BudgetType
import com.google.android.material.dialog.MaterialAlertDialogBuilder

class BudgetExchangeActivity : AppCompatActivity() {
    lateinit var binding: ActivityBudgetExchangeBinding
    lateinit var budgetTypes: Array<BudgetType>
    lateinit var chosenBudgetTypeFrom: BudgetType
    lateinit var chosenBudgetTypeTo: BudgetType

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityBudgetExchangeBinding.inflate(layoutInflater)
        budgetTypes = intent.extras?.getParcelableArray("types")?.filterIsInstance<BudgetType>()
            ?.toTypedArray()!!
        chosenBudgetTypeFrom = budgetTypes[0].copy()
        chosenBudgetTypeTo = budgetTypes[1].copy()
        binding.listTo.text = chosenBudgetTypeTo.title
        binding.listFrom.text = chosenBudgetTypeFrom.title
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
            Dialogs().chooseOneBudgetType(
                this,
                resources.getString(R.string.account),
                budgetTypes,
                chosenBudgetTypeFrom,
                chosenBudgetTypeTo,
                binding.listFrom,
                binding.listTo
            )
        }
        binding.listTo.setOnClickListener {
            val budgetTypesCut = budgetTypes.toMutableList()
            budgetTypesCut.removeAt(budgetTypes.indexOfFirst { it.id == chosenBudgetTypeFrom.id })
            Dialogs().chooseOneBudgetType(
                this,
                resources.getString(R.string.account),
                budgetTypesCut.toTypedArray(),
                chosenBudgetTypeTo,
                binding.listTo
            )
        }
    }

    private fun add() {
        val intent = Intent()
        intent.putExtra("type", "exchange")
        intent.putExtra("sum", binding.budgetSum.text.toString().toFloat())
        intent.putExtra("from", chosenBudgetTypeFrom.id)
        intent.putExtra("to", chosenBudgetTypeTo.id)
        setResult(RESULT_OK, intent)
        finish()
    }

    private fun exit() {
        val intent = Intent()
        setResult(RESULT_CANCELED, intent)
        finish()
    }
}