package com.ofek.hunter.activities

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
import com.google.android.material.button.MaterialButton
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.ofek.hunter.R
import com.ofek.hunter.databinding.ActivityAddQuestionBinding
import com.ofek.hunter.models.InterviewQuestion
import com.ofek.hunter.utilities.AnimationHelper
import com.ofek.hunter.utilities.Constants
import com.ofek.hunter.utilities.DrawerHelper

/**
 * Form for posting a new question or tip to the community.
 */
class AddQuestionActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAddQuestionBinding
    private var isAnonymous = false
    private var postType = InterviewQuestion.TYPE_QUESTION
    private val selectedCategories = mutableSetOf("Technical")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddQuestionBinding.inflate(layoutInflater)
        setContentView(binding.root)
        AnimationHelper.enterTransition(this)
        initViews()
    }

    // Wire up header, category chips, and submit button
    private fun initViews() {
        setupHeader()
        // Set up drawer menu navigation
        DrawerHelper.setupDrawer(this, binding.drawerLayout)
        setupChips()
        binding.submitButton.setOnClickListener { handleSubmit() }
    }

    // Configure the screen header title and icon
    private fun setupHeader() {
        binding.root.findViewById<TextView>(R.id.header_title)?.apply {
            text = getString(R.string.add_question_title)
            setShadowLayer(12f, 0f, 0f, getColor(R.color.rose_glow))
        }
        binding.root.findViewById<ImageView>(R.id.header_screen_icon)?.setImageResource(R.drawable.ic_edit)
    }

    // Set up type toggle, anonymity switch, and category chip listeners
    private fun setupChips() {
        binding.typeToggle.addOnButtonCheckedListener { _, checkedId, isChecked ->
            if (isChecked) {
                postType = if (checkedId == R.id.btn_type_tip) InterviewQuestion.TYPE_TIP
                           else InterviewQuestion.TYPE_QUESTION
            }
        }

        binding.anonymityChips.setOnCheckedChangeListener { _, checked ->
            isAnonymous = checked
        }

        listOf(
            findViewById<MaterialButton>(R.id.chip_technical) to "Technical",
            findViewById<MaterialButton>(R.id.chip_behavioral) to "Behavioral",
            findViewById<MaterialButton>(R.id.chip_situational) to "Situational",
            findViewById<MaterialButton>(R.id.chip_system_design) to "System Design",
            findViewById<MaterialButton>(R.id.chip_leadership) to "Leadership",
            findViewById<MaterialButton>(R.id.chip_hr_culture) to "HR / Culture",
            findViewById<MaterialButton>(R.id.chip_problem_solving) to "Problem Solving",
            findViewById<MaterialButton>(R.id.chip_coding) to "Coding"
        ).forEach { (chip, category) ->
            chip.addOnCheckedChangeListener { _, isChecked ->
                if (isChecked) selectedCategories.add(category) else selectedCategories.remove(category)
            }
        }
    }

    // Validate all required fields before saving
    private fun handleSubmit() {
        val company = binding.companyText.text.toString().trim()
        val position = binding.positionText.text.toString().trim()
        val question = binding.questionText.text.toString().trim()
        val answer = binding.answerText.text.toString().trim()

        if (company.isEmpty()) {
            Toast.makeText(this, R.string.error_company_required, Toast.LENGTH_SHORT).show()
            return
        }
        if (position.isEmpty()) {
            Toast.makeText(this, R.string.error_position_required, Toast.LENGTH_SHORT).show()
            return
        }
        if (question.isEmpty()) {
            Toast.makeText(this, R.string.error_question_required, Toast.LENGTH_SHORT).show()
            return
        }
        if (selectedCategories.isEmpty()) {
            Toast.makeText(this, R.string.error_category_required, Toast.LENGTH_SHORT).show()
            return
        }

        saveQuestion(company, position, question, answer)
    }

    // Build the InterviewQuestion object and push it to Firestore
    private fun saveQuestion(company: String, position: String, question: String, answer: String) {
        showLoading(true)

        val currentUser = FirebaseAuth.getInstance().currentUser
        val userId = currentUser?.uid ?: ""
        val userName = if (isAnonymous) "" else (currentUser?.displayName ?: "")
        val q = InterviewQuestion(
            companyName = company,
            position = position,
            question = question,
            answer = answer,
            categories = selectedCategories.toList(),
            isAnonymous = isAnonymous,
            userId = userId,
            userName = userName,
            type = postType
        )
        FirebaseFirestore.getInstance()
            .collection(Constants.FIRESTORE.QUESTIONS)
            .add(q)
            .addOnSuccessListener {
                showLoading(false)
                showSuccessDialog(
                    R.string.dialog_success_question_published_title,
                    R.string.dialog_success_question_published_msg
                )
            }
            .addOnFailureListener {
                showLoading(false)
                Toast.makeText(this, getString(R.string.error_generic), Toast.LENGTH_SHORT).show()
            }
    }

    // Toggle progress bar and disable the submit button
    private fun showLoading(show: Boolean) {
        binding.progressBar.visibility = if (show) View.VISIBLE else View.GONE
        binding.submitButton.isEnabled = !show
    }

    // Show a rose-accented success dialog, then finish the activity
    private fun showSuccessDialog(titleRes: Int, messageRes: Int) {
        val dialogView = layoutInflater.inflate(R.layout.dialog_success, null)
        dialogView.findViewById<TextView>(R.id.dialog_success_title).setText(titleRes)
        dialogView.findViewById<TextView>(R.id.dialog_success_message).setText(messageRes)

        // Rose accent for Community
        val card = dialogView.findViewById<MaterialCardView>(R.id.dialog_success_card)
        card.strokeColor = getColor(R.color.rose_stroke)
        dialogView.findViewById<View>(R.id.dialog_success_circle)
            .setBackgroundResource(R.drawable.circle_glass_rose)
        val icon = dialogView.findViewById<ImageView>(R.id.dialog_success_icon)
        icon.setImageResource(R.drawable.ic_community)
        icon.imageTintList = android.content.res.ColorStateList.valueOf(getColor(R.color.accent_rose_light))
        dialogView.findViewById<TextView>(R.id.dialog_success_title)
            .setShadowLayer(10f, 0f, 0f, getColor(R.color.rose_glow))

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
