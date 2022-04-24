package com.example.budgetcircle.fragments.auth

import android.content.Context
import android.content.Intent
import android.content.res.ColorStateList
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat
import com.example.budgetcircle.AuthActivity
import com.example.budgetcircle.MainActivity
import com.example.budgetcircle.R
import com.example.budgetcircle.databinding.FragmentSignUpBinding
import com.example.budgetcircle.fragments.UserFragment
import com.example.budgetcircle.requests.Client
import com.example.budgetcircle.requests.UserApi
import com.example.budgetcircle.requests.models.AuthResponse
import com.example.budgetcircle.requests.models.ErrorResponse
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class SignUpFragment : Fragment() {
    lateinit var binding: FragmentSignUpBinding
    private lateinit var service: UserApi

    private val appear: Animation by lazy {
        AnimationUtils.loadAnimation(
            this.context,
            R.anim.appear_short_anim
        )
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentSignUpBinding.inflate(inflater)
        setButtons()
        setService()
        setTheme()
        return binding.root
    }

    override fun onStart() {
        super.onStart()
        setAnimation()
    }

    //region Setting
    private fun setTheme() {
        val textPrimary: Int
        val textSecondary: Int
        val backgroundColor: Int
        val mainColor: Int

        binding.apply {
            if (AuthActivity.mode == UserFragment.NIGHT) {
                textPrimary = ContextCompat.getColor(
                    this@SignUpFragment.requireContext(),
                    R.color.light_grey
                )
                textSecondary = ContextCompat.getColor(
                    this@SignUpFragment.requireContext(),
                    R.color.grey
                )
                backgroundColor = ContextCompat.getColor(
                    this@SignUpFragment.requireContext(),
                    R.color.dark_grey
                )
                mainColor = ContextCompat.getColor(
                    this@SignUpFragment.requireContext(),
                    R.color.darker_grey
                )


                signUpFragmentHeaderLayout.backgroundTintList =
                    ColorStateList.valueOf(mainColor)
                signUpFragmentAdmitButton.backgroundTintList =
                    ColorStateList.valueOf(mainColor)
                signUpFragmentLayout.setBackgroundColor(backgroundColor)

                signUpFragmentEmailTitle.setTextColor(textSecondary)
                signUpFragmentPasswordTitle.setTextColor(textSecondary)
                signUpFragmentConfirmPasswordTitle.setTextColor(textSecondary)
                signUpFragmentLoginButton.setTextColor(textPrimary)

                setFieldColor(
                    binding.signUpFragmentEmailField,
                    mainColor,
                    textPrimary,
                    textSecondary
                )
                setFieldColor(
                    binding.signUpFragmentPasswordField,
                    mainColor,
                    textPrimary,
                    textSecondary
                )
                setFieldColor(
                    binding.signUpFragmentConfirmPasswordField,
                    mainColor,
                    textPrimary,
                    textSecondary
                )
            }
        }
    }

    private fun setFieldColor(
        editText: TextView,
        mainColor: Int,
        textColor: Int,
        textSecondary: Int
    ) {
        editText.backgroundTintList = ColorStateList.valueOf(mainColor)
        editText.highlightColor = mainColor
        editText.setLinkTextColor(mainColor)
        editText.setTextColor(textColor)
        editText.setHintTextColor(textSecondary)
    }

    private fun setButtons() {
        binding.signUpFragmentLoginButton.setOnClickListener {
            openLogin()
        }

        binding.signUpFragmentAdmitButton.setOnClickListener {
            if (checkFields()) {

                startLoading()
                GlobalScope.launch {
                    sendRequest()
                }
            }
        }
    }

    private fun setService() {
        val url = resources.getString(R.string.url)
        service = Client.getClient(url).create(UserApi::class.java)
    }

    private fun setAnimation() {
        binding.signUpFragmentHeaderLayout.startAnimation(appear)
        binding.signUpFragmentAdmitButton.startAnimation(appear)
        binding.signUpFragmentScrollView.startAnimation(appear)
    }
    //endregion

    //region Methods
    private fun checkFields(): Boolean {
        var isValid = true
        binding.signUpFragmentEmailField.apply {
            error = null
            if (text.isNullOrBlank()) {
                error = resources.getString(R.string.empty_field)
                isValid = false
            } else if (!android.util.Patterns.EMAIL_ADDRESS.matcher(text).matches()) {
                error = resources.getString(R.string.email_format_string)
                isValid = false
            }
        }

        binding.signUpFragmentPasswordField.apply {
            error = null
            if (text.isNullOrBlank()) {
                error = resources.getString(R.string.empty_field)
                isValid = false
            }
        }

        binding.signUpFragmentConfirmPasswordField.apply {
            error = null
            if (text.isNullOrBlank()) {
                error = resources.getString(R.string.empty_field)
                isValid = false
            }
        }

        return isValid
    }

    private fun startLoading() {
        binding.signUpFragmentLoadingLayout.visibility = View.VISIBLE

        binding.signUpFragmentEmailField.isEnabled = false
        binding.signUpFragmentPasswordField.isEnabled = false
        binding.signUpFragmentConfirmPasswordField.isEnabled = false
        binding.signUpFragmentAdmitButton.isEnabled = false
        binding.signUpFragmentLoginButton.isClickable = false
    }

    private fun stopLoading() {
        binding.signUpFragmentLoadingLayout.visibility = View.GONE

        binding.signUpFragmentEmailField.isEnabled = true
        binding.signUpFragmentPasswordField.isEnabled = true
        binding.signUpFragmentConfirmPasswordField.isEnabled = true
        binding.signUpFragmentAdmitButton.isEnabled = true
        binding.signUpFragmentLoginButton.isClickable = true
    }

    private fun openLogin() {
        binding.signUpFragmentLayout.clearAnimation()
        activity
            ?.supportFragmentManager
            ?.beginTransaction()
            ?.replace(R.id.authLayout, LoginFragment())
            ?.commit()
    }

    private fun sendRequest() {
        service.signUp(
            binding.signUpFragmentEmailField.text.toString(),
            binding.signUpFragmentPasswordField.text.toString(),
            binding.signUpFragmentConfirmPasswordField.text.toString()
        ).enqueue(object : Callback<Any> {
            override fun onFailure(call: Call<Any>, t: Throwable) {
                print(t.message)
                stopLoading()
            }

            override fun onResponse(call: Call<Any>, response: Response<Any>) {
                val gson = Gson()
                if (response.isSuccessful) {
                    val token =
                        gson.fromJson(gson.toJson(response.body()), AuthResponse::class.java).token

                    saveToken(token)
                    startApp(token)
                } else {
                    val errorBody = response.errorBody() ?: return
                    val type = object : TypeToken<ErrorResponse>() {}.type
                    val errorResponse: ErrorResponse? = gson.fromJson(errorBody.charStream(), type)
                    val errorMessage =
                        errorResponse?.error ?: resources.getString(R.string.wrongLoginOrPassword)

                    print(errorMessage)
                    stopLoading()
                }
            }
        })
    }

    private fun print(message: String?) {
        if (message != null)
            Toast.makeText(this.requireContext(), message, Toast.LENGTH_LONG).show()
    }

    private fun saveToken(token: String) {
        val sharedPref = activity?.getPreferences(Context.MODE_PRIVATE) ?: return
        with(sharedPref.edit()) {
            putString(getString(R.string.token), token)
            apply()
        }
    }

    private fun startApp(token: String) {
        val intent = Intent(activity, MainActivity::class.java)
        intent.putExtra(
            "token",
            token
        )

        startActivity(intent)
    }
    //endregion
}