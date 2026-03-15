package com.ofek.hunter.models

/**
 * Data model for community questions and tips.
 */
data class InterviewQuestion(
    var id: String = "",
    var companyName: String = "",
    var position: String = "",
    var question: String = "",
    var answer: String = "",
    var categories: List<String> = emptyList(),
    var isAnonymous: Boolean = false,
    var upvotes: Int = 0,
    var userName: String = "",
    var userId: String = "",
    var upvotedBy: List<String> = emptyList(),
    var createdAt: Long = System.currentTimeMillis(),
    var type: String = TYPE_QUESTION
) {
    companion object {
        const val TYPE_QUESTION = "question"
        const val TYPE_TIP = "tip"
    }
}
