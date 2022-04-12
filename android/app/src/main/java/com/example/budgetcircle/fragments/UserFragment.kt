package com.example.budgetcircle.fragments

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import androidx.fragment.app.activityViewModels
import com.example.budgetcircle.AuthActivity
import com.example.budgetcircle.MainActivity
import com.example.budgetcircle.R
import com.example.budgetcircle.databinding.FragmentUserBinding
import com.example.budgetcircle.dialogs.Dialogs
import com.example.budgetcircle.forms.PasswordChangeActivity
import com.example.budgetcircle.viewmodel.BudgetDataApi

class UserFragment : Fragment() {
    lateinit var binding: FragmentUserBinding
    private val budgetDataApi: BudgetDataApi by activityViewModels()

    private val appear: Animation by lazy {
        AnimationUtils.loadAnimation(
            this.requireContext(),
            R.anim.appear_short_anim
        )
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentUserBinding.inflate(inflater)
        setButtons()
        return binding.root
    }

    override fun onStart() {
        super.onStart()
        appear()
    }

    //region Setting
    private fun setButtons() {
        binding.logoutButton.setOnClickListener {
            Dialogs().chooseYesNo(
                this.requireContext(),
                resources.getString(R.string.log_out),
                resources.getString(R.string.r_u_sure),
                resources.getString(R.string.yes),
                resources.getString(R.string.no),
                R.color.purple_main,
                ::logout
            )
        }

        binding.clearAccountsButton.setOnClickListener {
            Dialogs().chooseYesNo(
                this.requireContext(),
                resources.getString(R.string.clear_accounts),
                resources.getString(R.string.r_u_sure),
                resources.getString(R.string.yes),
                resources.getString(R.string.no),
                R.color.purple_main,
                ::clearBudgetTypes
            )
        }

        binding.deleteAllOperationsButton.setOnClickListener {
            Dialogs().chooseYesNo(
                this.requireContext(),
                resources.getString(R.string.delete_all_operations),
                resources.getString(R.string.r_u_sure),
                resources.getString(R.string.yes),
                resources.getString(R.string.no),
                R.color.purple_main,
                ::deleteAllOperations
            )
        }

        binding.changePasswordActivityButton.setOnClickListener {
            openChangePasswordActivity()
        }
    }
    //endregion

    //region Methods
    private fun appear() {
        binding.scrollView3.startAnimation(appear)
        binding.userFragmentHeaderLayout.startAnimation(appear)

    }

    private fun logout() {
        val intent = Intent(activity, AuthActivity::class.java)
        intent.putExtra(
            "logout",
            true
        )

        startActivity(intent)
    }

    private fun deleteAllOperations() {
        budgetDataApi.deleteAllOperations()
    }

    private fun clearBudgetTypes() {
        budgetDataApi.clearBudgetTypes()
    }

    private fun openChangePasswordActivity() {
        val intent = Intent(activity, PasswordChangeActivity::class.java)
        intent.putExtra(
            "token",
            MainActivity.Token
        )

        startActivity(intent)
    }
    //endregion
}