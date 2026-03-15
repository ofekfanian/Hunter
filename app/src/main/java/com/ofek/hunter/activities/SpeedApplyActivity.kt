package com.ofek.hunter.activities

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import android.view.animation.AnimationUtils
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.card.MaterialCardView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.ofek.hunter.R
import com.ofek.hunter.databinding.ActivitySpeedApplyBinding
import com.ofek.hunter.models.JobApplication
import com.ofek.hunter.utilities.AnimationHelper
import com.ofek.hunter.utilities.Constants
import com.ofek.hunter.utilities.DrawerHelper

/**
 * Quick-save a job from shared links or text into the tracker.
 */
class SpeedApplyActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySpeedApplyBinding
    private var extractedCompany: String = ""
    private var extractedTitle: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySpeedApplyBinding.inflate(layoutInflater)
        setContentView(binding.root)
        AnimationHelper.enterTransition(this)
        initViews()
        handleIncomingIntent()
    }

    // Set up header, skip/save buttons, and back press handling
    private fun initViews() {
        val headerTitle = binding.root.findViewById<TextView>(R.id.header_title)
        headerTitle?.text = getString(R.string.speed_apply_title)
        headerTitle?.setShadowLayer(12f, 0f, 0f, getColor(R.color.indigo_glow))
        binding.root.findViewById<ImageView>(R.id.header_screen_icon)?.setImageResource(R.drawable.ic_flash)

        // Set up drawer menu navigation
        DrawerHelper.setupDrawer(this, binding.speedApplyDrawerLayout)

        // Skip button
        binding.speedApplyBtnSkip.setOnClickListener { finish() }

        // Save button
        binding.speedApplyBtnSave.setOnClickListener { saveJob() }
    }

    override fun finish() {
        super.finish()
        AnimationHelper.exitTransition(this)
    }

    // Check if the activity was launched via ACTION_SEND or ACTION_VIEW
    private fun handleIncomingIntent() {
        when (intent?.action) {
            Intent.ACTION_SEND -> {
                if (intent.type == "text/plain") {
                    intent.getStringExtra(Intent.EXTRA_TEXT)?.let { extractJobInfo(it) }
                }
            }
            Intent.ACTION_VIEW -> {
                intent.data?.let { extractJobInfo(it.toString()) }
            }
        }
    }

    // Parse the shared text for URL, company, and position
    private fun extractJobInfo(text: String) {
        val urlRegex = Regex("https?://\\S+")
        val url = urlRegex.find(text)?.value ?: ""

        when {
            url.contains("linkedin.com") -> binding.speedApplyTXTSource.text = getString(R.string.source_linkedin)
            url.contains("indeed.com") -> binding.speedApplyTXTSource.text = getString(R.string.source_indeed)
            url.contains("drushim.co.il") -> binding.speedApplyTXTSource.text = getString(R.string.source_drushim)
            url.contains("alljobs.co.il") -> binding.speedApplyTXTSource.text = getString(R.string.source_alljobs)
            url.isNotEmpty() -> binding.speedApplyTXTSource.text = getString(R.string.source_web_link)
            else -> binding.speedApplyTXTSource.text = getString(R.string.source_shared_text)
        }

        val lines = text.split("\n").filter { it.isNotBlank() }
        if (lines.isNotEmpty()) extractedTitle = lines[0]
        if (lines.size > 1) extractedCompany = lines[1]

        binding.companyText.setText(extractedCompany)
        binding.positionText.setText(extractedTitle)
        binding.urlText.setText(url)
    }

    // Validate fields and save the job application to Firestore
    private fun saveJob() {
        val company = binding.companyText.text.toString().trim()
        val position = binding.positionText.text.toString().trim()
        val location = binding.locationText.text.toString().trim()
        val url = binding.urlText.text.toString().trim()

        if (company.isEmpty() || position.isEmpty()) {
            Toast.makeText(this, getString(R.string.error_required_fields), Toast.LENGTH_SHORT).show()
            return
        }

        showLoading(true)

        val description = if (url.isNotEmpty()) {
            "${getString(R.string.speed_apply_description)}\n$url"
        } else {
            getString(R.string.speed_apply_description)
        }

        val job = JobApplication(
            id = "",
            title = position,
            company = company,
            location = location.ifEmpty { getString(R.string.location_remote) },
            salary = "",
            jobType = getString(R.string.job_type_saved),
            description = description,
            requirements = "",
            isSaved = true,
            userId = FirebaseAuth.getInstance().currentUser?.uid ?: ""
        )

        FirebaseFirestore.getInstance()
            .collection(Constants.FIRESTORE.JOBS)
            .add(job)
            .addOnSuccessListener {
                showLoading(false)
                showSuccessDialog(
                    R.string.dialog_success_speed_apply_title,
                    R.string.dialog_success_speed_apply_msg
                )
            }
            .addOnFailureListener {
                showLoading(false)
                Toast.makeText(this, getString(R.string.error_save_failed), Toast.LENGTH_SHORT).show()
            }
    }

    // Show an indigo-accented success dialog, then finish the activity
    private fun showSuccessDialog(titleRes: Int, messageRes: Int) {
        val dialogView = layoutInflater.inflate(R.layout.dialog_success, null)
        dialogView.findViewById<TextView>(R.id.dialog_success_title).setText(titleRes)
        dialogView.findViewById<TextView>(R.id.dialog_success_message).setText(messageRes)

        // Indigo accent for SpeedApply (Jobs)
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

    // Toggle progress bar and disable save/skip buttons
    private fun showLoading(show: Boolean) {
        binding.speedApplyProgressBar.visibility = if (show) View.VISIBLE else View.GONE
        binding.speedApplyBtnSave.isEnabled = !show
        binding.speedApplyBtnSkip.isEnabled = !show
    }
}
