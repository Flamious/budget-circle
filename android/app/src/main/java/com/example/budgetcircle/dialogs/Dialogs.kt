package com.example.budgetcircle.dialogs

import android.app.DatePickerDialog
import android.app.DatePickerDialog.OnDateSetListener
import android.content.Context
import android.content.DialogInterface
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import android.widget.TextView
import com.example.budgetcircle.MainActivity
import java.util.*
import android.widget.DatePicker
import com.example.budgetcircle.R
import com.example.budgetcircle.viewmodel.items.BudgetType


class Dialogs {
    fun chooseOne(context: Context, title: String, list: Array<String>, view: TextView) {
        if (list.isNotEmpty()) {
            MaterialAlertDialogBuilder(context)
                .setTitle(title)
                .setItems(list) { _, which ->
                    run {
                        view.text = list[which]
                    }
                }
                .show()
        } else {
            view.text = null
        }
    }

    fun chooseOneBudgetType(
        context: Context,
        title: String,
        list: Array<BudgetType>,
        chosenType: BudgetType,
        view: TextView
    ) {
        if (list.isNotEmpty()) {
            val typesNames: Array<String> = Array(list.size) { index -> list[index].title!! }
            MaterialAlertDialogBuilder(context)
                .setTitle(title)
                .setItems(typesNames) { _, which ->
                    run {
                        chosenType.id = list[which].id
                        chosenType.title = list[which].title
                        chosenType.sum = list[which].sum
                        chosenType.isDeletable = list[which].isDeletable
                        view.text = list[which].title
                    }
                }
                .show()
        } else {
            view.text = null
        }
    }

    fun chooseOneBudgetType(
        context: Context,
        title: String,
        list: Array<BudgetType>,
        chosenTypeMain: BudgetType,
        chosenTypeSecondary: BudgetType,
        viewMain: TextView,
        viewSecondary: TextView
    ) {
        if (list.isNotEmpty()) {
            val typesNames: Array<String> = Array(list.size) { index -> list[index].title!! }
            MaterialAlertDialogBuilder(context)
                .setTitle(title)
                .setItems(typesNames) { _, which ->
                    run {
                        chosenTypeMain.id = list[which].id
                        chosenTypeMain.title = list[which].title
                        chosenTypeMain.sum = list[which].sum
                        chosenTypeMain.isDeletable = list[which].isDeletable
                        viewMain.text = list[which].title

                        var id: Int = if(which == 0) 1 else 0
                        chosenTypeSecondary.id = list[id].id
                        chosenTypeSecondary.title = list[id].title
                        chosenTypeSecondary.sum = list[id].sum
                        chosenTypeSecondary.isDeletable = list[id].isDeletable
                        viewSecondary.text = list[id].title
                    }
                }
                .show()
        } else {
            viewMain.text = null
        }
    }

    fun pickDate(context: Context, view: TextView, theme: Int) {
        val calendar = Calendar.getInstance()
        val year = calendar[Calendar.YEAR]
        val month = calendar[Calendar.MONTH]
        val day = calendar[Calendar.DAY_OF_MONTH]
        DatePickerDialog(
            context,
            theme,
            OnDateSetListener { _, year, monthOfYear, dayOfMonth ->
                val editTextDateParam = dayOfMonth.toString() + "." + (monthOfYear + 1) + "." + year
                view.text = editTextDateParam
            }, year, month, day
        )
            .show()

    }
}
