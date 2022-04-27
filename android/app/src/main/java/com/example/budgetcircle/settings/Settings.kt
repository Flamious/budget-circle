package com.example.budgetcircle.settings

import android.content.Context
import android.content.res.ColorStateList
import android.view.View
import android.widget.TextView
import android.widget.Toast

class Settings {
    companion object {
        const val DAY = 0
        const val NIGHT = 1
        var mode: Int = 0
        var token: String = ""

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