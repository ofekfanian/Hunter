package com.ofek.hunter.interfaces

import com.ofek.hunter.models.JobApplication

/**
 * Callback interface for job list item interactions.
 */
interface JobCallback {
    fun itemClicked(job: JobApplication, position: Int)
    fun favoriteClicked(job: JobApplication, position: Int)
}
