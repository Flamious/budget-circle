package com.example.budgetcircle

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.budgetcircle.databinding.ActivityAuthBinding
import com.example.budgetcircle.fragments.auth.LoginFragment
import android.content.SharedPreferences
import android.content.res.Configuration
import com.example.budgetcircle.fragments.auth.LoginLoadingFragment


class AuthActivity : AppCompatActivity() {
    lateinit var binding: ActivityAuthBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAuthBinding.inflate(layoutInflater)
        setContentView(binding.root)
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
    //endregion
}