package com.ofek.hunter.utilities

import android.app.Activity
import android.content.Intent
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import com.ofek.hunter.R
import com.ofek.hunter.activities.CVManagerActivity
import com.ofek.hunter.activities.JobListActivity
import com.ofek.hunter.activities.ProfileActivity
import com.ofek.hunter.activities.StatisticsActivity

/**
 * Set up bottom navigation bar and handle tab switching.
 */
object NavigationHelper {

    // Wire up bottom nav click listeners and highlight the active tab
    fun setupBottomNavigation(activity: Activity, activeTab: String) {
        val navHome = activity.findViewById<View>(R.id.nav_home)
        val navStatistics = activity.findViewById<View>(R.id.nav_statistics)
        val navCv = activity.findViewById<View>(R.id.nav_cv)
        val navProfile = activity.findViewById<View>(R.id.nav_profile)

        // Highlight active tab
        highlightTab(activity, activeTab)

        // Click listeners
        navHome?.setOnClickListener {
            if (activeTab != "home") {
                activity.startActivity(Intent(activity, JobListActivity::class.java)
                    .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP))
                activity.finish()
                AnimationHelper.enterTransition(activity)
            }
        }

        navStatistics?.setOnClickListener {
            if (activeTab != "statistics") {
                activity.startActivity(Intent(activity, StatisticsActivity::class.java)
                    .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP))
                activity.finish()
                AnimationHelper.enterTransition(activity)
            }
        }

        navCv?.setOnClickListener {
            if (activeTab != "cv") {
                activity.startActivity(Intent(activity, CVManagerActivity::class.java)
                    .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP))
                activity.finish()
                AnimationHelper.enterTransition(activity)
            }
        }

        navProfile?.setOnClickListener {
            if (activeTab != "profile") {
                activity.startActivity(Intent(activity, ProfileActivity::class.java)
                    .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP))
                activity.finish()
                AnimationHelper.enterTransition(activity)
            }
        }
    }

    // Set full opacity on the active tab icon/label, dim the rest
    private fun highlightTab(activity: Activity, activeTab: String) {
        val tabs = mapOf(
            "home" to Pair(R.id.nav_home_icon, R.id.nav_home_label),
            "statistics" to Pair(R.id.nav_statistics_icon, R.id.nav_statistics_label),
            "cv" to Pair(R.id.nav_cv_icon, R.id.nav_cv_label),
            "profile" to Pair(R.id.nav_profile_icon, R.id.nav_profile_label)
        )

        for ((tab, ids) in tabs) {
            val icon = activity.findViewById<ImageView>(ids.first)
            val label = activity.findViewById<TextView>(ids.second)
            if (tab == activeTab) {
                icon?.alpha = 1f
                label?.alpha = 1f
            } else {
                icon?.alpha = 0.5f
                label?.alpha = 0f
            }
        }
    }
}
