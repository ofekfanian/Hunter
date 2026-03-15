package com.ofek.hunter.activities

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.ofek.hunter.databinding.ActivityMainBinding
import com.ofek.hunter.utilities.AnimationHelper

/**
 * Main dashboard with four feature cards and navigation drawer.
 */
class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        AnimationHelper.enterTransition(this)
        initViews()
    }

    // Wires up card click listeners and entrance animations
    private fun initViews() {
        setupClickListeners()
        AnimationHelper.animateViews(
            this,
            binding.mainCardJobTracker,
            binding.mainCardCvManager,
            binding.mainCardCommunity,
            binding.mainCardStatistics
        )
    }

    override fun finish() {
        super.finish()
        AnimationHelper.exitTransition(this)
    }

    // Hooks up the four dashboard cards to their target activities
    private fun setupClickListeners() {
        binding.mainCardJobTracker.setOnClickListener {
            startActivity(Intent(this, JobListActivity::class.java))
        }

        binding.mainCardCvManager.setOnClickListener {
            startActivity(Intent(this, CVManagerActivity::class.java))
        }

        binding.mainCardCommunity.setOnClickListener {
            startActivity(Intent(this, CommunityActivity::class.java))
        }

        binding.mainCardStatistics.setOnClickListener {
            startActivity(Intent(this, StatisticsActivity::class.java))
        }
    }

}
