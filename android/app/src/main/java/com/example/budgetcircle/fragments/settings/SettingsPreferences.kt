package com.example.budgetcircle.fragments.settings

import android.os.Bundle
import androidx.preference.PreferenceFragmentCompat
import com.example.budgetcircle.R

class SettingsPreferences : PreferenceFragmentCompat() {

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        addPreferencesFromResource(R.xml.preferences)
    }
}