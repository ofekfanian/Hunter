package com.ofek.hunter.activities

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import android.view.animation.AnimationUtils
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.card.MaterialCardView
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.ofek.hunter.R
import com.ofek.hunter.databinding.ActivityAddJobBinding
import com.ofek.hunter.models.JobApplication
import com.ofek.hunter.utilities.AnimationHelper
import com.ofek.hunter.utilities.Constants
import com.ofek.hunter.utilities.DrawerHelper
import com.ofek.hunter.utilities.ValidationHelper

/**
 * Form screen for adding a new job application to Firestore.
 */
class AddJobActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAddJobBinding
    private var selectedStatus = "Applied"
    private var selectedSource = ""
    private var selectedWorkModel = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddJobBinding.inflate(layoutInflater)
        setContentView(binding.root)
        AnimationHelper.enterTransition(this)
        initViews()
    }

    // Set up header, status chips, and save button
    private fun initViews() {
        binding.root.findViewById<TextView>(R.id.header_title)?.apply {
            text = getString(R.string.add_job)
            setShadowLayer(12f, 0f, 0f, getColor(R.color.indigo_glow))
        }
        binding.root.findViewById<ImageView>(R.id.header_screen_icon)?.setImageResource(R.drawable.ic_work)
        // Set up drawer menu navigation
        DrawerHelper.setupDrawer(this, binding.drawerLayout)
        binding.statusChipGroup.setOnCheckedStateChangeListener { _, checkedIds ->
            selectedStatus = if (checkedIds.isNotEmpty()) {
                when (checkedIds[0]) {
                    R.id.chip_screening -> "Screening"
                    R.id.chip_interview -> "Interview"
                    else -> "Applied"
                }
            } else {
                "Applied"
            }
        }
        binding.sourceChipGroup.setOnCheckedStateChangeListener { _, checkedIds ->
            selectedSource = checkedIds.mapNotNull { id ->
                when (id) {
                    R.id.chip_linkedin -> "LinkedIn"
                    R.id.chip_indeed -> "Indeed"
                    R.id.chip_drushim -> "Drushim"
                    R.id.chip_alljobs -> "AllJobs"
                    R.id.chip_referral -> "Referral"
                    R.id.chip_other_source -> "Other"
                    else -> null
                }
            }.joinToString(", ")
        }
        binding.workModelChipGroup.setOnCheckedStateChangeListener { _, checkedIds ->
            selectedWorkModel = checkedIds.mapNotNull { id ->
                when (id) {
                    R.id.chip_onsite -> "On-site"
                    R.id.chip_hybrid -> "Hybrid"
                    R.id.chip_remote -> "Remote"
                    else -> null
                }
            }.joinToString(", ")
        }
        binding.saveButton.setOnClickListener { saveJob() }
    }

    // Validate inputs and save the job to Firestore
    private fun saveJob() {
        val company = binding.companyText.text.toString().trim()
        val position = binding.positionText.text.toString().trim()
        val location = binding.locationText.text.toString().trim()

        if (!ValidationHelper.isNotEmpty(company)) {
            Snackbar.make(binding.root, R.string.error_required, Snackbar.LENGTH_LONG).show()
            return
        }
        if (!ValidationHelper.isNotEmpty(position)) {
            Snackbar.make(binding.root, R.string.error_required, Snackbar.LENGTH_LONG).show()
            return
        }
        if (!ValidationHelper.isNotEmpty(location)) {
            Snackbar.make(binding.root, R.string.error_required, Snackbar.LENGTH_LONG).show()
            return
        }

        binding.saveButton.isEnabled = false

        val userId = FirebaseAuth.getInstance().currentUser?.uid ?: ""
        val job = JobApplication(
            title = position,
            company = company,
            location = location,
            salary = binding.salaryText.text.toString().trim(),
            jobType = selectedStatus,
            description = binding.notesText.text.toString().trim(),
            requirements = binding.cvText.text.toString().trim(),
            userId = userId,
            dateApplied = System.currentTimeMillis(),
            source = selectedSource,
            workModel = selectedWorkModel,
            contactName = binding.contactNameText.text.toString().trim(),
            jobUrl = binding.jobUrlText.text.toString().trim()
        )

        FirebaseFirestore.getInstance()
            .collection(Constants.FIRESTORE.JOBS)
            .add(job)
            .addOnSuccessListener {
                showSuccessDialog(
                    R.string.dialog_success_job_added_title,
                    R.string.dialog_success_job_added_msg
                )
            }
            .addOnFailureListener {
                binding.saveButton.isEnabled = true
                Snackbar.make(binding.root, R.string.error_save, Snackbar.LENGTH_LONG).show()
            }
    }

    // Show an animated success dialog and close the screen after a delay
    private fun showSuccessDialog(titleRes: Int, messageRes: Int) {
        val dialogView = layoutInflater.inflate(R.layout.dialog_success, null)
        dialogView.findViewById<TextView>(R.id.dialog_success_title).setText(titleRes)
        dialogView.findViewById<TextView>(R.id.dialog_success_message).setText(messageRes)

        // Indigo accent for Jobs
        val card = dialogView.findViewById<MaterialCardView>(R.id.dialog_success_card)
        card.strokeColor = getColor(R.color.indigo_stroke)
        dialogView.findViewById<View>(R.id.dialog_success_circle)
            .setBackgroundResource(R.drawable.circle_glass_indigo)
        val icon = dialogView.findViewById<ImageView>(R.id.dialog_success_icon)
        icon.setImageResource(R.drawable.ic_work)
        icon.imageTintList = android.content.res.ColorStateList.valueOf(getColor(R.color.accent_indigo))
        dialogView.findViewById<TextView>(R.id.dialog_success_title)
            .setShadowLayer(10f, 0f, 0f, getColor(R.color.indigo_glow))

        val dialog = AlertDialog.Builder(this, R.style.TransparentDialog)
            .setView(dialogView)
            .setCancelable(false)
            .create()

        dialog.show()
        dialogView.startAnimation(AnimationUtils.loadAnimation(this, R.anim.scale_in))

        Handler(Looper.getMainLooper()).postDelayed({
            if (!isFinishing) {
                dialog.dismiss()
                finish()
            }
        }, 2000)
    }

    override fun finish() {
        super.finish()
        AnimationHelper.exitTransition(this)
    }
}
