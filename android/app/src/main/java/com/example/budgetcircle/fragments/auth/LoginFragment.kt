package com.example.budgetcircle.fragments.auth

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.budgetcircle.R
import com.example.budgetcircle.databinding.FragmentLoginBinding

class LoginFragment : Fragment() {
    lateinit var binding: FragmentLoginBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentLoginBinding.inflate(inflater)
        setButtons()
        return binding.root
    }

    //region Setting
    private fun setButtons() {
        binding.signUpButton.setOnClickListener {
            openSignUp()
        }
    }
    //endregion

    //region Methods
    private fun openSignUp() {
        activity
            ?.supportFragmentManager
            ?.beginTransaction()
            ?.replace(R.id.authLayout, SignUpFragment())
            ?.commit()
    }
    //endregion
}