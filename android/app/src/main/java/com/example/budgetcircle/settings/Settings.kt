package com.example.budgetcircle.settings

import android.R
import android.content.Context
import android.content.res.ColorStateList
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.widget.SwitchCompat
import androidx.core.graphics.drawable.DrawableCompat
import android.net.NetworkInfo

import androidx.core.content.ContextCompat.getSystemService

import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build
import androidx.core.content.ContextCompat


class Settings {
    companion object {
        const val DAY = 0
        const val NIGHT = 1
        var mode: Int = 0
        var token: String = ""
        var login = ""

        fun isDay(): Boolean {
            return mode == DAY
        }

        fun isNight(): Boolean {
            return mode == NIGHT
        }

        fun changeMode() {
            mode =
                if (mode == DAY) {
                    NIGHT
                } else {
                    DAY
                }

        }

        fun isInternetAvailable(context: Context): Boolean {
            var result = false
            val connectivityManager =
                context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                val networkCapabilities = connectivityManager.activeNetwork ?: return false
                val actNw =
                    connectivityManager.getNetworkCapabilities(networkCapabilities) ?: return false
                result = when {
                    actNw.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> true
                    actNw.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> true
                    actNw.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET) -> true
                    else -> false
                }
            } else {
                connectivityManager.run {
                    connectivityManager.activeNetworkInfo?.run {
                        result = when (type) {
                            ConnectivityManager.TYPE_WIFI -> true
                            ConnectivityManager.TYPE_MOBILE -> true
                            ConnectivityManager.TYPE_ETHERNET -> true
                            else -> false
                        }

                    }
                }
            }

            return result
        }

        fun setBackgroundColor(color: Int, vararg views: View) {
            for (view in views) {
                view.setBackgroundColor(color)
            }
        }

        fun setTextColor(color: Int, vararg views: TextView) {
            for (view in views) {
                view.setTextColor(color)
            }
        }

        fun setSwitchColor(
            circleColor: Int,
            uncheckedColor: Int,
            checkedColor: Int,
            vararg views: SwitchCompat
        ) {
            for (switch in views) {
                DrawableCompat.setTintList(
                    switch.thumbDrawable, ColorStateList(
                        arrayOf(intArrayOf(R.attr.state_checked), intArrayOf()), intArrayOf(
                            circleColor,
                            circleColor
                        )
                    )
                )

                DrawableCompat.setTintList(
                    switch.trackDrawable, ColorStateList(
                        arrayOf(intArrayOf(R.attr.state_checked), intArrayOf()), intArrayOf(
                            checkedColor,
                            uncheckedColor
                        )
                    )
                )
            }
        }

        fun setFieldColor(
            mainColor: Int,
            textColor: Int,
            textSecondary: Int,
            vararg views: TextView
        ) {
            for (view in views) {
                view.backgroundTintList = ColorStateList.valueOf(mainColor)
                view.highlightColor = mainColor
                view.setLinkTextColor(mainColor)
                view.setTextColor(textColor)
                view.setHintTextColor(textSecondary)
            }
        }

        fun print(context: Context, message: String?) {
            if (message != null)
                Toast.makeText(context, message, Toast.LENGTH_LONG).show()
        }
    }
}