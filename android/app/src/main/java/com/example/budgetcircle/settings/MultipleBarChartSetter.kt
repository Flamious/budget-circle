package com.example.budgetcircle.settings

import com.github.mikephil.charting.charts.BarChart
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import com.github.mikephil.charting.formatter.ValueFormatter
import com.github.mikephil.charting.interfaces.datasets.IBarDataSet

class MultipleBarChartSetter {
    companion object {
        private fun applyData(
            titles: Array<String>,
            values1: Array<Double>,
            values2: Array<Double>,
            values3: Array<Double>,
            colors: ArrayList<Int>,
            chart: BarChart,
            xToBottom: Boolean = true
        ) {
            val barEntries1: ArrayList<BarEntry> = ArrayList()
            val barEntries2: ArrayList<BarEntry> = ArrayList()
            val barEntries3: ArrayList<BarEntry> = ArrayList()
            val xAxisLabel: ArrayList<String> = ArrayList()

            for (i in titles.indices) {
                barEntries1.add(BarEntry(i.toFloat(), values1[i].toFloat()))
                barEntries2.add(BarEntry(i.toFloat(), values2[i].toFloat()))
                barEntries3.add(BarEntry(i.toFloat(), values3[i].toFloat()))

                xAxisLabel.add(titles[i])
            }
            val barDataSet1 = BarDataSet(barEntries1, "")
            val barDataSet2 = BarDataSet(barEntries2, "")
            val barDataSet3 = BarDataSet(barEntries3, "")

            barDataSet1.color = colors[0]
            barDataSet2.color = colors[1]
            barDataSet3.color = colors[2]

            val barData = BarData(arrayListOf<IBarDataSet>(barDataSet1, barDataSet2, barDataSet3))
            chart.data = barData

            val groupSpace = 0.4f
            val barSpace = 0f
            val barWidth = 0.2f
            barData.barWidth = barWidth
            chart.xAxis.apply {
                position =
                    if (xToBottom) XAxis.XAxisPosition.BOTTOM_INSIDE else XAxis.XAxisPosition.TOP

                val formatter: ValueFormatter = object : ValueFormatter() {
                    override fun getFormattedValue(value: Float): String {
                        val intValue = value.toInt()
                        return if (intValue < 0 || intValue >= xAxisLabel.size) "" else xAxisLabel[value.toInt()]
                    }
                }


                granularity = 1f

                valueFormatter = formatter
            }

            chart.setVisibleXRange(5f, 5f)
            chart.groupBars(-0.5f, groupSpace, barSpace)
        }

        fun setChart(
            titles: Array<String>,
            values1: Array<Double>,
            values2: Array<Double>,
            values3: Array<Double>,
            colors: ArrayList<Int>,
            chart: BarChart,
            noEntries: Boolean = false,
            xToBottom: Boolean = true,
        ) {
            applyData(titles, values1, values2, values3, colors, chart, xToBottom)

            chart.setTouchEnabled(!noEntries)
            chart.description.isEnabled = false
            chart.legend.isEnabled = false
            chart.highlightValues(null)
            chart.invalidate()
            chart.animateXY(1000, 1000)
        }
    }
}