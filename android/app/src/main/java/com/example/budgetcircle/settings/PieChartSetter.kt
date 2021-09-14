package com.example.budgetcircle.settings

import com.example.budgetcircle.R
import com.github.mikephil.charting.charts.PieChart
import com.github.mikephil.charting.data.PieData
import com.github.mikephil.charting.data.PieDataSet
import com.github.mikephil.charting.data.PieEntry

class PieChartSetter {
    companion object {
        private var sliceSpace = 3f
        private var holeRadius = 90f

        private fun applyData(titles: ArrayList<String>, values: ArrayList<Float>,
                      colors: ArrayList<Int>, chart: PieChart) {
            val pieEntries: ArrayList<PieEntry> = ArrayList()
            val label = ""

            for (i in 0 until titles.size) {
                pieEntries.add(PieEntry(values[i], titles[i]))
            }
            val pieDataSet = PieDataSet(pieEntries, label)
            pieDataSet.colors = colors
            pieDataSet.sliceSpace = sliceSpace

            val pieData = PieData(pieDataSet)
            pieData.setDrawValues(false)

            chart.data = pieData
        }

        fun setChart(titles: ArrayList<String>, values: ArrayList<Float>,
                     colors: ArrayList<Int>, chart: PieChart) {
            applyData(titles, values, colors, chart)

            chart.description.isEnabled = false
            chart.legend.isEnabled = false
            chart.holeRadius = holeRadius
            chart.setDrawEntryLabels(false)
            chart.invalidate()

        }

        fun changeData(titles: ArrayList<String>, values: ArrayList<Float>,
                       colors: ArrayList<Int>, chart: PieChart) {
            applyData(titles, values, colors, chart)
            chart.invalidate()
        }
    }
}