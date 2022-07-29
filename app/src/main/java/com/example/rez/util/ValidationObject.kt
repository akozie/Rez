package com.example.rez.util

import android.text.TextUtils.replace
import android.widget.EditText
import java.util.Locale
import java.util.regex.Matcher
import java.util.regex.Pattern

object ValidationObject {

    /*Function to validate Email*/
    fun validateEmail(email: String): Boolean {
        val pattern = "\\w+@[a-zA-Z_]+?\\.[a-zA-Z]{2,6}".toRegex()
        return email.matches(pattern)
    }

    fun String.validatePhoneNumber(number: String): Boolean {
       // val pattern: Pattern = Pattern.compile("^(0|234)((70)|([89][01]))[0-9]{8}\$")
        val pattern: Pattern = Pattern.compile("^(?:(?:\\+?1\\s*(?:[.-]\\s*)?)?(?:\\(\\s*([2-9]1[02-9]|[2-9][02-8]1|[2-9][02-8][02-9])\\s*\\)|([2-9]1[02-9]|[2-9][02-8]1|[2-9][02-8][02-9]))\\s*(?:[.-]\\s*)?)?([2-9]1[02-9]|[2-9][02-9]1|[2-9][02-9]{2})\\s*(?:[.-]\\s*)?([0-9]{4})(?:\\s*(?:#|x\\.?|ext\\.?|extension)\\s*(\\d+))?\$")
        val matcher: Matcher = pattern.matcher(number)
        val matchFound = matcher.matches()
        return !(number.isEmpty() || !matchFound)
    }
    fun EditText.validatePhoneNumber(number: String): Boolean {
       // val pattern: Pattern = Pattern.compile("^(0|234)((70)|([89][01]))[0-9]{8}\$")
        val pattern: Pattern = Pattern.compile("^(?:(?:\\+?1\\s*(?:[.-]\\s*)?)?(?:\\(\\s*([2-9]1[02-9]|[2-9][02-8]1|[2-9][02-8][02-9])\\s*\\)|([2-9]1[02-9]|[2-9][02-8]1|[2-9][02-8][02-9]))\\s*(?:[.-]\\s*)?)?([2-9]1[02-9]|[2-9][02-9]1|[2-9][02-9]{2})\\s*(?:[.-]\\s*)?([0-9]{4})(?:\\s*(?:#|x\\.?|ext\\.?|extension)\\s*(\\d+))?\$")
        val matcher: Matcher = pattern.matcher(number)
        val matchFound = matcher.matches()
        return !(number.isEmpty() || !matchFound)
    }

    fun String.cleanPhoneNumberString(): String {
        return replace("-", "").replace(",", "")
            .replace("[\\s]".toRegex(), "").trim()
    }

    fun String.isValidPhoneNumber(): Boolean {
        val clearDigitsRegex = "[^\\d]"
        var phoneNumber: String = replace(clearDigitsRegex.toRegex(), "").cleanPhoneNumberString()

        if (phoneNumber.startsWith("0").not() && phoneNumber.startsWith("234").not()) {
            phoneNumber = "234$phoneNumber"
        }

        val regInternationalised = "^234[7,8,9]\\d{9}\$"
        val regNorm = "^0[7,8,9]\\d{9}\$"


        val pattern: Pattern = Pattern.compile(regNorm)
        val internationalizedPattern: Pattern = Pattern.compile(regInternationalised)
        return pattern.matcher(phoneNumber).find() or (internationalizedPattern.matcher(phoneNumber)
            .find())
    }

//
//
    // Function to check password mismatch
    fun validatePasswordMismatch(password: String, confirmPassword: String): Boolean {
        return password == confirmPassword
    }

}
