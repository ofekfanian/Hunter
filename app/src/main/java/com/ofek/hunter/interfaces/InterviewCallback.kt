package com.ofek.hunter.interfaces

import com.ofek.hunter.models.Interview

/**
 * Callback interface for interview list item clicks.
 */
interface InterviewCallback {
    fun itemClicked(interview: Interview, position: Int)
}
