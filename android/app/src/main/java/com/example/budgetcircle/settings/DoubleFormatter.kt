package com.example.budgetcircle.settings

class DoubleFormatter {
    companion object {
        public fun format(number: Double): Double {
            return String.format("%.2f", number).toDouble()
        }
    }
}