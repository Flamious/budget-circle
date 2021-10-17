package com.example.budgetcircle.lists

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.ActivityResultLauncher
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.GridLayoutManager
import com.example.budgetcircle.R
import com.example.budgetcircle.databinding.FragmentBudgetBinding
import com.example.budgetcircle.databinding.FragmentBudgetTypeListBinding
import com.example.budgetcircle.databinding.FragmentEarningsBinding
import com.example.budgetcircle.fragments.BudgetFragment
import com.example.budgetcircle.viewmodel.BudgetData
import com.example.budgetcircle.viewmodel.items.BudgetTypeAdapter

class BudgetTypeListFragment : Fragment() {

    lateinit var binding: FragmentBudgetTypeListBinding
    private val adapter = BudgetTypeAdapter()
    private val budgetData: BudgetData by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentBudgetTypeListBinding.inflate(inflater)
        setButtons()
        init()

        budgetData.budgetTypes.observe(this.viewLifecycleOwner, {
            adapter.setList(it)
        })
        return binding.root
    }

    private fun setButtons() {
        binding.budgetTypesBackButton2.setOnClickListener {
            exit()
        }
    }

    private fun init() {
        binding.apply {
            budgetTypelist.layoutManager = GridLayoutManager(this@BudgetTypeListFragment.context, 1)
            budgetTypelist.adapter = adapter
        }
    }

    private fun exit() {
        activity
            ?.supportFragmentManager
            ?.beginTransaction()
            ?.replace(R.id.fragmentPanel, BudgetFragment())
            ?.disallowAddToBackStack()
            ?.commit()
    }

    companion object {
        @JvmStatic
        fun newInstance() = BudgetTypeListFragment()
    }
}