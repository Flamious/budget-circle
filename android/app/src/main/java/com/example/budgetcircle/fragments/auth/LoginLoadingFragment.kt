package com.example.budgetcircle.fragments.auth

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import androidx.lifecycle.lifecycleScope
import com.example.budgetcircle.MainActivity
import com.example.budgetcircle.R
import com.example.budgetcircle.databinding.FragmentLoginLoadingBinding
import com.example.budgetcircle.requests.Client
import com.example.budgetcircle.requests.UserApi
import com.example.budgetcircle.requests.models.AuthResponse
import com.example.budgetcircle.settings.PieChartSetter
import com.google.gson.Gson
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import kotlin.random.Random

class LoginLoadingFragment : Fragment() {
    lateinit var binding: FragmentLoginLoadingBinding
    lateinit var service: UserApi
    private var loading: Long = 5000;

    private val appear: Animation by lazy {
        AnimationUtils.loadAnimation(
            this.context,
            R.anim.appear_anim
        )
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentLoginLoadingBinding.inflate(inflater)
        setService()
        return binding.root
    }

    override fun onStart() {
        super.onStart()
        setChart()
        loadToken().start()
    }

    //region Setting
    private fun setService() {
        val url = resources.getString(R.string.url)
        service = Client.getClient(url).create(UserApi::class.java)
    }

    private fun setChart() {
        binding.titleView.startAnimation(appear)

        val size = 4
        val values = Array(size) { Random.nextDouble(10.0, 50.0) }
        val titles = Array(size) { "" }
        val colors = resources.getIntArray(R.array.budget_colors).toCollection(ArrayList())
            PieChartSetter.setChart(
                titles,
                values,
                colors,
                0.0,
                resources.getString(R.string.app_name),
                binding.loadingPieChart,
                binding.sumTextView,
                binding.titleView,
                false
            )
    }
    //endregion

    //region Methods
    private fun openLogin() = lifecycleScope.launch(Dispatchers.IO) {
        Thread.sleep(loading)
        activity
            ?.supportFragmentManager
            ?.beginTransaction()
            ?.replace(R.id.authLayout, LoginFragment())
            ?.commit()
    }

    private fun saveToken(token: String) {
        val sharedPref = activity?.getPreferences(Context.MODE_PRIVATE) ?: return
        with(sharedPref.edit()) {
            putString(getString(R.string.token), token)
            apply()
        }
    }

    private fun startApp(token: String)  = lifecycleScope.launch(Dispatchers.IO) {
        Thread.sleep(loading)
        val intent = Intent(activity, MainActivity::class.java)
        intent.putExtra(
            "token",
            token
        )

        startActivity(intent)
    }

    private fun loadToken() = lifecycleScope.launch(Dispatchers.IO) {
        val sharedPref = activity?.getPreferences(Context.MODE_PRIVATE)
        val token = sharedPref!!.getString(getString(R.string.token), null)

        if (token != null) {
            refreshToken(token)
        } else {
            openLogin()
        }
    }

    private fun refreshToken(token: String) = lifecycleScope.launch(Dispatchers.IO) {
        service.refreshToken(
            "Bearer $token"
        ).enqueue(object : Callback<Any> {
            override fun onFailure(call: Call<Any>, t: Throwable) {
            }

            override fun onResponse(call: Call<Any>, response: Response<Any>) {
                val gson = Gson()
                if (response.isSuccessful) {
                    val responseToken =
                        gson.fromJson(gson.toJson(response.body()), AuthResponse::class.java).token
                    saveToken(responseToken)
                    startApp(responseToken)
                } else {
                    openLogin()
                }
            }
        })
    }
    //endregion
}