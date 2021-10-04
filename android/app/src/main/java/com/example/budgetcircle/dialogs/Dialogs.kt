package com.example.budgetcircle.dialogs

import android.app.DatePickerDialog
import android.app.DatePickerDialog.OnDateSetListener
import android.content.Context
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import android.widget.TextView
import com.example.budgetcircle.MainActivity
import java.util.*
import android.widget.DatePicker
import com.example.budgetcircle.R


class Dialogs() {
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

    fun pickDate(context: Context, view: TextView, theme: Int) {
        val calendar = Calendar.getInstance()
        val Year = calendar[Calendar.YEAR]
        val Month = calendar[Calendar.MONTH]
        val Day = calendar[Calendar.DAY_OF_MONTH]
        DatePickerDialog(
            context,
            theme,
            OnDateSetListener { _, year, monthOfYear, dayOfMonth ->
                val editTextDateParam = dayOfMonth.toString() + "." + (monthOfYear + 1) + "." + year
                view.text = editTextDateParam
            }, Year, Month, Day
        )
            .show()

    }
}
