package com.example.budgetcircle.fragments.auth

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.example.budgetcircle.MainActivity
import com.example.budgetcircle.R
import com.example.budgetcircle.databinding.FragmentLoginBinding
import com.example.budgetcircle.requests.Client
import com.example.budgetcircle.requests.UserApi
import com.example.budgetcircle.requests.models.AuthResponse
import com.example.budgetcircle.requests.models.ErrorResponse
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import retrofit2.*

class LoginFragment : Fragment() {
    lateinit var binding: FragmentLoginBinding
    lateinit var service: UserApi

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentLoginBinding.inflate(inflater)
        setButtons()
        setService()
        loadToken()
        return binding.root
    }

    //region Setting
    private fun setButtons() {
        binding.signUpButton.setOnClickListener {
            openSignUp()
        }

        binding.loginAdmitButton.setOnClickListener {
            GlobalScope.launch {
                sendRequest()
            }
        }
    }

    private fun setService() {
        val url = resources.getString(R.string.url)
        service = Client.getClient(url).create(UserApi::class.java)
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

    private fun sendRequest() {
        service.signIn(
            binding.emailLoginText.text.toString(),
            binding.PasswordLoginText.text.toString()
        ).enqueue(object : Callback<Any> {
            override fun onFailure(call: Call<Any>, t: Throwable) {
                print(t.message)
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

    private fun loadToken() {
        val sharedPref = activity?.getPreferences(Context.MODE_PRIVATE) ?: return
        val token = sharedPref.getString(getString(R.string.token), null)

        if (token != null) {
            refreshToken(token)
        }
    }

    private fun refreshToken(token: String) {
        service.refreshToken(
            "Bearer $token"
        ).enqueue(object : Callback<Any> {
            override fun onFailure(call: Call<Any>, t: Throwable) {
                print(t.message)
            }

            override fun onResponse(call: Call<Any>, response: Response<Any>) {
                val gson = Gson()
                if (response.isSuccessful) {
                    val token =
                        gson.fromJson(gson.toJson(response.body()), AuthResponse::class.java).token

                    saveToken(token)
                    startApp(token)
                }
            }
        })
    }
    //endregion
}