package com.ofek.hunter.models

/**
 * Data model for a tracked job application.
 */
data class JobApplication(
    var id: String = "",
    var title: String = "",
    var company: String = "",
    var location: String = "",
    var salary: String = "",
    var jobType: String = "Applied",
    var description: String = "",
    var requirements: String = "",
    var isSaved: Boolean = false,
    var userId: String = "",
    var dateApplied: Long = 0L,
    var source: String = "",
    var workModel: String = "",
    var contactName: String = "",
    var jobUrl: String = ""
)
