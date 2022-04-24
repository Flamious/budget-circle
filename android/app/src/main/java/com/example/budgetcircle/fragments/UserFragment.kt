package com.example.budgetcircle.fragments

import android.content.Intent
import android.content.res.ColorStateList
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.fragment.app.activityViewModels
import com.example.budgetcircle.AuthActivity
import com.example.budgetcircle.MainActivity
import com.example.budgetcircle.R
import com.example.budgetcircle.databinding.FragmentUserBinding
import com.example.budgetcircle.dialogs.Dialogs
import com.example.budgetcircle.forms.PasswordChangeActivity
import com.example.budgetcircle.viewmodel.BudgetDataApi
import android.content.SharedPreferences




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
        setObservations()
        setButtons()
        applyDayNight(BudgetDataApi.mode.value!!)
        return binding.root
    }

    override fun onStart() {
        super.onStart()
        appear()
    }

    //region Setting
    private fun setObservations() {
        BudgetDataApi.mode.observe(this.viewLifecycleOwner) {
            applyDayNight(it)

            val prefs = activity?.getSharedPreferences(resources.getString(R.string.settings),
                AppCompatActivity.MODE_PRIVATE
            )
            val editor = prefs!!.edit()
            editor.putInt(resources.getString(R.string.mode), it)
            editor.apply()

            setButtons()
        }
    }

    private fun setButtons() {
        val background: Int
        val buttonColor: Int

        if (BudgetDataApi.mode.value!! == UserFragment.NIGHT) {
            background = R.style.darkEdgeEffect
            buttonColor = ContextCompat.getColor(this.requireContext(), R.color.darker_grey)
        } else {
            background = -1
            buttonColor = ContextCompat.getColor(this.requireContext(), R.color.purple_main)
        }

        binding.userFragmentModeButton.setOnClickListener {
            changeMode()
            appear()
        }

        binding.userFragmentLogoutButton.setOnClickListener {
            Dialogs().chooseYesNo(
                this.requireContext(),
                resources.getString(R.string.log_out),
                resources.getString(R.string.r_u_sure),
                resources.getString(R.string.yes),
                resources.getString(R.string.no),
                buttonColor,
                ::logout,
                background
            )
        }

        binding.userFragmentClearAccountsButton.setOnClickListener {
            Dialogs().chooseYesNo(
                this.requireContext(),
                resources.getString(R.string.clear_accounts),
                resources.getString(R.string.r_u_sure),
                resources.getString(R.string.yes),
                resources.getString(R.string.no),
                buttonColor,
                ::clearBudgetTypes,
                background
            )
        }

        binding.userFragmentDeleteAllOperationsButton.setOnClickListener {
            Dialogs().chooseYesNo(
                this.requireContext(),
                resources.getString(R.string.delete_all_operations),
                resources.getString(R.string.r_u_sure),
                resources.getString(R.string.yes),
                resources.getString(R.string.no),
                buttonColor,
                ::deleteAllOperations,
                background
            )
        }

        binding.userFragmentChangePasswordActivityButton.setOnClickListener {
            openChangePasswordActivity()
        }
    }
    //endregion

    //region Methods
    private fun appear() {
        binding.userFragmentScrollView.startAnimation(appear)
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

    private fun changeMode() {
        BudgetDataApi.mode.postValue(
            if (BudgetDataApi.mode.value!! == DAY) {
                NIGHT
            } else {
                DAY
            }
        )
    }

    private fun applyDayNight(mode: Int) {
        val textColor: Int
        val backgroundColor: Int
        val headerBackgroundColor: Int
        val textSecondary1: Int
        val textSecondary2: Int

        binding.apply {
            if (mode == DAY) {
                textColor = ContextCompat.getColor(
                    this@UserFragment.requireContext(),
                    R.color.text_secondary
                )
                headerBackgroundColor =
                    ContextCompat.getColor(this@UserFragment.requireContext(), R.color.purple_main)
                backgroundColor =
                    ContextCompat.getColor(this@UserFragment.requireContext(), R.color.light_grey)
                textSecondary1 =
                    ContextCompat.getColor(this@UserFragment.requireContext(), R.color.blue_main)
                textSecondary2 = ContextCompat.getColor(this@UserFragment.requireContext(), R.color.red_main)
                userFragmentModeButton.setImageResource(R.drawable.ic_dark_mode)
            } else {
                headerBackgroundColor =
                    ContextCompat.getColor(this@UserFragment.requireContext(), R.color.darker_grey)
                textColor =
                    ContextCompat.getColor(this@UserFragment.requireContext(), R.color.grey)
                backgroundColor =
                    ContextCompat.getColor(this@UserFragment.requireContext(), R.color.dark_grey)
                textSecondary1 =
                    ContextCompat.getColor(
                        this@UserFragment.requireContext(),
                        R.color.light_grey
                    )
                textSecondary2 = ContextCompat.getColor(
                    this@UserFragment.requireContext(),
                    R.color.light_grey
                )
                userFragmentModeButton.setImageResource(R.drawable.ic_day_mode)
            }

            userFragmentOperationsTitle.setTextColor(textColor)
            userFragmentOtherTitle.setTextColor(textColor)
            userFragmentPasswordTitle.setTextColor(textColor)

            userFragmentChangePasswordActivityButton.setTextColor(textSecondary1)
            userFragmentDeleteAllOperationsButton.setTextColor(textSecondary1)
            userFragmentClearAccountsButton.setTextColor(textSecondary1)
            userFragmentLogoutButton.setTextColor(textSecondary2)

            userFragmentLayout.backgroundTintList = ColorStateList.valueOf(backgroundColor)
            userFragmentHeaderLayout.backgroundTintList = ColorStateList.valueOf(headerBackgroundColor)
            userFragmentModeButton.backgroundTintList = ColorStateList.valueOf(headerBackgroundColor)
        }
    }
    //endregion

    companion object {
        const val DAY = 0
        const val NIGHT = 1
    }
}