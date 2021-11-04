package com.example.budgetcircle.settings

import android.text.TextUtils

import android.text.Spanned

import android.text.InputFilter
import java.util.regex.Matcher
import java.util.regex.Pattern

class SumInputFilter(digitsBeforeZero: Int = DIGITS_BEFORE_ZERO_DEFAULT, digitsAfterZero: Int = DIGITS_AFTER_ZERO_DEFAULT) :
    InputFilter {
    private val mDigitsBeforeZero: Int = digitsBeforeZero
    private val mDigitsAfterZero: Int = digitsAfterZero
    private val mPattern: Pattern = Pattern.compile(
        "-?[0-9]{0," + mDigitsBeforeZero + "}+((\\.[0-9]{0," + mDigitsAfterZero
                + "})?)||(\\.)?"
    )

    override fun filter(
        source: CharSequence,
        start: Int,
        end: Int,
        dest: Spanned,
        dstart: Int,
        dend: Int
    ): CharSequence? {
        val replacement = source.subSequence(start, end).toString()
        val newVal = (dest.subSequence(0, dstart).toString() + replacement
                + dest.subSequence(dend, dest.length).toString())
        val matcher: Matcher = mPattern.matcher(newVal)
        if (matcher.matches()) return null
        return if (TextUtils.isEmpty(source)) dest.subSequence(dstart, dend) else ""
    }

    companion object {
        private const val DIGITS_BEFORE_ZERO_DEFAULT = 7
        private const val DIGITS_AFTER_ZERO_DEFAULT = 2
    }

}