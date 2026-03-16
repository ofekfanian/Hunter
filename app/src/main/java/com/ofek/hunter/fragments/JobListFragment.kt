package com.ofek.hunter.fragments

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.ProgressBar
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.ofek.hunter.R
import com.ofek.hunter.activities.JobDetailsActivity
import com.ofek.hunter.adapters.JobAdapter
import com.ofek.hunter.interfaces.JobCallback
import com.ofek.hunter.models.JobApplication
import com.ofek.hunter.utilities.Constants

/**
 * Fragment that shows a filtered list of jobs (by status, saved, or all).
 */
class JobListFragment : Fragment() {

    companion object {
        private const val ARG_FILTER_TYPE = "filter_type"
        private const val ARG_FILTER_VALUE = "filter_value"

        const val FILTER_STATUS = "status"
        const val FILTER_SAVED = "saved"
        const val FILTER_ALL = "all"

        @Suppress("unused")
        fun newInstance(filterType: String, filterValue: String = ""): JobListFragment {
            return JobListFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_FILTER_TYPE, filterType)
                    putString(ARG_FILTER_VALUE, filterValue)
                }
            }
        }
    }

    private lateinit var recyclerView: RecyclerView
    private lateinit var emptyState: LinearLayout
    private lateinit var progressBar: ProgressBar
    private lateinit var jobAdapter: JobAdapter
    private var filterType: String = FILTER_ALL
    private var filterValue: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        filterType = arguments?.getString(ARG_FILTER_TYPE) ?: FILTER_ALL
        filterValue = arguments?.getString(ARG_FILTER_VALUE) ?: ""
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_job_list, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        recyclerView = view.findViewById(R.id.jobs_recycler)
        emptyState = view.findViewById(R.id.empty_state)
        progressBar = view.findViewById(R.id.jobs_progress_bar)

        setupRecyclerView()
    }

    private fun setupRecyclerView() {
        jobAdapter = JobAdapter()
        jobAdapter.jobCallback = object : JobCallback {
            override fun itemClicked(job: JobApplication, position: Int) {
                startActivity(Intent(requireContext(), JobDetailsActivity::class.java).apply {
                    putExtra(Constants.EXTRA_JOB_ID, job.id)
                })
            }
            override fun favoriteClicked(job: JobApplication, position: Int) {}
        }
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        recyclerView.adapter = jobAdapter
    }

    // Demo jobs for presentation
    private fun loadDemoJobs() {
        val now = System.currentTimeMillis()
        val day = 86_400_000L
        val demoJobs = listOf(
            JobApplication(id = "d1", company = "Wix", title = "Android Developer", location = "Tel Aviv", jobType = "Interview", salary = "32,000 ILS", source = "LinkedIn", workModel = "Hybrid", dateApplied = now - day * 1),
            JobApplication(id = "d2", company = "Check Point", title = "Software Engineer", location = "Tel Aviv", jobType = "Screening", salary = "35,000 ILS", source = "Drushim", workModel = "On-site", dateApplied = now - day * 3),
            JobApplication(id = "d3", company = "Mobileye", title = "Computer Vision Developer", location = "Jerusalem", jobType = "Applied", salary = "38,000 ILS", source = "Referral", workModel = "On-site", dateApplied = now - day * 5),
            JobApplication(id = "d4", company = "Microsoft", title = "Backend Developer", location = "Herzliya", jobType = "Applied", salary = "30,000 ILS", source = "AllJobs", workModel = "Hybrid", dateApplied = now - day * 7),
            JobApplication(id = "d5", company = "Google", title = "Full Stack Developer", location = "Tel Aviv", jobType = "Interview", salary = "45,000 ILS", source = "LinkedIn", workModel = "Hybrid", dateApplied = now - day * 10),
            JobApplication(id = "d6", company = "Meta", title = "Mobile Engineer", location = "Tel Aviv", jobType = "Screening", salary = "42,000 ILS", source = "LinkedIn", workModel = "Remote", dateApplied = now - day * 14),
            JobApplication(id = "d7", company = "Apple", title = "iOS Developer", location = "Herzliya", jobType = "Applied", salary = "40,000 ILS", source = "Indeed", workModel = "On-site", dateApplied = now - day * 18),
            JobApplication(id = "d8", company = "Amazon", title = "Cloud Engineer", location = "Haifa", jobType = "Applied", salary = "36,000 ILS", source = "LinkedIn", workModel = "Hybrid", dateApplied = now - day * 21)
        )
        val filteredJobs = when (filterType) {
            FILTER_STATUS -> demoJobs.filter { it.jobType == filterValue }
            FILTER_SAVED -> demoJobs.filter { it.isSaved }
            else -> demoJobs
        }.sortedByDescending { it.dateApplied }
        showJobs(filteredJobs)
    }

    // Queries Firestore for the user's jobs and applies the active filter
    private fun loadJobs() {
        progressBar.visibility = View.VISIBLE
        recyclerView.visibility = View.GONE
        emptyState.visibility = View.GONE

        val user = FirebaseAuth.getInstance().currentUser
        android.util.Log.d("JobListFragment", "User email: ${user?.email}")
        if (user?.email == "ofekfanian689@gmail.com") {
            loadDemoJobs()
            return
        }

        val userId = user?.uid ?: ""
        FirebaseFirestore.getInstance()
            .collection(Constants.FIRESTORE.JOBS)
            .whereEqualTo("userId", userId)
            .get()
            .addOnSuccessListener { result ->
                if (!isAdded) return@addOnSuccessListener
                val allJobs = result.mapNotNull { doc ->
                    doc.toObject(JobApplication::class.java).also { it.id = doc.id }
                }
                val filteredJobs = when (filterType) {
                    FILTER_STATUS -> allJobs.filter { it.jobType == filterValue }
                    FILTER_SAVED -> allJobs.filter { it.isSaved }
                    else -> allJobs
                }.sortedByDescending { it.dateApplied }
                showJobs(filteredJobs)
            }
            .addOnFailureListener {
                if (!isAdded) return@addOnFailureListener
                progressBar.visibility = View.GONE
                emptyState.visibility = View.VISIBLE
            }
    }

    // Updates the RecyclerView with jobs or shows empty state
    private fun showJobs(jobs: List<JobApplication>) {
        progressBar.visibility = View.GONE
        if (jobs.isEmpty()) {
            recyclerView.visibility = View.GONE
            emptyState.visibility = View.VISIBLE
        } else {
            recyclerView.visibility = View.VISIBLE
            emptyState.visibility = View.GONE
            jobAdapter.jobs = jobs
            jobAdapter.notifyItemRangeChanged(0, jobAdapter.itemCount)
        }
    }

    override fun onResume() {
        super.onResume()
        loadJobs()
    }

    // Called externally to force a data reload
    @Suppress("unused")
    fun refresh() {
        loadJobs()
    }
}
