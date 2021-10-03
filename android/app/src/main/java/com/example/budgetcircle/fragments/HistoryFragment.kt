package com.example.budgetcircle.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.GridLayoutManager
import com.example.budgetcircle.R
import com.example.budgetcircle.databinding.FragmentHistoryBinding
import com.example.budgetcircle.viewmodel.HistoryAdapter
import com.example.budgetcircle.viewmodel.HistoryItem
import java.util.*

class HistoryFragment : Fragment() {
    lateinit var binding: FragmentHistoryBinding
    private val adapter = HistoryAdapter()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentHistoryBinding.inflate(inflater)
        init()
        return binding.root
    }

    private fun init() {
        binding.apply {
            historyList.layoutManager = GridLayoutManager(this@HistoryFragment.context, 1)
            historyList.adapter = adapter
        }
        for (i in 1..33) {
            adapter.addItem(
                HistoryItem(
                    i,
                    i * 1000 + 33 * (i - 1) - 100 * (i + 2),
                    "Operation $i",
                    "type ${i % 5}",
                    Date(),
                    resources.getColor(
                        when (i % 3) {
                            0 -> R.color.red_button
                            1 -> R.color.blue_button
                            else -> R.color.green_button
                        }
                    ),
                    i % 5 == 0
                )
            )
        }
    }

    companion object {
        @JvmStatic
        fun newInstance() = HistoryFragment()
    }
}