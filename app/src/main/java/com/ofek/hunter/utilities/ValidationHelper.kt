package com.ofek.hunter.utilities

/**
 * Centralized validation logic for forms and user input.
 */
object ValidationHelper {

    // Validate required field is not empty
    fun isNotEmpty(text: String): Boolean {
        return text.trim().isNotEmpty()
    }
}
