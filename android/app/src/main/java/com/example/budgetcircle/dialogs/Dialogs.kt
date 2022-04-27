package com.example.budgetcircle.dialogs

import android.app.AlertDialog
import android.content.Context
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import android.widget.TextView
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
        mutableDataString: MutableLiveData<String>,
        theme: Int = -1
    ) {
        if (list.isNotEmpty()) {
            val dialog = MaterialAlertDialogBuilder(context)
            dialog.setTitle(title)
            dialog.setItems(list) { _, which ->
                run {
                    mutableDataString.postValue(list[which])
                }
            }
            if (theme >= 0) dialog.context.setTheme(theme)
            dialog.show()
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

    fun chooseYesNo(
        context: Context,
        title: String,
        message: String,
        positiveText: String,
        negativeText: String,
        color: Int,
        actionOnPositive: () -> Unit,
        theme: Int = -1
    ) {
        val dialogSettings = MaterialAlertDialogBuilder(context, R.style.greenButtonsDialog)
            .setTitle(title)
            .setMessage(message)
            .setPositiveButton(
                positiveText
            ) { dialogInterface, _ ->
                run {
                    actionOnPositive.invoke()
                    dialogInterface.dismiss()
                }
            }.setNegativeButton(
                negativeText
            ) { dialogInterface, _ -> dialogInterface.dismiss() }

        if (theme >= 0) dialogSettings.context.setTheme(theme)

        val dialog = dialogSettings.show()

        dialog.getButton(AlertDialog.BUTTON_POSITIVE)
            .setTextColor(color)
        dialog.getButton(AlertDialog.BUTTON_NEGATIVE)
            .setTextColor(color)
    }
}
