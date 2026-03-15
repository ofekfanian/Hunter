package com.ofek.hunter.interfaces

import com.ofek.hunter.models.InterviewQuestion

/**
 * Callback interface for community question interactions.
 */
interface QuestionCallback {
    fun itemClicked(question: InterviewQuestion, position: Int)
    fun upvoteClicked(question: InterviewQuestion, position: Int)
}
