package com.example.budgetcircle.settings.charts

import com.github.mikephil.charting.charts.BarChart
import com.github.mikephil.charting.data.*
import com.github.mikephil.charting.formatter.ValueFormatter

import com.github.mikephil.charting.components.XAxis

class BarChartSetter {
    companion object {
        private fun applyData(
            titles: Array<String>,
            values: Array<Double>,
            colors: ArrayList<Int>,
            chart: BarChart,
            textColor: Int
        ) {
            val barEntries: ArrayList<BarEntry> = ArrayList()
            val xAxisLabel: ArrayList<String> = ArrayList()

            for (i in titles.indices) {
                barEntries.add(BarEntry(i.toFloat(), values[i].toFloat()))
                xAxisLabel.add(titles[i])
            }
            val barDataSet = BarDataSet(barEntries, "")
            barDataSet.colors = colors
            barDataSet.valueTextColor = textColor
            val barData = BarData(barDataSet)
            chart.data = barData

            chart.xAxis.apply {
                position = XAxis.XAxisPosition.BOTTOM_INSIDE

                val formatter: ValueFormatter = object : ValueFormatter() {
                    override fun getFormattedValue(value: Float): String {
                        val intValue = value.toInt()
                        return if(intValue < 0 || intValue >= xAxisLabel.size) "" else xAxisLabel[value.toInt()]
                    }
                }


                granularity = 1f

                valueFormatter = formatter
            }
        }

        fun setChart(
            titles: Array<String>,
            values: Array<Double>,
            colors: ArrayList<Int>,
            chart: BarChart,
            textColor: Int,
            noEntries: Boolean = false
        ) {
            applyData(titles, values, colors, chart, textColor)

            chart.setTouchEnabled(!noEntries)
            chart.description.isEnabled = false
            chart.legend.isEnabled = false
            chart.highlightValues(null)
            chart.axisLeft.textColor = textColor
            chart.axisRight.textColor = textColor
            chart.xAxis.textColor = textColor
            chart.invalidate()
        }
    }
}