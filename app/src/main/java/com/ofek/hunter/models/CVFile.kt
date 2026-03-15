package com.ofek.hunter.models

/**
 * Data model for an uploaded CV/resume file.
 */
data class CVFile(
    var id: String = "",
    var fileName: String = "",
    var fileSize: Long = 0L,
    var downloadUrl: String = "",
    var uploadedAt: Long = System.currentTimeMillis(),
    var userId: String = ""
)
