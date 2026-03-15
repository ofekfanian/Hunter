package com.ofek.hunter.activities

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.ofek.hunter.R
import com.ofek.hunter.adapters.JobAdapter
import com.ofek.hunter.databinding.ActivityJobListBinding
import com.ofek.hunter.interfaces.JobCallback
import com.ofek.hunter.models.JobApplication
import com.ofek.hunter.utilities.AnimationHelper
import com.ofek.hunter.utilities.Constants
import com.ofek.hunter.utilities.DrawerHelper
import com.ofek.hunter.utilities.NavigationHelper

/**
 * Job tracker list with search, stats, daily quote, and swipe refresh.
 */
class JobListActivity : AppCompatActivity() {

    private lateinit var binding: ActivityJobListBinding
    private lateinit var jobAdapter: JobAdapter
    private var allJobs: List<JobApplication> = listOf()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityJobListBinding.inflate(layoutInflater)
        setContentView(binding.root)
        AnimationHelper.enterTransition(this)
        initViews()
    }

    override fun onResume() {
        super.onResume()
        loadJobs()
    }

    // Sets up header, bottom nav, search, FAB, recycler, and swipe-to-refresh
    private fun initViews() {
        binding.root.findViewById<TextView>(R.id.header_title)?.apply {
            text = getString(R.string.job_list_title)
            setShadowLayer(12f, 0f, 0f, getColor(R.color.indigo_glow))
        }
        binding.root.findViewById<ImageView>(R.id.header_screen_icon)?.setImageResource(R.drawable.ic_work)
        NavigationHelper.setupBottomNavigation(this, "home")
        // Set up drawer menu navigation
        DrawerHelper.setupDrawer(this, binding.drawerLayout)
        setupDailyQuote()
        setupSearchField()
        setupFAB()
        setupRecyclerView()
        binding.jobListSwipeRefresh.setOnRefreshListener { loadJobs() }
    }

    // Picks a motivational quote based on the day of the year
    private fun setupDailyQuote() {
        val quotes = resources.getStringArray(R.array.motivational_quotes)
        val dayOfYear = java.util.Calendar.getInstance().get(java.util.Calendar.DAY_OF_YEAR)
        val quote = quotes[dayOfYear % quotes.size]
        findViewById<TextView>(R.id.daily_quote_text)?.text = "\"$quote\""
    }

    // Filters the job list as the user types in the search bar
    private fun setupSearchField() {
        binding.searchText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable?) {
                filterJobs(s?.toString().orEmpty())
            }
        })
    }

    private fun filterJobs(query: String) {
        val filtered = if (query.isBlank()) {
            allJobs
        } else {
            val q = query.lowercase()
            allJobs.filter {
                it.company.lowercase().contains(q) ||
                it.title.lowercase().contains(q) ||
                it.location.lowercase().contains(q)
            }
        }
        jobAdapter.jobs = filtered
        jobAdapter.notifyDataSetChanged()
        showContent(filtered.isEmpty())
    }

    // Configures the floating action button to open AddJobActivity
    private fun setupFAB() {
        binding.jobListFAB.setOnClickListener {
            startActivity(Intent(this, AddJobActivity::class.java))
        }
        binding.jobListFAB.post { AnimationHelper.bounce(binding.jobListFAB) }
    }

    // Creates the adapter with a click callback and attaches it to the RecyclerView
    private fun setupRecyclerView() {
        jobAdapter = JobAdapter()
        jobAdapter.jobCallback = object : JobCallback {
            override fun itemClicked(job: JobApplication, position: Int) {
                startActivity(Intent(this@JobListActivity, JobDetailsActivity::class.java).apply {
                    putExtra(Constants.EXTRA_JOB_ID, job.id)
                })
            }
            override fun favoriteClicked(job: JobApplication, position: Int) {
                val newSaved = !job.isSaved
                FirebaseFirestore.getInstance()
                    .collection(Constants.FIRESTORE.JOBS)
                    .document(job.id)
                    .update("isSaved", newSaved)
                    .addOnSuccessListener {
                        job.isSaved = newSaved
                        jobAdapter.notifyItemChanged(position)
                    }
            }
        }
        binding.jobListLSTJobs.apply {
            layoutManager = LinearLayoutManager(this@JobListActivity)
            adapter = jobAdapter
        }
    }

    // Fetches all jobs for the current user from Firestore
    private fun loadJobs() {
        showLoading()
        val userId = FirebaseAuth.getInstance().currentUser?.uid ?: ""
        FirebaseFirestore.getInstance()
            .collection(Constants.FIRESTORE.JOBS)
            .whereEqualTo("userId", userId)
            .get()
            .addOnSuccessListener { result ->
                allJobs = result.mapNotNull { doc ->
                    doc.toObject(JobApplication::class.java)?.also { it.id = doc.id }
                }
                updateStats(allJobs)
                binding.jobListSwipeRefresh.isRefreshing = false
                filterJobs(binding.searchText.text?.toString().orEmpty())
            }
            .addOnFailureListener {
                binding.jobListSwipeRefresh.isRefreshing = false
                showError()
            }
    }

    // Refreshes the stat counters (total, applied, interviews, offers)
    private fun updateStats(jobs: List<JobApplication>) {
        findViewById<TextView>(R.id.stat_total_value)?.text = jobs.size.toString()
        findViewById<TextView>(R.id.stat_applied_value)?.text = jobs.count { it.jobType == "Applied" }.toString()
        findViewById<TextView>(R.id.stat_interviews_value)?.text = jobs.count { it.jobType == "Interview" }.toString()
        findViewById<TextView>(R.id.stat_offers_value)?.text = jobs.count { it.jobType == "Offer" }.toString()
    }

    // Shows the progress bar and hides list + empty state
    private fun showLoading() {
        binding.jobListProgressBar.visibility = View.VISIBLE
        binding.jobListLSTJobs.visibility = View.GONE
        binding.jobListEmptyState.root.visibility = View.GONE
    }

    // Hides progress and shows either the job list or the empty state
    private fun showContent(isEmpty: Boolean) {
        binding.jobListProgressBar.visibility = View.GONE
        binding.jobListEmptyState.root.visibility = if (isEmpty) View.VISIBLE else View.GONE
        binding.jobListLSTJobs.visibility = if (isEmpty) View.GONE else View.VISIBLE
    }

    override fun finish() {
        super.finish()
        AnimationHelper.exitTransition(this)
    }

    // Shows the empty state view when loading fails
    private fun showError() {
        binding.jobListProgressBar.visibility = View.GONE
        binding.jobListEmptyState.root.visibility = View.VISIBLE
        binding.jobListLSTJobs.visibility = View.GONE
    }
}
