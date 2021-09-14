package com.example.budgetcircle.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import com.example.budgetcircle.viewmodel.BudgetData
import com.example.budgetcircle.databinding.FragmentEarningsBinding

class EarningsFragment : Fragment() {
    lateinit var binding: FragmentEarningsBinding
    val budgetData: BudgetData by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentEarningsBinding.inflate(inflater)
        return binding.root
    }

    companion object {
        @JvmStatic
        fun newInstance() = EarningsFragment()
    }
}