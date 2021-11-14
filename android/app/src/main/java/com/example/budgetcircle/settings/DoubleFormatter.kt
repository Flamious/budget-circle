package com.example.budgetcircle.settings

class DoubleFormatter {
    companion object {
        fun format(number: Double): Double {
            return String.format("%.2f", number).replace(",", ".").toDouble()
        }
    }
}