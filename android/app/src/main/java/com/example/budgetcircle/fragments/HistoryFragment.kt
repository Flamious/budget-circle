package com.example.budgetcircle.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.GridLayoutManager
import com.example.budgetcircle.R
import com.example.budgetcircle.databinding.FragmentHistoryBinding
import com.example.budgetcircle.viewmodel.BudgetData
/*import com.example.budgetcircle.viewmodel.items.BudgetType*/
import com.example.budgetcircle.viewmodel.items.HistoryAdapter
import com.example.budgetcircle.viewmodel.items.HistoryItem
import java.util.*

class HistoryFragment : Fragment() {
    lateinit var binding: FragmentHistoryBinding
    private val adapter = HistoryAdapter()
    private val budgetData: BudgetData by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentHistoryBinding.inflate(inflater)
        init()

        budgetData.operations.observe(this.viewLifecycleOwner, {
            adapter.setList(it)
        })
        return binding.root
    }

    private fun init() {
        binding.apply {
            historyList.layoutManager = GridLayoutManager(this@HistoryFragment.context, 1)
            historyList.adapter = adapter
        }
    }

    companion object {
        @JvmStatic
        fun newInstance() = HistoryFragment()
    }
}