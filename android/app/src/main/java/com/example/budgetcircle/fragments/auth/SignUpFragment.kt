package com.example.budgetcircle.fragments.auth

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.budgetcircle.R
import com.example.budgetcircle.databinding.FragmentLoginBinding
import com.example.budgetcircle.databinding.FragmentSignUpBinding

class SignUpFragment : Fragment() {
    lateinit var binding: FragmentSignUpBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentSignUpBinding.inflate(inflater)
        setButtons()
        return binding.root
    }

    //region Setting
    private fun setButtons() {
        binding.loginButton.setOnClickListener {
            openLogin()
        }
    }
    //endregion

    //region Methods
    private fun openLogin() {
        activity
            ?.supportFragmentManager
            ?.beginTransaction()
            ?.replace(R.id.authLayout, LoginFragment())
            ?.commit()
    }
    //endregion
}