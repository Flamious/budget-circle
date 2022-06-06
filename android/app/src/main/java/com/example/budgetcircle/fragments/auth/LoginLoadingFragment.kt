package com.example.budgetcircle.fragments.auth

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.example.budgetcircle.MainActivity
import com.example.budgetcircle.NoInternetActivity
import com.example.budgetcircle.R
import com.example.budgetcircle.databinding.FragmentLoginLoadingBinding
import com.example.budgetcircle.requests.Client
import com.example.budgetcircle.requests.UserApi
import com.example.budgetcircle.requests.models.AuthResponse
import com.example.budgetcircle.settings.Settings
import com.example.budgetcircle.settings.charts.PieChartSetter
import com.google.gson.Gson
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import kotlin.random.Random

class LoginLoadingFragment : Fragment() {
    lateinit var binding: FragmentLoginLoadingBinding
    private lateinit var service: UserApi
    private var loading: Long = 1500

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
        setTheme()
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

    private fun setTheme() {
        if (Settings.isNight()) {
            binding.apply {
                val textPrimary = ContextCompat.getColor(
                    this@LoginLoadingFragment.requireContext(),
                    R.color.light_grey
                )
                val backgroundColor = ContextCompat.getColor(
                    this@LoginLoadingFragment.requireContext(),
                    R.color.dark_grey
                )

                loadingFragmentLayout.setBackgroundColor(backgroundColor) //= ColorStateList.valueOf(backgroundColor)
                loadingFragmentTitle.setTextColor(textPrimary)
            }
        }
    }

    private fun setChart() {
        binding.loadingFragmentTitle.startAnimation(appear)

        val size = 4
        val values = Array(size) { Random.nextDouble(10.0, 50.0) }
        val titles = Array(size) { resources.getString(R.string.app_name) }
        val colors = if (Settings.isDay())
            resources.getIntArray(R.array.budget_colors).toCollection(ArrayList())
        else
            resources.getIntArray(R.array.dark_colors).toCollection(ArrayList())

        val holeColor = ContextCompat.getColor(
            this.requireContext(),
            if (Settings.isDay()) R.color.white else R.color.dark_grey
        )

        PieChartSetter.setChart(
            titles,
            values,
            colors,
            0.0,
            resources.getString(R.string.app_name),
            binding.loadingFragmentPieChart,
            binding.loadingFragmentSumTextView,
            binding.loadingFragmentTitle,
            holeColor,
            isFull = false,
            noEntries = true
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

    private fun startApp(token: String) = lifecycleScope.launch(Dispatchers.IO) {
        Thread.sleep(loading)
        val intent = Intent(activity, MainActivity::class.java)
        intent.putExtra(
            "token",
            token
        )

        val sharedPref = activity?.getPreferences(Context.MODE_PRIVATE)
        val login = sharedPref!!.getString(getString(R.string.login_save), null)

        intent.putExtra(
            "login",
            login
        )
        startActivity(intent)
    }

    private fun loadToken() = lifecycleScope.launch(Dispatchers.IO) {
        val sharedPref = activity?.getPreferences(Context.MODE_PRIVATE)
        val token = sharedPref!!.getString(getString(R.string.token), null)

        if (token != null) {
            if(Settings.isInternetAvailable(this@LoginLoadingFragment.requireContext())) {
                refreshToken(token)
            } else {
                openNoInternetActivity()
            }
        } else {
            openLogin()
        }
    }

    private fun refreshToken(token: String) = lifecycleScope.launch(Dispatchers.IO) {
        service.refreshToken(
            "Bearer $token"
        ).enqueue(object : Callback<Any> {
            override fun onFailure(call: Call<Any>, t: Throwable) {
                openLogin()
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

    private fun openNoInternetActivity() {
        Thread.sleep(loading)
        val intent = Intent(activity, NoInternetActivity::class.java)

        val sharedPref = activity?.getPreferences(Context.MODE_PRIVATE)
        val login = sharedPref!!.getString(getString(R.string.login_save), null)
        intent.putExtra(
            "login",
            login
        )

        startActivity(intent)
    }
    //endregion
}