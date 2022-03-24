package com.example.budgetcircle.settings

import java.lang.Exception
import java.text.DecimalFormat
import java.text.DecimalFormatSymbols

class DoubleFormatter {
    companion object {
        fun format(number: Double): Double {
            val twoDForm = DecimalFormat("#.##")
            val dfs = DecimalFormatSymbols()
            dfs.decimalSeparator = '.'
            twoDForm.decimalFormatSymbols = dfs

            return twoDForm.format(number).toDouble()
        }
    }
}