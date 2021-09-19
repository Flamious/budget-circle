package com.example.budgetcircle.fragments.settings

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.FragmentManager
import com.example.budgetcircle.R
import com.example.budgetcircle.databinding.FragmentSettingsBinding
import com.example.budgetcircle.fragments.BudgetFragment
import com.example.budgetcircle.fragments.EarningsFragment

class SettingsFragment : Fragment() {
    lateinit var binding: FragmentSettingsBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentSettingsBinding.inflate(inflater)
        childFragmentManager
            .beginTransaction()
            .replace(R.id.settingsList, SettingsPreferances())
            .commit()
        return binding.root
    }

    companion object {
        @JvmStatic
        fun newInstance() = SettingsFragment()
    }
}
