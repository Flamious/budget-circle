package com.example.budgetcircle

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.fragment.app.Fragment
import com.example.budgetcircle.databinding.ActivityAuthBinding
import com.example.budgetcircle.fragments.auth.LoginFragment

class AuthActivity : AppCompatActivity() {
    lateinit var binding: ActivityAuthBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAuthBinding.inflate(layoutInflater)
        setContentView(binding.root)
        openLogin()
    }

    //region Setting
    private fun openLogin() {
        openFragment(LoginFragment())
    }
    //endregion

    //region Methods
    private fun openFragment(fragment: Fragment) {
        supportFragmentManager
            .beginTransaction()
            .replace(R.id.authLayout, fragment)
            .commit()
    }
    //endregion
}