package com.example.budgetcircle.settings

import android.widget.TextView
import com.github.mikephil.charting.charts.BarChart
import com.github.mikephil.charting.data.*
import com.github.mikephil.charting.highlight.Highlight
import com.github.mikephil.charting.listener.OnChartValueSelectedListener
import com.github.mikephil.charting.formatter.ValueFormatter

import com.github.mikephil.charting.components.XAxis

class BarChartSetter {
    companion object {
        private fun applyData(
            titles: Array<String>,
            values: Array<Double>,
            colors: ArrayList<Int>,
            chart: BarChart
        ) {
            val barEntries: ArrayList<BarEntry> = ArrayList()
            val xAxisLabel: ArrayList<String> = ArrayList()

            for (i in titles.indices) {
                barEntries.add(BarEntry(i.toFloat(), values[i].toFloat()))
                xAxisLabel.add(titles[i])
            }
            val barDataSet = BarDataSet(barEntries, "")
            barDataSet.colors = colors

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
            noEntries: Boolean = false
        ) {
            applyData(titles, values, colors, chart)

            chart.setTouchEnabled(!noEntries)
            chart.description.isEnabled = false
            chart.legend.isEnabled = false
            chart.highlightValues(null)
            chart.invalidate()
            chart.animateXY(1000, 1000)
        }
    }
}