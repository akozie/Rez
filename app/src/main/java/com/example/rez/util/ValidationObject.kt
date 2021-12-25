package com.example.rez.util

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

    fun EditText.validatePhoneNumber(number: String): Boolean {
        val pattern: Pattern = Pattern.compile("^(0|234)((70)|([89][01]))[0-9]{8}\$")
        val matcher: Matcher = pattern.matcher(number)
        val matchFound = matcher.matches()
        return !(number.isEmpty() || !matchFound)
    }

    // Function to check password mismatch
    fun validatePasswordMismatch(password: String, confirmPassword: String): Boolean {
        return password == confirmPassword
    }

}
