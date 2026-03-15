package com.ofek.hunter.utilities

import android.util.Patterns

/**
 * Centralized validation logic for forms and user input.
 */
object ValidationHelper {

    // Validate email format
    fun isValidEmail(email: String): Boolean {
        return email.isNotEmpty() && Patterns.EMAIL_ADDRESS.matcher(email).matches()
    }

    // Validate password strength (min 6 chars)
    fun isValidPassword(password: String): Boolean {
        return password.length >= 6
    }

    // Validate passwords match
    fun passwordsMatch(password: String, confirmPassword: String): Boolean {
        return password == confirmPassword && password.isNotEmpty()
    }

    // Validate required field is not empty
    fun isNotEmpty(text: String): Boolean {
        return text.trim().isNotEmpty()
    }
}
