package com.ofek.hunter.utilities

import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

/**
 * Format dates and times for display across the app.
 */
object DateTimeHelper {

    // Format timestamp as dd/MM/yyyy
    fun formatDateShort(timestamp: Long): String {
        val sdf = SimpleDateFormat("dd/MM/yy", Locale.getDefault())
        return sdf.format(Date(timestamp))
    }

    // Format timestamp as HH:mm
    fun formatTime(timestamp: Long): String {
        val sdf = SimpleDateFormat("HH:mm", Locale.getDefault())
        return sdf.format(Date(timestamp))
    }
}
