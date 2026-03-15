package com.ofek.hunter.activities

import android.content.Intent
import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.tabs.TabLayoutMediator
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.ofek.hunter.R
import com.ofek.hunter.databinding.ActivityInterviewsBinding
import com.ofek.hunter.adapters.InterviewsPagerAdapter
import com.ofek.hunter.models.Interview
import com.ofek.hunter.utilities.AnimationHelper
import com.ofek.hunter.utilities.Constants
import com.ofek.hunter.utilities.DrawerHelper
import com.ofek.hunter.utilities.NavigationHelper

/**
 * Interview list screen with upcoming/past tabs and statistics.
 */
class InterviewsActivity : AppCompatActivity() {

    private lateinit var binding: ActivityInterviewsBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityInterviewsBinding.inflate(layoutInflater)
        setContentView(binding.root)
        AnimationHelper.enterTransition(this)
        initViews()
    }

    // Set up header, bottom nav, FAB, and view pager with tabs
    private fun initViews() {
        binding.root.findViewById<TextView>(R.id.header_title)?.apply {
            text = getString(R.string.interviews_title)
            setShadowLayer(12f, 0f, 0f, getColor(R.color.blue_glow))
        }
        binding.root.findViewById<ImageView>(R.id.header_screen_icon)?.setImageResource(R.drawable.ic_calendar)
        NavigationHelper.setupBottomNavigation(this, "home")
        // Set up drawer menu navigation
        DrawerHelper.setupDrawer(this, binding.drawerLayout)
        setupFAB()
        setupViewPager()
    }

    override fun finish() {
        super.finish()
        AnimationHelper.exitTransition(this)
    }

    // Configure the FAB to open the add-interview screen
    private fun setupFAB() {
        binding.fabAddInterview.setOnClickListener {
            startActivity(Intent(this, AddInterviewActivity::class.java))
        }
        binding.fabAddInterview.post { AnimationHelper.bounce(binding.fabAddInterview) }
    }

    // Set up the upcoming/past tabs with a view pager
    private fun setupViewPager() {
        binding.viewPager.adapter = InterviewsPagerAdapter(this)

        TabLayoutMediator(binding.tabs, binding.viewPager) { tab, position ->
            tab.text = when (position) {
                0 -> getString(R.string.tab_upcoming)
                1 -> getString(R.string.tab_past)
                else -> ""
            }
        }.attach()
    }

    // Fetch interview counts from Firestore and update the stat cards
    private fun updateStatistics() {
        val userId = FirebaseAuth.getInstance().currentUser?.uid ?: ""
        FirebaseFirestore.getInstance()
            .collection(Constants.FIRESTORE.INTERVIEWS)
            .whereEqualTo("userId", userId)
            .get()
            .addOnSuccessListener { result ->
                val all = result.mapNotNull { doc ->
                    doc.toObject(Interview::class.java)?.also { it.id = doc.id }
                }
                val upcoming = all.filter { it.isUpcoming }

                binding.statTotalValue.text = all.size.toString()
                binding.statUpcomingValue.text = upcoming.size.toString()
            }
            .addOnFailureListener { }
    }

    override fun onResume() {
        super.onResume()
        updateStatistics()
    }
}
