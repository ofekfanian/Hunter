package com.ofek.hunter.activities

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import android.view.animation.AnimationUtils
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.card.MaterialCardView
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.firestore.FirebaseFirestore
import com.ofek.hunter.R
import com.ofek.hunter.databinding.ActivityEditJobBinding
import com.ofek.hunter.models.JobApplication
import com.ofek.hunter.utilities.AnimationHelper
import com.ofek.hunter.utilities.Constants
import com.ofek.hunter.utilities.DrawerHelper

/**
 * Edit an existing job application and save changes to Firestore.
 */
class EditJobActivity : AppCompatActivity() {

    private lateinit var binding: ActivityEditJobBinding
    private var currentJob: JobApplication? = null
    private var selectedJobType = "Applied"
    private var selectedSource = ""
    private var selectedWorkModel = ""
    private var jobId: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityEditJobBinding.inflate(layoutInflater)
        setContentView(binding.root)
        jobId = intent.getStringExtra(Constants.EXTRA_JOB_ID) ?: ""
        if (jobId.isEmpty()) {
            Toast.makeText(this, getString(R.string.error_job_not_found), Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        AnimationHelper.enterTransition(this)
        initViews()
        loadJobData()
    }

    // Wire up header, spinner, and buttons
    private fun initViews() {
        setupHeader()
        // Set up drawer menu navigation
        DrawerHelper.setupDrawer(this, binding.drawerLayout)
        setupJobTypeSpinner()
        setupSourceSpinner()
        setupWorkModelSpinner()
        setupButtons()
    }

    override fun finish() {
        super.finish()
        AnimationHelper.exitTransition(this)
    }

    // Set the screen title and icon in the header
    private fun setupHeader() {
        binding.root.findViewById<TextView>(R.id.header_title)?.apply {
            text = getString(R.string.title_edit_job)
            setShadowLayer(12f, 0f, 0f, getColor(R.color.indigo_glow))
        }
        binding.root.findViewById<ImageView>(R.id.header_screen_icon)?.setImageResource(R.drawable.ic_edit)
    }

    // Populate the status spinner and track the selected value
    private fun setupJobTypeSpinner() {
        val statuses = resources.getStringArray(R.array.job_statuses)
        binding.editJobSpinnerStatus.adapter = ArrayAdapter(
            this,
            android.R.layout.simple_spinner_item,
            statuses
        ).apply { setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item) }

        binding.editJobSpinnerStatus.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                selectedJobType = statuses[position]
            }
            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }
    }

    // Populate the source spinner and track the selected value
    private fun setupSourceSpinner() {
        val sources = resources.getStringArray(R.array.job_sources)
        binding.editJobSpinnerSource.adapter = ArrayAdapter(
            this, android.R.layout.simple_spinner_item, sources
        ).apply { setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item) }
        binding.editJobSpinnerSource.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                selectedSource = sources[position]
            }
            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }
    }

    // Populate the work model spinner and track the selected value
    private fun setupWorkModelSpinner() {
        val models = resources.getStringArray(R.array.work_models)
        binding.editJobSpinnerWorkModel.adapter = ArrayAdapter(
            this, android.R.layout.simple_spinner_item, models
        ).apply { setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item) }
        binding.editJobSpinnerWorkModel.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                selectedWorkModel = models[position]
            }
            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }
    }

    // Hook up save and cancel button listeners
    private fun setupButtons() {
        binding.editJobBTNSave.setOnClickListener { updateJob() }
        binding.editJobBTNCancel.setOnClickListener { finish() }
    }

    // Fetch the job document from Firestore by ID
    private fun loadJobData() {
        binding.editJobProgressBar.visibility = View.VISIBLE

        FirebaseFirestore.getInstance()
            .collection(Constants.FIRESTORE.JOBS)
            .document(jobId)
            .get()
            .addOnSuccessListener { doc ->
                val job = doc.toObject(JobApplication::class.java)?.also { it.id = doc.id }
                if (job != null) {
                    binding.editJobProgressBar.visibility = View.GONE
                    currentJob = job
                    populateFields(job)
                } else {
                    binding.editJobProgressBar.visibility = View.GONE
                    Toast.makeText(this, getString(R.string.error_job_not_found), Toast.LENGTH_SHORT).show()
                    finish()
                }
            }
            .addOnFailureListener { ex ->
                binding.editJobProgressBar.visibility = View.GONE
                Toast.makeText(this, getString(R.string.error_generic), Toast.LENGTH_SHORT).show()
                finish()
            }
    }

    // Fill the form fields with the loaded job data
    private fun populateFields(job: JobApplication) {
        binding.companyText.setText(job.company)
        binding.positionText.setText(job.title)
        binding.locationText.setText(job.location)
        binding.salaryText.setText(job.salary)
        binding.cvVersionText.setText(job.requirements)
        binding.notesText.setText(job.description)

        binding.jobUrlText.setText(job.jobUrl)
        binding.contactNameText.setText(job.contactName)

        val statuses = resources.getStringArray(R.array.job_statuses)
        val position = statuses.indexOf(job.jobType)
        if (position >= 0) {
            binding.editJobSpinnerStatus.setSelection(position)
            selectedJobType = job.jobType
        }

        val sources = resources.getStringArray(R.array.job_sources)
        val sourcePos = sources.indexOf(job.source)
        if (sourcePos >= 0) {
            binding.editJobSpinnerSource.setSelection(sourcePos)
            selectedSource = job.source
        }

        val models = resources.getStringArray(R.array.work_models)
        val modelPos = models.indexOf(job.workModel)
        if (modelPos >= 0) {
            binding.editJobSpinnerWorkModel.setSelection(modelPos)
            selectedWorkModel = job.workModel
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
        icon.setImageResource(R.drawable.ic_edit)
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

    // Validate fields and push the updated job to Firestore
    private fun updateJob() {
        val company = binding.companyText.text.toString().trim()
        val position = binding.positionText.text.toString().trim()
        val location = binding.locationText.text.toString().trim()
        val salary = binding.salaryText.text.toString().trim()
        val cvVersion = binding.cvVersionText.text.toString().trim()
        val notes = binding.notesText.text.toString().trim()
        val jobUrl = binding.jobUrlText.text.toString().trim()
        val contactName = binding.contactNameText.text.toString().trim()

        if (company.isEmpty()) {
            Toast.makeText(this, getString(R.string.error_company_required), Toast.LENGTH_SHORT).show()
            return
        }
        if (position.isEmpty()) {
            Toast.makeText(this, getString(R.string.error_position_required), Toast.LENGTH_SHORT).show()
            return
        }

        val updatedJob = currentJob?.copy(
            title = position,
            company = company,
            location = location,
            salary = salary,
            jobType = selectedJobType,
            description = notes,
            requirements = cvVersion,
            source = selectedSource,
            workModel = selectedWorkModel,
            contactName = contactName,
            jobUrl = jobUrl
        ) ?: run {
            Toast.makeText(this, getString(R.string.error_job_not_found), Toast.LENGTH_SHORT).show()
            return
        }

        binding.editJobProgressBar.visibility = View.VISIBLE
        binding.editJobForm.visibility = View.GONE

        FirebaseFirestore.getInstance()
            .collection(Constants.FIRESTORE.JOBS)
            .document(updatedJob.id)
            .set(updatedJob)
            .addOnSuccessListener {
                binding.editJobProgressBar.visibility = View.GONE
                showSuccessDialog(
                    R.string.dialog_success_job_updated_title,
                    R.string.dialog_success_job_updated_msg
                )
            }
            .addOnFailureListener { ex ->
                binding.editJobProgressBar.visibility = View.GONE
                binding.editJobForm.visibility = View.VISIBLE
                Snackbar.make(binding.root, getString(R.string.error_generic), Snackbar.LENGTH_LONG).show()
            }
    }
}
