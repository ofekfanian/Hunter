package com.ofek.hunter.models

import java.text.SimpleDateFormat
import java.util.*

/**
 * Data model for a scheduled interview with date helpers.
 */
data class Interview(
    var id: String = "",
    var jobId: String = "",
    var companyName: String = "",
    var position: String = "",
    var dateTime: Long = 0L,
    var location: String = "",
    var interviewerName: String = "",
    var notes: String = "",
    var type: String = "Phone",
    var reminderTime: Int = 60,
    var status: String = "Scheduled",
    var createdAt: Long = System.currentTimeMillis(),
    var userId: String = ""
) {
    val formattedDate: String
        get() = if (dateTime > 0) SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(Date(dateTime)) else ""

    val formattedTime: String
        get() = if (dateTime > 0) SimpleDateFormat("HH:mm", Locale.getDefault()).format(Date(dateTime)) else ""

    val isPast: Boolean
        get() = dateTime > 0 && dateTime < System.currentTimeMillis()

    val isUpcoming: Boolean
        get() = dateTime > 0 && dateTime >= System.currentTimeMillis()
}
