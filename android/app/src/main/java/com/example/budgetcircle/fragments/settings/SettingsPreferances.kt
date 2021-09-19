package com.example.budgetcircle.fragments.settings

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.preference.PreferenceFragmentCompat
import com.example.budgetcircle.R
import com.example.budgetcircle.databinding.FragmentSettingsBinding

class SettingsPreferances : PreferenceFragmentCompat() {

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        addPreferencesFromResource(R.xml.preferences)
    }

    companion object {
        @JvmStatic
        fun newInstance() = SettingsFragment()
    }
}