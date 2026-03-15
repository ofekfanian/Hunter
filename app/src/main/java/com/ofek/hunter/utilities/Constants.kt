package com.ofek.hunter.utilities

/**
 * Central place for all app-wide constants and Firestore collection names
 */
object Constants {

    // Firestore collection paths
    object FIRESTORE {
        const val JOBS = "job_applications"
        const val INTERVIEWS = "interviews"
        const val QUESTIONS = "interview_questions"
        const val CV_FILES = "cv_files"
        const val USERS = "users"
    }

    // Intent extra keys for passing data between activities
    const val EXTRA_JOB_ID = "JOB_ID"
    const val EXTRA_INTERVIEW_ID = "INTERVIEW_ID"
}
