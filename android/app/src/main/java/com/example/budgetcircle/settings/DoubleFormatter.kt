package com.example.budgetcircle.settings

import java.text.DecimalFormat
import java.text.DecimalFormatSymbols

class DoubleFormatter {
    companion object {
        fun format(number: Double): Double {
            val twoDForm = DecimalFormat("#.##")
            twoDForm.maximumIntegerDigits = 20
            val dfs = DecimalFormatSymbols()
            dfs.decimalSeparator = '.'
            twoDForm.decimalFormatSymbols = dfs

            return twoDForm.format(number).toDouble()
        }

        fun formatString(number: Double): String {
            val twoDForm = DecimalFormat("#.##")
            twoDForm.maximumIntegerDigits = 20
            twoDForm.maximumFractionDigits = 2
            twoDForm.minimumFractionDigits = 2
            val dfs = DecimalFormatSymbols()
            dfs.decimalSeparator = '.'
            twoDForm.decimalFormatSymbols = dfs

            return twoDForm.format(number).toString()
        }
    }
}