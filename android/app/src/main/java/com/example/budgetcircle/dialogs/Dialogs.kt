package com.example.budgetcircle.dialogs

import android.app.DatePickerDialog
import android.app.DatePickerDialog.OnDateSetListener
import android.content.Context
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import android.widget.TextView
import java.util.*
import androidx.lifecycle.MutableLiveData


data class Index(var value: Int)

class Dialogs {
    fun chooseOne(
        context: Context,
        title: String,
        list: Array<String>,
        view: TextView,
        index: Index
    ) {
        if (list.isNotEmpty()) {
            MaterialAlertDialogBuilder(context)
                .setTitle(title)
                .setItems(list) { _, which ->
                    run {
                        view.text = list[which]
                        index.value = which
                    }
                }
                .show()
        } else {
            view.text = null
        }
    }

    fun chooseOne(
        context: Context,
        title: String,
        list: Array<String>,
        values: Array<Int>,
        view: TextView,
        mutableDataInt: MutableLiveData<Int>
    ) {
        if (list.isNotEmpty() && values.isNotEmpty()) {
            MaterialAlertDialogBuilder(context)
                .setTitle(title)
                .setItems(list) { _, which ->
                    run {
                        view.text = list[which]
                        mutableDataInt.postValue(values[which])
                    }
                }
                .show()
        } else {
            view.text = null
        }

    }

    fun chooseTwoWithNoRepeat(
        context: Context,
        title: String,
        list: Array<String>,
        viewMain: TextView,
        viewSecondary: TextView,
        chosenTypeMain: Index,
        chosenTypeSecondary: Index
    ) {
        if (list.isNotEmpty()) {
            MaterialAlertDialogBuilder(context)
                .setTitle(title)
                .setItems(list) { _, which ->
                    run {
                        chosenTypeMain.value = which
                        viewMain.text = list[chosenTypeMain.value]

                        chosenTypeSecondary.value = if(which == 0) 1 else 0
                        viewSecondary.text = list[chosenTypeSecondary.value]
                    }
                }
                .show()
        } else {
            viewMain.text  = null
            viewSecondary.text = null
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
