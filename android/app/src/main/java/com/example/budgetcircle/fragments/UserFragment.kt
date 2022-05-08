package com.example.budgetcircle.fragments

import android.content.Intent
import android.content.res.ColorStateList
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.example.budgetcircle.AuthActivity
import com.example.budgetcircle.MainActivity
import com.example.budgetcircle.R
import com.example.budgetcircle.databinding.FragmentUserBinding
import com.example.budgetcircle.dialogs.Dialogs
import com.example.budgetcircle.forms.PasswordChangeActivity
import com.example.budgetcircle.settings.Settings
import com.example.budgetcircle.viewmodel.BudgetCircleData


class UserFragment : Fragment() {
    lateinit var binding: FragmentUserBinding
    private val budgetCircleData: BudgetCircleData by activityViewModels()

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
        applyDayNight()
        return binding.root
    }

    override fun onStart() {
        super.onStart()
        appear()
    }

    //region Setting
    private fun setButtons() {
        val background: Int
        val buttonColor: Int

        if (Settings.mode == Settings.NIGHT) {
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
        budgetCircleData.deleteAllOperations()
    }

    private fun clearBudgetTypes() {
        budgetCircleData.clearBudgetTypes()
    }

    private fun openChangePasswordActivity() {
        val intent = Intent(activity, PasswordChangeActivity::class.java)
        intent.putExtra(
            "token",
            Settings.token
        )

        startActivity(intent)
    }

    private fun changeMode() {
        Settings.changeMode()
        (activity as MainActivity).applyDayNight()
        applyDayNight()
        setButtons()

        val prefs = activity?.getSharedPreferences(
            resources.getString(R.string.settings),
            AppCompatActivity.MODE_PRIVATE
        )
        val editor = prefs!!.edit()
        editor.putInt(resources.getString(R.string.mode), Settings.mode)
        editor.apply()
    }

    private fun applyDayNight() {
        val textPrimary: Int
        val backgroundColor: Int
        val mainColor: Int
        val textSecondary1: Int
        val textSecondary2: Int

        binding.apply {
            if (Settings.isDay()) {
                textPrimary = ContextCompat.getColor(
                    this@UserFragment.requireContext(),
                    R.color.text_secondary
                )
                mainColor =
                    ContextCompat.getColor(this@UserFragment.requireContext(), R.color.purple_main)
                backgroundColor =
                    ContextCompat.getColor(this@UserFragment.requireContext(), R.color.light_grey)
                textSecondary1 =
                    ContextCompat.getColor(this@UserFragment.requireContext(), R.color.blue_main)
                textSecondary2 =
                    ContextCompat.getColor(this@UserFragment.requireContext(), R.color.red_main)
                userFragmentModeButton.setImageResource(R.drawable.ic_dark_mode)
            } else {
                mainColor =
                    ContextCompat.getColor(this@UserFragment.requireContext(), R.color.darker_grey)
                textPrimary =
                    ContextCompat.getColor(this@UserFragment.requireContext(), R.color.grey)
                backgroundColor =
                    ContextCompat.getColor(this@UserFragment.requireContext(), R.color.dark_grey)
                textSecondary1 =
                    ContextCompat.getColor(
                        this@UserFragment.requireContext(),
                        R.color.light_grey
                    )
                textSecondary2 =
                    ContextCompat.getColor(
                        this@UserFragment.requireContext(),
                        R.color.light_grey
                    )
                userFragmentModeButton.setImageResource(R.drawable.ic_day_mode)
            }

            userFragmentOperationsTitle.setTextColor(textPrimary)
            userFragmentOtherTitle.setTextColor(textPrimary)
            userFragmentPasswordTitle.setTextColor(textPrimary)

            userFragmentChangePasswordActivityButton.setTextColor(textSecondary1)
            userFragmentDeleteAllOperationsButton.setTextColor(textSecondary1)
            userFragmentClearAccountsButton.setTextColor(textSecondary1)
            userFragmentLogoutButton.setTextColor(textSecondary2)

            userFragmentLayout.backgroundTintList = ColorStateList.valueOf(backgroundColor)
            userFragmentHeaderLayout.backgroundTintList = ColorStateList.valueOf(mainColor)
            userFragmentModeButton.backgroundTintList = ColorStateList.valueOf(mainColor)
        }
    }
    //endregion
}