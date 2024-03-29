package com.example.budgetcircle.settings.charts

import com.github.mikephil.charting.charts.PieChart
import com.github.mikephil.charting.data.PieData
import com.github.mikephil.charting.data.PieDataSet
import com.github.mikephil.charting.data.PieEntry
import android.widget.TextView
import com.example.budgetcircle.settings.DoubleFormatter
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.highlight.Highlight
import com.github.mikephil.charting.listener.OnChartValueSelectedListener

class PieChartSetter {
    companion object {
        private var sliceSpace = 3f
        private var holeRadius = 90f

        private fun applyData(
            titles: Array<String>,
            values: Array<Double>,
            colors: ArrayList<Int>,
            chart: PieChart,
            isFull: Boolean
        ) {
            val pieEntries: ArrayList<PieEntry> = ArrayList()

            for (i in titles.indices) {
                pieEntries.add(PieEntry(values[i].toFloat(), titles[i]))
            }
            val pieDataSet = PieDataSet(pieEntries, "")
            pieDataSet.colors = colors
            pieDataSet.sliceSpace = if (!isFull) sliceSpace else 0f

            val pieData = PieData(pieDataSet)
            pieData.setDrawValues(false)

            chart.data = pieData

        }

        fun setChart(
            titles: Array<String>,
            values: Array<Double>,
            colors: ArrayList<Int>,
            sum: Double,
            label: String,
            chart: PieChart,
            sumTextView: TextView,
            labelTextView: TextView,
            holeColor: Int,
            isFull: Boolean = false,
            noEntries: Boolean = false
        ) {
            applyData(titles, values, colors, chart, isFull)

            chart.setTouchEnabled(!noEntries)
            chart.description.isEnabled = false
            chart.legend.isEnabled = false
            chart.holeRadius = if (!isFull) holeRadius else 0f
            chart.setHoleColor(holeColor)
            chart.setDrawEntryLabels(false)
            chart.setTransparentCircleAlpha(0)
            sumTextView.text = DoubleFormatter.formatString(sum)
            labelTextView.text = label
            chart.highlightValues(null)
            chart.setOnChartValueSelectedListener(
                ChartListener(
                    sumTextView,
                    labelTextView,
                    sum,
                    label
                )
            )
            chart.invalidate()
            chart.animateXY(1000, 1000)
        }

        private class ChartListener(
            val sumTextView: TextView,
            val labelTextView: TextView,
            val sum: Double,
            val label: String
        ) : OnChartValueSelectedListener {

            override fun onValueSelected(e: Entry?, h: Highlight?) {
                sumTextView.text = DoubleFormatter.formatString(e?.y!!.toDouble())
                labelTextView.text = (e as PieEntry).label
            }

            override fun onNothingSelected() {
                sumTextView.text = DoubleFormatter.formatString(sum)
                labelTextView.text = label
            }

        }
    }
}