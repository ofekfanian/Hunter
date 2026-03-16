package com.ofek.hunter.activities

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.ofek.hunter.R
import com.ofek.hunter.adapters.JobAdapter
import com.ofek.hunter.databinding.ActivityFavoritesBinding
import com.ofek.hunter.interfaces.JobCallback
import com.ofek.hunter.models.JobApplication
import com.ofek.hunter.utilities.AnimationHelper
import com.ofek.hunter.utilities.Constants
import com.ofek.hunter.utilities.DrawerHelper
import com.ofek.hunter.utilities.NavigationHelper

/**
 * Shows only jobs the user has marked as favorite (isSaved == true).
 */
class FavoritesActivity : AppCompatActivity() {

    private lateinit var binding: ActivityFavoritesBinding
    private lateinit var jobAdapter: JobAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityFavoritesBinding.inflate(layoutInflater)
        setContentView(binding.root)
        AnimationHelper.enterTransition(this)
        initViews()
    }

    override fun onResume() {
        super.onResume()
        loadFavorites()
    }

    private fun initViews() {
        binding.root.findViewById<TextView>(R.id.header_title)?.apply {
            text = getString(R.string.drawer_favorites)
            setShadowLayer(12f, 0f, 0f, getColor(R.color.stat_accent_rose))
        }
        binding.root.findViewById<ImageView>(R.id.header_screen_icon)?.setImageResource(R.drawable.ic_favorite_filled)
        NavigationHelper.setupBottomNavigation(this, "")
        DrawerHelper.setupDrawer(this, binding.drawerLayout)
        setupRecyclerView()
    }

    private fun setupRecyclerView() {
        jobAdapter = JobAdapter()
        jobAdapter.jobCallback = object : JobCallback {
            override fun itemClicked(job: JobApplication, position: Int) {
                startActivity(Intent(this@FavoritesActivity, JobDetailsActivity::class.java).apply {
                    putExtra(Constants.EXTRA_JOB_ID, job.id)
                })
            }
            override fun favoriteClicked(job: JobApplication, position: Int) {
                FirebaseFirestore.getInstance()
                    .collection(Constants.FIRESTORE.JOBS)
                    .document(job.id)
                    .update("isSaved", false)
                    .addOnSuccessListener {
                        job.isSaved = false
                        jobAdapter.jobs = jobAdapter.jobs.filter { it.isSaved }
                        jobAdapter.notifyItemRangeChanged(0, jobAdapter.itemCount)
                        showContent(jobAdapter.jobs.isEmpty())
                    }
            }
        }
        binding.favoritesRecycler.apply {
            layoutManager = LinearLayoutManager(this@FavoritesActivity)
            adapter = jobAdapter
        }
    }

    private fun loadFavorites() {
        showLoading()
        val userId = FirebaseAuth.getInstance().currentUser?.uid ?: ""
        FirebaseFirestore.getInstance()
            .collection(Constants.FIRESTORE.JOBS)
            .whereEqualTo("userId", userId)
            .whereEqualTo("isSaved", true)
            .get()
            .addOnSuccessListener { result ->
                val favorites = result.mapNotNull { doc ->
                    doc.toObject(JobApplication::class.java).also { it.id = doc.id }
                }
                jobAdapter.jobs = favorites
                jobAdapter.notifyItemRangeChanged(0, jobAdapter.itemCount)
                showContent(favorites.isEmpty())
            }
            .addOnFailureListener {
                showContent(true)
            }
    }

    private fun showLoading() {
        binding.favoritesProgressBar.visibility = View.VISIBLE
        binding.favoritesRecycler.visibility = View.GONE
        binding.favoritesEmptyState.root.visibility = View.GONE
    }

    private fun showContent(isEmpty: Boolean) {
        binding.favoritesProgressBar.visibility = View.GONE
        binding.favoritesEmptyState.root.visibility = if (isEmpty) View.VISIBLE else View.GONE
        binding.favoritesRecycler.visibility = if (isEmpty) View.GONE else View.VISIBLE
        if (isEmpty) {
            binding.favoritesEmptyState.root.findViewById<TextView>(R.id.empty_text)?.text =
                getString(R.string.no_favorites)
        }
    }

    override fun finish() {
        super.finish()
        AnimationHelper.exitTransition(this)
    }
}
