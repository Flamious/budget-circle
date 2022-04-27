package com.example.budgetcircle

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.budgetcircle.databinding.ActivityAuthBinding
import com.example.budgetcircle.fragments.auth.LoginLoadingFragment
import com.example.budgetcircle.settings.Settings


class AuthActivity : AppCompatActivity() {
    lateinit var binding: ActivityAuthBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAuthBinding.inflate(layoutInflater)
        setContentView(binding.root)
        getMode()
        checkLogout()
        openLoading()
    }

    override fun onBackPressed() {

    }

    //region Methods
    private fun checkLogout() {
        val isLogout = intent.getBooleanExtra("logout", false)
        if (isLogout) {
            val mySPrefs: SharedPreferences = getPreferences(Context.MODE_PRIVATE) ?: return
            val editor = mySPrefs.edit()
            editor.remove(getString(R.string.token))
            editor.apply()
        }
    }

    private fun openLoading() {
        supportFragmentManager
            .beginTransaction()
            .replace(R.id.authLayout, LoginLoadingFragment())
            .commit()
    }

    private fun getMode() {
        val prefs = getSharedPreferences(resources.getString(R.string.settings), MODE_PRIVATE)
        val mode = prefs.getInt(resources.getString(R.string.mode), Settings.DAY)

        Settings.mode = mode
    }
    //endregion
}