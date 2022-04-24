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
import androidx.lifecycle.lifecycleScope
import com.example.budgetcircle.AuthActivity
import com.example.budgetcircle.MainActivity
import com.example.budgetcircle.R
import com.example.budgetcircle.databinding.FragmentLoginBinding
import com.example.budgetcircle.fragments.UserFragment
import com.example.budgetcircle.requests.Client
import com.example.budgetcircle.requests.UserApi
import com.example.budgetcircle.requests.models.AuthResponse
import com.example.budgetcircle.requests.models.ErrorResponse
import com.example.budgetcircle.viewmodel.BudgetDataApi
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import retrofit2.*

class LoginFragment : Fragment() {
    lateinit var binding: FragmentLoginBinding
    lateinit var service: UserApi

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
        binding = FragmentLoginBinding.inflate(inflater)
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
                    this@LoginFragment.requireContext(),
                    R.color.light_grey
                )
                textSecondary = ContextCompat.getColor(
                    this@LoginFragment.requireContext(),
                    R.color.grey
                )
                backgroundColor = ContextCompat.getColor(
                    this@LoginFragment.requireContext(),
                    R.color.dark_grey
                )
                mainColor = ContextCompat.getColor(
                    this@LoginFragment.requireContext(),
                    R.color.darker_grey
                )


                loginFragmentHeaderLayout.backgroundTintList =
                    ColorStateList.valueOf(mainColor)
                loginFragmentAdmitButton.backgroundTintList =
                    ColorStateList.valueOf(mainColor)
                loginFragmentLayout.setBackgroundColor(backgroundColor)

                loginFragmentEmailTitle.setTextColor(textSecondary)
                loginFragmentPasswordTitle.setTextColor(textSecondary)
                loginFragmentSignUpButton.setTextColor(textPrimary)

                setFieldColor(
                    binding.loginFragmentEmailField,
                    mainColor,
                    textPrimary,
                    textSecondary
                )
                setFieldColor(
                    binding.loginFragmentPasswordField,
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
        binding.loginFragmentSignUpButton.setOnClickListener {
            openSignUp()
        }

        binding.loginFragmentAdmitButton.setOnClickListener {
            if (checkFields()) {
                startLoading()
                sendRequest()
            }
        }
    }

    private fun setService() {
        val url = resources.getString(R.string.url)
        service = Client.getClient(url).create(UserApi::class.java)
    }

    private fun setAnimation() {
        binding.loginFragmentHeaderLayout.startAnimation(appear)
        binding.loginFragmentAdmitButton.startAnimation(appear)
        binding.loginFragmentScrollView.startAnimation(appear)
    }
    //endregion

    //region Methods
    private fun checkFields(): Boolean {
        var isValid = true
        binding.loginFragmentEmailField.apply {
            error = null
            if (text.isNullOrBlank()) {
                error = resources.getString(R.string.empty_field)
                isValid = false
            } else if (!android.util.Patterns.EMAIL_ADDRESS.matcher(text).matches()) {
                error = resources.getString(R.string.email_format_string)
                isValid = false
            }
        }

        binding.loginFragmentPasswordField.apply {
            error = null
            if (text.isNullOrBlank()) {
                error = resources.getString(R.string.empty_field)
                isValid = false
            }
        }

        return isValid
    }

    private fun startLoading() {
        binding.loginFragmentLoadingLayout.visibility = View.VISIBLE

        binding.loginFragmentEmailField.isEnabled = false
        binding.loginFragmentPasswordField.isEnabled = false
        binding.loginFragmentAdmitButton.isEnabled = false
        binding.loginFragmentSignUpButton.isClickable = false
    }

    private fun stopLoading() {
        binding.loginFragmentLoadingLayout.visibility = View.GONE

        binding.loginFragmentEmailField.isEnabled = true
        binding.loginFragmentPasswordField.isEnabled = true
        binding.loginFragmentAdmitButton.isEnabled = true
        binding.loginFragmentSignUpButton.isClickable = true
    }

    private fun openSignUp() {
        activity
            ?.supportFragmentManager
            ?.beginTransaction()
            ?.replace(R.id.authLayout, SignUpFragment())
            ?.commit()
    }

    private fun sendRequest() = lifecycleScope.launch(Dispatchers.IO) {
        service.signIn(
            binding.loginFragmentEmailField.text.toString(),
            binding.loginFragmentPasswordField.text.toString()
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
                        errorResponse?.error

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