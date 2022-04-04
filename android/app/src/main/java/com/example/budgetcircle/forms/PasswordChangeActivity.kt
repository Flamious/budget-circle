package com.example.budgetcircle.forms

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.example.budgetcircle.R
import com.example.budgetcircle.databinding.ActivityPasswordChangeBinding
import com.example.budgetcircle.requests.Client
import com.example.budgetcircle.requests.UserApi
import com.example.budgetcircle.requests.models.ErrorResponse
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class PasswordChangeActivity : AppCompatActivity() {
    lateinit var binding: ActivityPasswordChangeBinding
    lateinit var token: String
    lateinit var service: UserApi

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPasswordChangeBinding.inflate(layoutInflater)
        token = intent.extras?.getString("token")!!

        setService()
        setButtons()
        setContentView(binding.root)
    }

    //region Setting
    private fun setButtons() {
        binding.changePasswordButton.setOnClickListener {
            if (checkFields()) {
                changePassword()
            }
        }
        binding.changePasswordBackButton.setOnClickListener {
            finish()
        }
    }

    private fun setService() {
        val url = resources.getString(R.string.url)
        service = Client.getClient(url).create(UserApi::class.java)
    }
    //endregion

    //region Methods
    private fun checkFields(): Boolean {
        var isValid = true

        binding.oldPasswordField.apply {
            error = null
            if (text.toString().isEmpty()) {
                error = resources.getString(R.string.empty_field)
                isValid = false
            }
        }
        binding.newPasswordField.apply {
            error = null
            if (text.toString().isEmpty()) {
                error = resources.getString(R.string.empty_field)
                isValid = false
            }
        }
        binding.confirmationPasswordField.apply {
            error = null
            if (text.toString().isEmpty()) {
                error = resources.getString(R.string.empty_field)
                isValid = false
            }
        }

        return isValid
    }

    private fun changePassword() {
        service.changePassword(
            token,
            binding.oldPasswordField.text.toString(),
            binding.newPasswordField.text.toString(),
            binding.confirmationPasswordField.text.toString()
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

    private fun print(message: String?) {
        if (message != null)
            Toast.makeText(this, message, Toast.LENGTH_LONG).show()
    }
    //endregion
}