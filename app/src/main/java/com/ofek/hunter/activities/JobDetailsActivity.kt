package com.ofek.hunter.activities

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.firestore.FirebaseFirestore
import com.ofek.hunter.R
import com.ofek.hunter.databinding.ActivityJobDetailsBinding
import com.ofek.hunter.models.JobApplication
import com.ofek.hunter.utilities.AnimationHelper
import com.ofek.hunter.utilities.Constants
import com.ofek.hunter.utilities.DrawerHelper

/**
 * Display full details of a job application with share and delete.
 */
class JobDetailsActivity : AppCompatActivity() {

    private lateinit var binding: ActivityJobDetailsBinding
    private var currentJob: JobApplication? = null
    private var jobId: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityJobDetailsBinding.inflate(layoutInflater)
        setContentView(binding.root)
        AnimationHelper.enterTransition(this)
        binding.root.findViewById<TextView>(R.id.header_title)?.apply {
            text = getString(R.string.job_details_title)
            setShadowLayer(12f, 0f, 0f, getColor(R.color.indigo_glow))
        }
        binding.root.findViewById<ImageView>(R.id.header_screen_icon)?.setImageResource(R.drawable.ic_work)
        // Set up drawer menu navigation
        DrawerHelper.setupDrawer(this, binding.drawerLayout)
        jobId = intent.getStringExtra(Constants.EXTRA_JOB_ID) ?: ""
        if (jobId.isEmpty()) { finish(); return }
        loadJobData()
    }

    override fun finish() {
        super.finish()
        AnimationHelper.exitTransition(this)
    }

    // Fetch the job document from Firestore and display it
    private fun loadJobData() {
        FirebaseFirestore.getInstance()
            .collection(Constants.FIRESTORE.JOBS)
            .document(jobId)
            .get()
            .addOnSuccessListener { doc ->
                val job = doc.toObject(JobApplication::class.java)?.also { it.id = doc.id }
                if (job != null) {
                    currentJob = job
                    displayJobData(job)
                } else {
                    Toast.makeText(this, getString(R.string.error_load_job), Toast.LENGTH_SHORT).show()
                    finish()
                }
            }
            .addOnFailureListener { e ->
                Toast.makeText(this, "${getString(R.string.error_load_job)}: ${e.message}", Toast.LENGTH_SHORT).show()
                finish()
            }
    }

    // Bind the job fields to the detail card views
    private fun displayJobData(job: JobApplication) {
        findViewById<TextView>(R.id.profile_title)?.text = job.company
        findViewById<TextView>(R.id.profile_subtitle)?.text = job.title

        // Job info card rows
        binding.detailLocation.text = job.location.ifEmpty { "N/A" }
        binding.detailStatus.text = job.jobType

        if (job.salary.isNotEmpty()) {
            binding.detailSalary.text = job.salary
        } else {
            binding.detailSalaryRow.visibility = View.GONE
        }

        if (job.dateApplied > 0) {
            binding.detailDate.text = android.text.format.DateFormat.format("dd/MM/yyyy", job.dateApplied).toString()
        } else {
            binding.detailDateRow.visibility = View.GONE
        }

        if (job.source.isNotEmpty()) {
            binding.detailSource.text = job.source
        } else {
            binding.detailSourceRow.visibility = View.GONE
        }

        if (job.workModel.isNotEmpty()) {
            binding.detailWorkModel.text = job.workModel
        } else {
            binding.detailWorkModelRow.visibility = View.GONE
        }

        if (job.contactName.isNotEmpty()) {
            binding.detailContact.text = job.contactName
        } else {
            binding.detailContactRow.visibility = View.GONE
        }

        // Existing detail cards
        binding.cardTextUrl.text = job.jobUrl.ifEmpty { getString(R.string.url_not_available) }
        binding.cardTextCv.text = job.requirements.ifEmpty { "${getString(R.string.cv_format)} - ${job.company}" }
        binding.cardTextNotes.text = job.description.ifEmpty { getString(R.string.notes_not_available) }
        binding.btnShareFeature.setOnClickListener { shareJob() }
        binding.btnDeleteFeature.setOnClickListener { showDeleteConfirmation() }
    }

    // Show a confirmation dialog before deleting the job
    private fun showDeleteConfirmation() {
        AlertDialog.Builder(this)
            .setTitle(R.string.dialog_delete_title)
            .setMessage(R.string.dialog_delete_message)
            .setPositiveButton(R.string.btn_delete) { _, _ -> deleteJob() }
            .setNegativeButton(R.string.btn_cancel, null)
            .show()
    }

    // Delete the job document from Firestore and close the screen
    private fun deleteJob() {
        FirebaseFirestore.getInstance()
            .collection(Constants.FIRESTORE.JOBS)
            .document(jobId)
            .delete()
            .addOnSuccessListener {
                Toast.makeText(this, getString(R.string.success_job_deleted), Toast.LENGTH_SHORT).show()
                finish()
            }
            .addOnFailureListener { e ->
                Snackbar.make(binding.root, "${getString(R.string.error_delete_job)}: ${e.message}", Snackbar.LENGTH_LONG).show()
            }
    }

    // Build a share intent with the job details
    private fun shareJob() {
        currentJob?.let { job ->
            val dateStr = if (job.dateApplied > 0) android.text.format.DateFormat.format("dd/MM/yyyy", job.dateApplied).toString() else ""
            val shareText = buildString {
                appendLine("${job.company} - ${job.title}")
                if (job.location.isNotEmpty()) appendLine(job.location)
                if (job.workModel.isNotEmpty()) appendLine(job.workModel)
                if (job.salary.isNotEmpty()) appendLine(job.salary)
                if (dateStr.isNotEmpty()) appendLine("Applied: $dateStr")
                if (job.source.isNotEmpty()) appendLine("Source: ${job.source}")
                if (job.contactName.isNotEmpty()) appendLine("Contact: ${job.contactName}")
                if (job.jobUrl.isNotEmpty()) appendLine(job.jobUrl)
                if (job.description.isNotEmpty()) appendLine(job.description)
            }
            val shareIntent = Intent().apply {
                action = Intent.ACTION_SEND
                type = "text/plain"
                putExtra(Intent.EXTRA_TEXT, shareText)
                putExtra(Intent.EXTRA_SUBJECT, "${getString(R.string.share_job_subject)}: ${job.title}")
            }
            startActivity(Intent.createChooser(shareIntent, getString(R.string.share_job_via)))
        }
    }
}
