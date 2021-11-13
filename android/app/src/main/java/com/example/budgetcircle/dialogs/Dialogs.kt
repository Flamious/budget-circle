package com.example.budgetcircle.dialogs

import android.app.AlertDialog
import android.content.Context
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.lifecycle.MutableLiveData
import com.example.budgetcircle.R

data class Index(var value: Int)

class Dialogs {
    fun chooseOne(
        context: Context,
        title: String,
        list: Array<String>,
        view: TextView,
        index: Index,
        theme: Int = -1
    ) {
        if (list.isNotEmpty()) {
            val dialog = MaterialAlertDialogBuilder(context)
            dialog.setTitle(title)
            dialog.setItems(list) { _, which ->
                run {
                    view.text = list[which]
                    index.value = which
                }
            }

            if (theme >= 0) dialog.context.setTheme(theme)
            dialog.show()
        } else {
            view.text = null
        }
    }

    fun chooseOne(
        context: Context,
        title: String,
        list: Array<String>,
        values: Array<Int>,
        mutableDataString: MutableLiveData<String>,
        mutableDataInt: MutableLiveData<Int>,
        theme: Int = -1
    ) {
        if (list.isNotEmpty() && values.isNotEmpty()) {
            val dialog = MaterialAlertDialogBuilder(context)
            dialog.setTitle(title)
            dialog.setItems(list) { _, which ->
                run {
                    mutableDataString.postValue(list[which])
                    mutableDataInt.postValue(values[which])
                }
            }
            if (theme >= 0) dialog.context.setTheme(theme)
            dialog.show()
        }
    }

    fun chooseTwoWithNoRepeat(
        context: Context,
        title: String,
        list: Array<String>,
        viewMain: TextView,
        viewSecondary: TextView,
        chosenTypeMain: Index,
        chosenTypeSecondary: Index,
        theme: Int = -1
    ) {
        if (list.isNotEmpty()) {
            val dialog = MaterialAlertDialogBuilder(context)
            dialog.setTitle(title)
            dialog.setItems(list) { _, which ->
                run {
                    chosenTypeMain.value = which
                    viewMain.text = list[chosenTypeMain.value]


                    if (chosenTypeSecondary.value == chosenTypeMain.value) {
                        chosenTypeSecondary.value = if (which == 0) 1 else 0
                        viewSecondary.text = list[chosenTypeSecondary.value]
                    }
                }
            }
            if (theme >= 0) dialog.context.setTheme(theme)
            dialog.show()
        } else {
            viewMain.text = null
            viewSecondary.text = null
        }
    }

    fun chooseYesNo(
        context: Context,
        title: String,
        message: String,
        positiveText: String,
        negativeText: String,
        color: Int,
        actionOnPositive: () -> Unit
    ) {
        val dialog = MaterialAlertDialogBuilder(context, R.style.greenButtonsDialog)
            .setTitle(title)
            .setMessage(message)
            .setPositiveButton(
                positiveText
            ) { dialogInterface, _ ->
                run {
                    actionOnPositive.invoke()
                    dialogInterface.dismiss()
                }
            }
            .setNegativeButton(
                negativeText
            ) { dialogInterface, _ -> dialogInterface.dismiss() }
            .show()
        dialog.getButton(AlertDialog.BUTTON_POSITIVE)
            .setTextColor(ContextCompat.getColor(context, color))
        dialog.getButton(AlertDialog.BUTTON_NEGATIVE)
            .setTextColor(ContextCompat.getColor(context, color))
    }
}
