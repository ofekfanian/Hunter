package com.ofek.hunter.activities

import android.app.AlarmManager
import android.app.DatePickerDialog
import android.app.PendingIntent
import android.app.TimePickerDialog
import android.content.Context
import android.content.Intent
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
import com.ofek.hunter.databinding.ActivityAddInterviewBinding
import com.ofek.hunter.models.Interview
import com.ofek.hunter.receivers.InterviewReminderReceiver
import com.ofek.hunter.utilities.AnimationHelper
import com.ofek.hunter.utilities.Constants
import com.ofek.hunter.utilities.DateTimeHelper
import com.ofek.hunter.utilities.DrawerHelper
import com.ofek.hunter.utilities.ValidationHelper
import java.util.*

/**
 * Form screen for scheduling a new interview with reminders.
 */
class AddInterviewActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAddInterviewBinding
    private var selectedDate = 0L
    private var selectedType = "Phone"
    private var selectedReminder = 60

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityAddInterviewBinding.inflate(layoutInflater)
        setContentView(binding.root)
        AnimationHelper.enterTransition(this)
        initViews()
    }

    // Wire up header, chips, date/time pickers, and save button
    private fun initViews() {
        setupHeader()
        // Set up drawer menu navigation
        DrawerHelper.setupDrawer(this, binding.drawerLayout)
        setupChips()
        setupPickers()
        setupSaveButton()
    }

    // Set the screen title and icon in the header
    private fun setupHeader() {
        binding.root.findViewById<TextView>(R.id.header_title)?.apply {
            text = getString(R.string.add_interview)
            setShadowLayer(12f, 0f, 0f, getColor(R.color.blue_glow))
        }
        binding.root.findViewById<ImageView>(R.id.header_screen_icon)?.setImageResource(R.drawable.ic_calendar)
    }

    // Handle interview type and reminder chip selections
    private fun setupChips() {
        binding.typeChipGroup.setOnCheckedStateChangeListener { _, checkedIds ->
            if (checkedIds.isNotEmpty()) {
                selectedType = when (checkedIds[0]) {
                    R.id.chip_video -> "Video"
                    R.id.chip_in_person -> "In Person"
                    else -> "Phone"
                }
            }
        }

        binding.reminderChipGroup.setOnCheckedStateChangeListener { _, checkedIds ->
            if (checkedIds.isNotEmpty()) {
                selectedReminder = when (checkedIds[0]) {
                    R.id.chip_30min -> 30
                    R.id.chip_1day -> 1440
                    else -> 60
                }
            }
        }
    }

    // Make date and time fields open picker dialogs on click
    private fun setupPickers() {
        binding.dateText.apply {
            isFocusable = false
            isClickable = true
            setOnClickListener {
                showDatePicker()
            }
        }

        binding.timeText.apply {
            isFocusable = false
            isClickable = true
            setOnClickListener {
                showTimePicker()
            }
        }
    }

    // Open a date picker dialog and store the selected date
    private fun showDatePicker() {
        val cal = Calendar.getInstance()
        DatePickerDialog(this, { _, y, m, d ->
            cal.set(y, m, d)
            selectedDate = cal.timeInMillis
            binding.dateText.setText(DateTimeHelper.formatDateShort(selectedDate))
        }, cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal.get(Calendar.DAY_OF_MONTH)).show()
    }

    // Open a time picker dialog and update the selected time
    private fun showTimePicker() {
        val cal = Calendar.getInstance()
        TimePickerDialog(this, { _, h, m ->
            if (selectedDate != 0L) {
                cal.timeInMillis = selectedDate
                cal.set(Calendar.HOUR_OF_DAY, h)
                cal.set(Calendar.MINUTE, m)
                selectedDate = cal.timeInMillis
            }
            binding.timeText.setText(DateTimeHelper.formatTime(cal.timeInMillis))
        }, cal.get(Calendar.HOUR_OF_DAY), cal.get(Calendar.MINUTE), true).show()
    }

    // Attach the save click listener
    private fun setupSaveButton() {
        binding.saveButton.setOnClickListener {
            saveInterview()
        }
    }

    // Validate inputs and save the interview to Firestore
    private fun saveInterview() {
        val company = binding.companyText.text.toString().trim()
        val position = binding.positionText.text.toString().trim()

        if (!ValidationHelper.isNotEmpty(company)) {
            showError(R.string.error_required)
            return
        }

        if (!ValidationHelper.isNotEmpty(position)) {
            showError(R.string.error_required)
            return
        }

        if (selectedDate == 0L) {
            showError(R.string.error_required)
            return
        }

        binding.saveButton.isEnabled = false

        val location = binding.locationText.text.toString().trim()
        val interviewer = binding.interviewerText.text.toString().trim()
        val notes = binding.notesText.text.toString().trim()

        val interview = Interview(
            jobId = "",
            companyName = company,
            position = position,
            dateTime = selectedDate,
            location = location.ifEmpty { getString(R.string.not_specified) },
            interviewerName = interviewer.ifEmpty { getString(R.string.not_specified) },
            notes = notes,
            type = selectedType,
            reminderTime = selectedReminder
        )

        val userId = FirebaseAuth.getInstance().currentUser?.uid ?: ""
        FirebaseFirestore.getInstance()
            .collection(Constants.FIRESTORE.INTERVIEWS)
            .add(interview.copy(userId = userId))
            .addOnSuccessListener { docRef ->
                scheduleReminder(docRef.id, interview)
                showSuccessDialog(
                    R.string.dialog_success_interview_added_title,
                    R.string.dialog_success_interview_added_msg
                )
            }
            .addOnFailureListener {
                binding.saveButton.isEnabled = true
            }
    }

    // Schedule an alarm to remind the user before the interview
    private fun scheduleReminder(interviewId: String, interview: Interview) {
        val reminderTimeMs = interview.dateTime - (interview.reminderTime * 60 * 1000L)

        if (reminderTimeMs <= System.currentTimeMillis()) return

        val intent = Intent(this, InterviewReminderReceiver::class.java).apply {
            putExtra(InterviewReminderReceiver.EXTRA_COMPANY, interview.companyName)
            putExtra(InterviewReminderReceiver.EXTRA_POSITION, interview.position)
            putExtra(InterviewReminderReceiver.EXTRA_INTERVIEW_ID, interviewId)
        }

        val pendingIntent = PendingIntent.getBroadcast(
            this,
            interviewId.hashCode(),
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val alarmManager = getSystemService(Context.ALARM_SERVICE) as AlarmManager
        try {
            alarmManager.setExactAndAllowWhileIdle(
                AlarmManager.RTC_WAKEUP,
                reminderTimeMs,
                pendingIntent
            )
        } catch (_: SecurityException) {
            // SCHEDULE_EXACT_ALARM not granted on Android 12+ — fall back silently
        }
    }

    // Show an animated success dialog and close the screen after a delay
    private fun showSuccessDialog(titleRes: Int, messageRes: Int) {
        val dialogView = layoutInflater.inflate(R.layout.dialog_success, null)
        dialogView.findViewById<TextView>(R.id.dialog_success_title).setText(titleRes)
        dialogView.findViewById<TextView>(R.id.dialog_success_message).setText(messageRes)

        // Blue accent for Interviews
        val card = dialogView.findViewById<MaterialCardView>(R.id.dialog_success_card)
        card.strokeColor = getColor(R.color.blue_stroke)
        dialogView.findViewById<View>(R.id.dialog_success_circle)
            .setBackgroundResource(R.drawable.circle_glass_blue)
        val icon = dialogView.findViewById<ImageView>(R.id.dialog_success_icon)
        icon.setImageResource(R.drawable.ic_calendar)
        icon.imageTintList = android.content.res.ColorStateList.valueOf(getColor(R.color.stat_accent_blue))
        dialogView.findViewById<TextView>(R.id.dialog_success_title)
            .setShadowLayer(10f, 0f, 0f, getColor(R.color.blue_glow))

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

    // Display a snackbar with the given error message
    private fun showError(resId: Int) {
        Snackbar.make(binding.root, resId, Snackbar.LENGTH_LONG).show()
    }

    override fun finish() {
        super.finish()
        AnimationHelper.exitTransition(this)
    }
}
