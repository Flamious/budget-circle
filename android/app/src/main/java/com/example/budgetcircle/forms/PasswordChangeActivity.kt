package com.example.budgetcircle.forms

import android.content.res.ColorStateList
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import androidx.core.content.ContextCompat
import com.example.budgetcircle.R
import com.example.budgetcircle.databinding.ActivityPasswordChangeBinding
import com.example.budgetcircle.requests.Client
import com.example.budgetcircle.requests.UserApi
import com.example.budgetcircle.requests.models.ErrorResponse
import com.example.budgetcircle.settings.Settings
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class PasswordChangeActivity : AppCompatActivity() {
    lateinit var binding: ActivityPasswordChangeBinding
    lateinit var token: String
    private lateinit var service: UserApi

    private val appear: Animation by lazy {
        AnimationUtils.loadAnimation(
            this,
            R.anim.appear_short_anim
        )
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPasswordChangeBinding.inflate(layoutInflater)
        token = intent.extras?.getString("token")!!

        setTheme()
        setService()
        setButtons()
        setContentView(binding.root)
    }

    override fun onStart() {
        super.onStart()
        appear()
    }

    override fun onBackPressed() {
        exit()
    }

    //region Setting
    private fun setTheme() {
        val textPrimary: Int
        val textSecondary: Int
        val backgroundColor: Int
        val mainColor: Int

        binding.apply {
            if (Settings.isNight()) {
                textPrimary = ContextCompat.getColor(
                    this@PasswordChangeActivity,
                    R.color.light_grey
                )
                textSecondary = ContextCompat.getColor(
                    this@PasswordChangeActivity,
                    R.color.grey
                )
                backgroundColor = ContextCompat.getColor(
                    this@PasswordChangeActivity,
                    R.color.dark_grey
                )
                mainColor = ContextCompat.getColor(
                    this@PasswordChangeActivity,
                    R.color.darker_grey
                )

                changePasswordActivityHeaderLayout.backgroundTintList =
                    ColorStateList.valueOf(mainColor)
                changePasswordActivityAcceptButton.backgroundTintList =
                    ColorStateList.valueOf(mainColor)
                changePasswordActivityBackButton.backgroundTintList =
                    ColorStateList.valueOf(mainColor)
                changePasswordActivityLayout.setBackgroundColor(backgroundColor)

                changePasswordActivityOldPasswordTitle.setTextColor(textSecondary)
                changePasswordActivityNewPasswordTitle.setTextColor(textSecondary)
                changePasswordActivityConfirmationPasswordTitle.setTextColor(textSecondary)

                Settings.setFieldColor(
                    mainColor,
                    textPrimary,
                    textSecondary,
                    binding.changePasswordActivityOldPasswordField,
                    binding.changePasswordActivityNewPasswordField,
                    binding.changePasswordActivityConfirmationPasswordField
                )
            }
        }
    }

    private fun setButtons() {
        binding.changePasswordActivityAcceptButton.setOnClickListener {
            if (checkFields()) {
                accept()
            }
        }
        binding.changePasswordActivityBackButton.setOnClickListener {
            finish()
        }
    }

    private fun setService() {
        val url = resources.getString(R.string.url)
        service = Client.getClient(url).create(UserApi::class.java)
    }
    //endregion

    //region Methods
    private fun appear() {
        binding.changePasswordActivityScrollView.startAnimation(appear)
        binding.changePasswordActivityBackButton.startAnimation(appear)
        binding.changePasswordActivityHeaderLayout.startAnimation(appear)
    }

    private fun checkFields(): Boolean {
        var isValid = true

        binding.changePasswordActivityOldPasswordField.apply {
            error = null
            if (text.toString().isEmpty()) {
                error = resources.getString(R.string.empty_field)
                isValid = false
            }
        }
        binding.changePasswordActivityNewPasswordField.apply {
            error = null
            if (text.toString().isEmpty()) {
                error = resources.getString(R.string.empty_field)
                isValid = false
            }
        }
        binding.changePasswordActivityConfirmationPasswordField.apply {
            error = null
            if (text.toString().isEmpty()) {
                error = resources.getString(R.string.empty_field)
                isValid = false
            }
        }

        return isValid
    }

    private fun accept() {
        service.changePassword(
            token,
            binding.changePasswordActivityOldPasswordField.text.toString(),
            binding.changePasswordActivityNewPasswordField.text.toString(),
            binding.changePasswordActivityConfirmationPasswordField.text.toString()
        ).enqueue(object : Callback<Any> {
            override fun onFailure(call: Call<Any>, t: Throwable) {
            }

            override fun onResponse(call: Call<Any>, response: Response<Any>) {
                val gson = Gson()
                if (response.isSuccessful) {
                    print(resources.getString(R.string.password_changed))
                    finish()
                } else {
                    val errorBody = response.errorBody() ?: return
                    val type = object : TypeToken<ErrorResponse>() {}.type
                    val errorResponse: ErrorResponse? = gson.fromJson(errorBody.charStream(), type)
                    val errorMessage =
                        errorResponse?.error ?: resources.getString(R.string.wrongLoginOrPassword)

                    print(errorMessage)
                }
            }
        })
    }

    private fun exit() {
        finish()
    }
    //endregion
}