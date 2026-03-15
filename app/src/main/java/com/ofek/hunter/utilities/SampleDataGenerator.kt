package com.ofek.hunter.utilities

import com.ofek.hunter.models.InterviewQuestion

/**
 * Provide fallback sample questions when Firestore is empty.
 */
object SampleDataGenerator {

    // Return a hardcoded list of sample questions and tips
    fun getSampleQuestions(): List<InterviewQuestion> = listOf(
        InterviewQuestion(
            id = "sample_1",
            companyName = "Google",
            position = "Senior Android Developer",
            question = "How does the Android Activity lifecycle work, and how do you handle configuration changes like screen rotation?",
            answer = "The Activity lifecycle consists of onCreate, onStart, onResume, onPause, onStop, onDestroy. For configuration changes, use ViewModel to preserve data.",
            categories = listOf("Technical"),
            isAnonymous = false,
            upvotes = 47,
            userName = "Alex R.",
            type = InterviewQuestion.TYPE_QUESTION
        ),
        InterviewQuestion(
            id = "sample_2",
            companyName = "Meta",
            position = "Software Engineer",
            question = "Tell me about a time you had to work under tight deadlines and how you prioritized tasks.",
            answer = "",
            categories = listOf("Behavioral"),
            isAnonymous = true,
            upvotes = 32,
            userName = "",
            type = InterviewQuestion.TYPE_QUESTION
        ),
        InterviewQuestion(
            id = "sample_3",
            companyName = "Amazon",
            position = "Backend Engineer",
            question = "Design a URL shortening service like bit.ly. How would you handle scalability and high availability?",
            answer = "Use a hash function to generate short codes, store in a distributed database (DynamoDB), add a caching layer (Redis), and use load balancers for high availability.",
            categories = listOf("System Design"),
            isAnonymous = false,
            upvotes = 89,
            userName = "Noa K.",
            type = InterviewQuestion.TYPE_QUESTION
        ),
        InterviewQuestion(
            id = "sample_4",
            companyName = "Microsoft",
            position = "Full Stack Developer",
            question = "Implement a function that finds the two numbers in an array that sum to a target value.",
            answer = "Use a HashMap to store complements. For each number, check if its complement (target - num) exists in the map. O(n) time complexity.",
            categories = listOf("Coding"),
            isAnonymous = false,
            upvotes = 61,
            userName = "Dan M.",
            type = InterviewQuestion.TYPE_QUESTION
        ),
        InterviewQuestion(
            id = "sample_5",
            companyName = "Apple",
            position = "iOS / Android Developer",
            question = "How would you describe your leadership style and give an example of when you led a team through a difficult project?",
            answer = "",
            categories = listOf("Leadership"),
            isAnonymous = true,
            upvotes = 18,
            userName = "",
            type = InterviewQuestion.TYPE_QUESTION
        ),
        InterviewQuestion(
            id = "tip_1",
            companyName = "Wix",
            position = "HR Manager",
            question = "Answer 'Tell me about yourself' like a pro — present role → relevant past experience → why you're excited about this opportunity. Keep it under 2 minutes.",
            answer = "",
            categories = listOf("Interview"),
            isAnonymous = false,
            upvotes = 134,
            userName = "Maya S.",
            type = InterviewQuestion.TYPE_TIP
        ),
        InterviewQuestion(
            id = "tip_2",
            companyName = "CyberArk",
            position = "Team Lead",
            question = "Use the STAR method for behavioral questions: Situation, Task, Action, Result. Always quantify your results when possible.",
            answer = "",
            categories = listOf("Behavioral"),
            isAnonymous = false,
            upvotes = 98,
            userName = "Yoni B.",
            type = InterviewQuestion.TYPE_TIP
        ),
        InterviewQuestion(
            id = "tip_3",
            companyName = "Monday.com",
            position = "Talent Acquisition",
            question = "Tailor your resume for every application. Mirror the job description keywords — ATS systems filter resumes before a human sees them.",
            answer = "",
            categories = listOf("Resume"),
            isAnonymous = false,
            upvotes = 211,
            userName = "Noa K.",
            type = InterviewQuestion.TYPE_TIP
        ),
        InterviewQuestion(
            id = "tip_4",
            companyName = "Check Point",
            position = "Senior Developer",
            question = "Always ask questions at the end. Prepare 3–5 thoughtful questions about the team, product, or growth opportunities.",
            answer = "",
            categories = listOf("Interview"),
            isAnonymous = false,
            upvotes = 76,
            userName = "Oren L.",
            type = InterviewQuestion.TYPE_TIP
        ),
        InterviewQuestion(
            id = "tip_5",
            companyName = "Intel",
            position = "Career Coach",
            question = "Research the company deeply before interviews. Study the mission, recent news, product launches, and competitors.",
            answer = "",
            categories = listOf("Preparation"),
            isAnonymous = false,
            upvotes = 55,
            userName = "Alex R.",
            type = InterviewQuestion.TYPE_TIP
        ),
        InterviewQuestion(
            id = "tip_6",
            companyName = "IronSource",
            position = "Product Manager",
            question = "Follow up with a thank-you email within 24 hours. Mention something specific from your conversation.",
            answer = "",
            categories = listOf("Follow-up"),
            isAnonymous = false,
            upvotes = 43,
            userName = "Dan M.",
            type = InterviewQuestion.TYPE_TIP
        )
    )
}
