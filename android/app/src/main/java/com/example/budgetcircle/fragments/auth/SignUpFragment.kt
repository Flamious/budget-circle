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
import com.example.budgetcircle.databinding.FragmentSignUpBinding
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

        binding.signUpAdmitButton.setOnClickListener {
            GlobalScope.launch {
                sendRequest()
            }
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

    private fun sendRequest() {
        val url = resources.getString(R.string.url)
        val service = Client.getClient(url).create(UserApi::class.java)

        service.signUp(
            binding.emailSignUpText.text.toString(),
            binding.passwordSignUpText.text.toString(),
            binding.confirmPasswordSignUpText.text.toString()
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
            token)

        startActivity(intent)
    }
    //endregion
}