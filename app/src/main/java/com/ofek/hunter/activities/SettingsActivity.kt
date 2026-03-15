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
import androidx.core.content.edit
import com.google.android.material.card.MaterialCardView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.ofek.hunter.R
import com.ofek.hunter.databinding.ActivitySettingsBinding
import com.ofek.hunter.utilities.AnimationHelper
import com.ofek.hunter.utilities.Constants
import com.ofek.hunter.utilities.DrawerHelper
import com.ofek.hunter.utilities.NavigationHelper

/**
 * App settings with notifications toggle, demo data, and logout.
 */
class SettingsActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySettingsBinding
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySettingsBinding.inflate(layoutInflater)
        setContentView(binding.root)
        auth = FirebaseAuth.getInstance()
        AnimationHelper.enterTransition(this)
        initViews()
        loadUserData()
    }

    // Set up header, nav, cards, notification switch, and logout button
    private fun initViews() {
        binding.root.findViewById<TextView>(R.id.header_title)?.apply {
            text = getString(R.string.settings_title)
            setShadowLayer(12f, 0f, 0f, getColor(R.color.purple_glow))
        }
        binding.root.findViewById<ImageView>(R.id.header_screen_icon)?.setImageResource(R.drawable.ic_settings)
        NavigationHelper.setupBottomNavigation(this, "profile")
        // Set up drawer menu navigation
        DrawerHelper.setupDrawer(this, binding.settingsDrawerLayout)

        binding.settingsCardProfile.setOnClickListener {
            startActivity(Intent(this, ProfileActivity::class.java))
        }

        val prefs = getSharedPreferences("hunter_settings", MODE_PRIVATE)
        binding.settingsSwitchNotifications.isChecked = prefs.getBoolean("notifications_enabled", true)
        binding.settingsSwitchNotifications.setOnCheckedChangeListener { _, isChecked ->
            prefs.edit {
                putBoolean("notifications_enabled", isChecked)
            }
            val msg = if (isChecked) R.string.notifications_enabled_msg else R.string.notifications_disabled_msg
            Toast.makeText(this, getString(msg), Toast.LENGTH_SHORT).show()
        }

        binding.settingsCardAbout.setOnClickListener {
            startActivity(Intent(this, AboutActivity::class.java))
        }

        binding.settingsCardDemoData.setOnClickListener { confirmLoadDemoData() }

        binding.settingsCardBtnLogout.setOnClickListener { handleLogout() }
    }

    override fun finish() {
        super.finish()
        AnimationHelper.exitTransition(this)
    }

    // Display the current user's name and email in the profile card
    private fun loadUserData() {
        val user = auth.currentUser
        binding.profileTitle.text = user?.displayName ?: getString(R.string.default_user_name)
        binding.profileSubtitle.text = user?.email ?: getString(R.string.no_email)
    }

    // Ask the user to confirm before loading sample data
    private fun confirmLoadDemoData() {
        AlertDialog.Builder(this, R.style.TransparentDialog)
            .setTitle(getString(R.string.demo_data_confirm_title))
            .setMessage(getString(R.string.demo_data_confirm_msg))
            .setPositiveButton(getString(R.string.btn_yes)) { _, _ -> loadDemoData() }
            .setNegativeButton(getString(R.string.btn_no), null)
            .show()
    }

    // Insert sample jobs, interviews, questions, CVs, and profile into Firestore
    private fun loadDemoData() {
        val userId = auth.currentUser?.uid ?: return
        val db = FirebaseFirestore.getInstance()
        val now = System.currentTimeMillis()
        val day = 86_400_000L

        // ── Sample Jobs ──
        val jobs = listOf(
            mapOf("title" to "Android Developer", "company" to "Google", "location" to "Tel Aviv", "salary" to "35,000 ILS", "jobType" to "Interview", "description" to "Develop Android apps using Kotlin", "requirements" to "3+ years Android, Kotlin, MVVM", "isSaved" to true, "userId" to userId, "source" to "LinkedIn", "workModel" to "Hybrid", "dateApplied" to (now - 5 * day)),
            mapOf("title" to "Frontend Developer", "company" to "Meta", "location" to "Ramat Gan", "salary" to "30,000 ILS", "jobType" to "Applied", "description" to "Build React web applications", "requirements" to "React, TypeScript, CSS", "isSaved" to false, "userId" to userId, "source" to "LinkedIn", "workModel" to "On-site", "dateApplied" to (now - 3 * day)),
            mapOf("title" to "Full Stack Developer", "company" to "Wix", "location" to "Tel Aviv", "salary" to "32,000 ILS", "jobType" to "Offer", "description" to "Full stack web development", "requirements" to "Node.js, React, MongoDB", "isSaved" to true, "userId" to userId, "source" to "Referral", "workModel" to "Hybrid", "dateApplied" to (now - 14 * day)),
            mapOf("title" to "iOS Developer", "company" to "Apple", "location" to "Herzliya", "salary" to "38,000 ILS", "jobType" to "Applied", "description" to "Develop iOS applications using Swift", "requirements" to "Swift, UIKit, SwiftUI", "isSaved" to false, "userId" to userId, "source" to "Indeed", "workModel" to "On-site", "dateApplied" to (now - 2 * day)),
            mapOf("title" to "Backend Developer", "company" to "Microsoft", "location" to "Herzliya", "salary" to "34,000 ILS", "jobType" to "Interview", "description" to "Build scalable backend services", "requirements" to "C#, .NET, Azure", "isSaved" to false, "userId" to userId, "source" to "LinkedIn", "workModel" to "Hybrid", "dateApplied" to (now - 7 * day)),
            mapOf("title" to "DevOps Engineer", "company" to "Amazon", "location" to "Haifa", "salary" to "36,000 ILS", "jobType" to "Applied", "description" to "Manage CI/CD pipelines and cloud infrastructure", "requirements" to "AWS, Docker, Kubernetes", "isSaved" to true, "userId" to userId, "source" to "Drushim", "workModel" to "Remote", "dateApplied" to (now - 1 * day)),
            mapOf("title" to "QA Engineer", "company" to "Monday.com", "location" to "Tel Aviv", "salary" to "25,000 ILS", "jobType" to "Applied", "description" to "Manual and automated testing", "requirements" to "Selenium, Python, API testing", "isSaved" to false, "userId" to userId, "source" to "AllJobs", "workModel" to "On-site", "dateApplied" to (now - 4 * day)),
            mapOf("title" to "Data Analyst", "company" to "Fiverr", "location" to "Tel Aviv", "salary" to "28,000 ILS", "jobType" to "Interview", "description" to "Analyze business data and create reports", "requirements" to "SQL, Python, Tableau", "isSaved" to false, "userId" to userId, "source" to "LinkedIn", "workModel" to "Hybrid", "dateApplied" to (now - 6 * day)),
            mapOf("title" to "ML Engineer", "company" to "NVIDIA", "location" to "Yokneam", "salary" to "42,000 ILS", "jobType" to "Applied", "description" to "Design and train deep learning models for GPU optimization", "requirements" to "Python, PyTorch, CUDA, C++", "isSaved" to true, "userId" to userId, "source" to "LinkedIn", "workModel" to "On-site", "dateApplied" to (now - 2 * day)),
            mapOf("title" to "Cloud Architect", "company" to "CyberArk", "location" to "Petah Tikva", "salary" to "40,000 ILS", "jobType" to "Interview", "description" to "Design secure cloud infrastructure solutions", "requirements" to "AWS, Azure, Terraform, Security", "isSaved" to false, "userId" to userId, "source" to "Referral", "workModel" to "Hybrid", "dateApplied" to (now - 8 * day)),
            mapOf("title" to "React Native Developer", "company" to "ironSource", "location" to "Tel Aviv", "salary" to "33,000 ILS", "jobType" to "Rejected", "description" to "Build cross-platform mobile apps", "requirements" to "React Native, JavaScript, Redux", "isSaved" to false, "userId" to userId, "source" to "Indeed", "workModel" to "Hybrid", "dateApplied" to (now - 20 * day)),
            mapOf("title" to "Site Reliability Engineer", "company" to "Check Point", "location" to "Tel Aviv", "salary" to "37,000 ILS", "jobType" to "Applied", "description" to "Maintain and scale production systems", "requirements" to "Linux, Kubernetes, Monitoring, Python", "isSaved" to false, "userId" to userId, "source" to "Drushim", "workModel" to "On-site", "dateApplied" to (now - 3 * day)),
            mapOf("title" to "Product Manager", "company" to "Playtika", "location" to "Herzliya", "salary" to "30,000 ILS", "jobType" to "Applied", "description" to "Lead mobile gaming product features", "requirements" to "Product strategy, A/B testing, Analytics", "isSaved" to true, "userId" to userId, "source" to "LinkedIn", "workModel" to "Hybrid", "dateApplied" to (now - 1 * day)),
            mapOf("title" to "Security Researcher", "company" to "Palo Alto Networks", "location" to "Tel Aviv", "salary" to "39,000 ILS", "jobType" to "Offer", "description" to "Research and analyze cyber threats", "requirements" to "Reverse engineering, Malware analysis, Python", "isSaved" to true, "userId" to userId, "source" to "Referral", "workModel" to "On-site", "dateApplied" to (now - 18 * day)),
            mapOf("title" to "Data Engineer", "company" to "Taboola", "location" to "Tel Aviv", "salary" to "31,000 ILS", "jobType" to "Interview", "description" to "Build and maintain data pipelines at scale", "requirements" to "Spark, Kafka, Airflow, SQL", "isSaved" to false, "userId" to userId, "source" to "AllJobs", "workModel" to "Hybrid", "dateApplied" to (now - 9 * day)),
            mapOf("title" to "Embedded Software Engineer", "company" to "Mobileye", "location" to "Jerusalem", "salary" to "35,000 ILS", "jobType" to "Applied", "description" to "Develop firmware for autonomous driving systems", "requirements" to "C, C++, RTOS, Computer Vision", "isSaved" to false, "userId" to userId, "source" to "LinkedIn", "workModel" to "On-site", "dateApplied" to (now - 4 * day)),
            mapOf("title" to "UX Designer", "company" to "Lightricks", "location" to "Jerusalem", "salary" to "27,000 ILS", "jobType" to "Rejected", "description" to "Design intuitive user experiences for creative apps", "requirements" to "Figma, User Research, Prototyping", "isSaved" to false, "userId" to userId, "source" to "LinkedIn", "workModel" to "Hybrid", "dateApplied" to (now - 25 * day)),
            mapOf("title" to "Blockchain Developer", "company" to "StarkWare", "location" to "Netanya", "salary" to "45,000 ILS", "jobType" to "Applied", "description" to "Develop ZK-proof systems and smart contracts", "requirements" to "Solidity, Cairo, Cryptography, Rust", "isSaved" to true, "userId" to userId, "source" to "Referral", "workModel" to "Remote", "dateApplied" to (now - 1 * day))
        )

        for (job in jobs) {
            db.collection(Constants.FIRESTORE.JOBS).add(job)
        }

        // ── Sample Interviews ──
        val interviews = listOf(
            mapOf("companyName" to "Google", "position" to "Android Developer", "dateTime" to (now + 3 * day), "location" to "Google Tel Aviv Office", "interviewerName" to "David Cohen", "notes" to "Prepare system design questions", "type" to "In Person", "reminderTime" to 60, "status" to "Scheduled", "createdAt" to now, "userId" to userId),
            mapOf("companyName" to "Microsoft", "position" to "Backend Developer", "dateTime" to (now + 7 * day), "location" to "Microsoft Teams", "interviewerName" to "Sarah Levi", "notes" to "Review .NET fundamentals", "type" to "Video", "reminderTime" to 30, "status" to "Scheduled", "createdAt" to now, "userId" to userId),
            mapOf("companyName" to "Fiverr", "position" to "Data Analyst", "dateTime" to (now + 1 * day), "location" to "Phone Call", "interviewerName" to "Yossi Ben-David", "notes" to "HR screening round", "type" to "Phone", "reminderTime" to 60, "status" to "Scheduled", "createdAt" to now, "userId" to userId),
            mapOf("companyName" to "Wix", "position" to "Full Stack Developer", "dateTime" to (now - 5 * day), "location" to "Wix Tower Tel Aviv", "interviewerName" to "Noa Shapira", "notes" to "Final technical interview - went great!", "type" to "In Person", "reminderTime" to 60, "status" to "Completed", "createdAt" to (now - 10 * day), "userId" to userId)
        )

        for (interview in interviews) {
            db.collection(Constants.FIRESTORE.INTERVIEWS).add(interview)
        }

        // ── Sample Community Questions ──
        val questions = listOf(
            mapOf("companyName" to "Google", "position" to "Software Engineer", "question" to "Design a URL shortener system. How would you handle millions of requests per day?", "answer" to "Use hash-based approach with Base62 encoding, distributed cache with Redis, and horizontal scaling.", "categories" to listOf("Technical", "System Design"), "isAnonymous" to false, "upvotes" to 12, "userName" to "Tech Guru", "userId" to userId, "upvotedBy" to emptyList<String>(), "createdAt" to (now - 2 * day), "type" to "question"),
            mapOf("companyName" to "Meta", "position" to "Frontend Developer", "question" to "What is the difference between useEffect and useLayoutEffect in React?", "answer" to "useLayoutEffect fires synchronously after DOM mutations but before paint. useEffect fires asynchronously after paint.", "categories" to listOf("Technical"), "isAnonymous" to false, "upvotes" to 8, "userName" to "React Pro", "userId" to userId, "upvotedBy" to emptyList<String>(), "createdAt" to (now - 1 * day), "type" to "question"),
            mapOf("companyName" to "General", "position" to "All Positions", "question" to "Always send a thank-you email within 24 hours after your interview. Mention something specific you discussed!", "answer" to "", "categories" to listOf("Behavioral"), "isAnonymous" to false, "upvotes" to 20, "userName" to "Career Coach", "userId" to userId, "upvotedBy" to emptyList<String>(), "createdAt" to (now - 3 * day), "type" to "tip"),
            mapOf("companyName" to "Amazon", "position" to "DevOps Engineer", "question" to "Explain the difference between Docker and Kubernetes. When would you use each?", "answer" to "Docker is for containerization, Kubernetes is for orchestration. Use Docker for packaging apps, K8s for managing clusters at scale.", "categories" to listOf("Technical"), "isAnonymous" to true, "upvotes" to 15, "userName" to "Anonymous", "userId" to userId, "upvotedBy" to emptyList<String>(), "createdAt" to (now - 4 * day), "type" to "question"),
            mapOf("companyName" to "General", "position" to "All Positions", "question" to "Use the STAR method for behavioral questions: Situation, Task, Action, Result. It keeps your answers structured and concise.", "answer" to "", "categories" to listOf("Behavioral", "Tips"), "isAnonymous" to false, "upvotes" to 25, "userName" to "HR Expert", "userId" to userId, "upvotedBy" to emptyList<String>(), "createdAt" to (now - 6 * day), "type" to "tip")
        )

        for (question in questions) {
            db.collection(Constants.FIRESTORE.QUESTIONS).add(question)
        }

        // ── Sample CV Files ──
        val cvFiles = listOf(
            mapOf("fileName" to "Resume_Ofek_2024.pdf", "fileSize" to 245_000L, "downloadUrl" to "", "uploadedAt" to (now - 2 * day), "userId" to userId),
            mapOf("fileName" to "CV_English_FullStack.pdf", "fileSize" to 312_000L, "downloadUrl" to "", "uploadedAt" to (now - 10 * day), "userId" to userId),
            mapOf("fileName" to "CV_Hebrew_Android.pdf", "fileSize" to 198_000L, "downloadUrl" to "", "uploadedAt" to (now - 20 * day), "userId" to userId),
            mapOf("fileName" to "Cover_Letter_Google.pdf", "fileSize" to 87_000L, "downloadUrl" to "", "uploadedAt" to (now - 5 * day), "userId" to userId)
        )

        for (cv in cvFiles) {
            db.collection(Constants.FIRESTORE.CV_FILES).add(cv)
        }

        // ── User Profile Data ──
        val userData = mapOf(
            "phone" to "054-1234567",
            "district" to "Tel Aviv District",
            "careerGoal" to "Senior Android Developer",
            "linkedin" to "linkedin.com/in/demo-user",
            "github" to "github.com/demo-user",
            "userId" to userId
        )
        db.collection(Constants.FIRESTORE.USERS).document(userId)
            .set(userData, com.google.firebase.firestore.SetOptions.merge())

        showDemoSuccessDialog()
    }

    // Show a purple-accented success dialog after demo data is loaded
    private fun showDemoSuccessDialog() {
        val dialogView = layoutInflater.inflate(R.layout.dialog_success, null)
        dialogView.findViewById<TextView>(R.id.dialog_success_title).setText(R.string.demo_data_loaded)
        dialogView.findViewById<TextView>(R.id.dialog_success_message).setText(R.string.demo_data_loaded_msg)

        val card = dialogView.findViewById<MaterialCardView>(R.id.dialog_success_card)
        card.strokeColor = getColor(R.color.primary_purple)
        dialogView.findViewById<View>(R.id.dialog_success_circle)
            .setBackgroundResource(R.drawable.circle_glass_purple)
        val icon = dialogView.findViewById<ImageView>(R.id.dialog_success_icon)
        icon.setImageResource(R.drawable.ic_database)
        icon.imageTintList = android.content.res.ColorStateList.valueOf(getColor(R.color.primary_purple))
        dialogView.findViewById<TextView>(R.id.dialog_success_title)
            .setShadowLayer(10f, 0f, 0f, getColor(R.color.purple_glow))

        val dialog = AlertDialog.Builder(this, R.style.TransparentDialog)
            .setView(dialogView)
            .setCancelable(false)
            .create()

        dialog.show()
        dialogView.startAnimation(AnimationUtils.loadAnimation(this, R.anim.scale_in))

        Handler(Looper.getMainLooper()).postDelayed({
            if (!isFinishing) dialog.dismiss()
        }, 2000)
    }

    // Confirm logout, sign out from Firebase, and redirect to login
    private fun handleLogout() {
        AlertDialog.Builder(this)
            .setTitle(getString(R.string.settings_logout))
            .setMessage(getString(R.string.logout_confirmation))
            .setPositiveButton(getString(R.string.logout)) { _, _ ->
                auth.signOut()
                startActivity(Intent(this, LoginActivity::class.java).apply {
                    flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                })
                finish()
            }
            .setNegativeButton(getString(R.string.cancel), null)
            .show()
    }
}
