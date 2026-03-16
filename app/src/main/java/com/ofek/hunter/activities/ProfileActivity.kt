package com.ofek.hunter.activities

import android.content.Intent
import android.net.Uri
import androidx.core.net.toUri
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import android.view.animation.AnimationUtils
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.card.MaterialCardView
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.UserProfileChangeRequest
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.ofek.hunter.R
import com.ofek.hunter.databinding.ActivityProfileBinding
import com.ofek.hunter.models.JobApplication
import com.ofek.hunter.utilities.AnimationHelper
import com.ofek.hunter.utilities.Constants
import com.ofek.hunter.utilities.DrawerHelper
import com.ofek.hunter.utilities.ImageLoader
import com.ofek.hunter.utilities.NavigationHelper

/**
 * User profile screen with stats, links, and edit dialog.
 */
class ProfileActivity : AppCompatActivity() {

    private lateinit var binding: ActivityProfileBinding
    private lateinit var auth: FirebaseAuth

    private var selectedImageUri: Uri? = null
    private var editDialogImageView: ImageView? = null

    private val pickImageLauncher = registerForActivityResult(ActivityResultContracts.GetContent()) { uri ->
        uri?.let {
            selectedImageUri = it
            editDialogImageView?.let { iv ->
                iv.setImageURI(it)
                iv.imageTintList = null
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)
        auth = FirebaseAuth.getInstance()
        AnimationHelper.enterTransition(this)
        initViews()
        loadProfile()
        loadStats()
    }

    // Set up header, bottom nav, drawer, and back press handling
    private fun initViews() {
        binding.root.findViewById<TextView>(R.id.header_title)?.apply {
            text = getString(R.string.profile_title)
            setShadowLayer(12f, 0f, 0f, getColor(R.color.purple_glow))
        }
        val headerIcon = binding.root.findViewById<ImageView>(R.id.header_screen_icon)
        headerIcon?.setImageResource(R.drawable.ic_edit)
        headerIcon?.setOnClickListener { showEditProfileDialog() }
        NavigationHelper.setupBottomNavigation(this, "profile")
        // Set up drawer menu navigation
        DrawerHelper.setupDrawer(this, binding.drawerLayout)
    }

    override fun finish() {
        super.finish()
        AnimationHelper.exitTransition(this)
    }

    // Populate the profile card — demo for personal email, real data otherwise
    private fun loadProfile() {
        val user = auth.currentUser ?: return
        if (user.email == "ofekfanian689@gmail.com") {
            loadDemoProfile()
            return
        }

        binding.profileTitle.text = user.displayName ?: getString(R.string.default_user_name)
        binding.profileSubtitle.text = user.email ?: getString(R.string.no_email)

        user.photoUrl?.let { url ->
            ImageLoader.loadCircleImage(binding.profileImage, url.toString())
            binding.profileImage.imageTintList = null
        }

        FirebaseFirestore.getInstance()
            .collection(Constants.FIRESTORE.USERS)
            .document(user.uid)
            .get()
            .addOnSuccessListener { doc ->
                if (doc.exists()) {
                    populateProfile(
                        doc.getString("phone") ?: "",
                        doc.getString("district") ?: "",
                        doc.getString("careerGoal") ?: "",
                        doc.getString("linkedin") ?: "",
                        doc.getString("github") ?: "",
                        doc.getString("profileImageUrl") ?: ""
                    )
                }
            }
    }

    // Fill profile fields with provided data
    private fun populateProfile(phone: String, district: String, goal: String, linkedin: String, github: String, imageUrl: String) {
        findViewById<TextView>(R.id.profile_phone_value)?.text =
            phone.ifEmpty { getString(R.string.no_phone_number) }
        findViewById<TextView>(R.id.profile_location_value)?.text =
            district.ifEmpty { getString(R.string.no_district) }
        findViewById<TextView>(R.id.profile_goal_value)?.text =
            goal.ifEmpty { getString(R.string.no_career_goal) }
        findViewById<TextView>(R.id.profile_linkedin_value)?.text =
            linkedin.ifEmpty { getString(R.string.no_link) }
        findViewById<TextView>(R.id.profile_github_value)?.text =
            github.ifEmpty { getString(R.string.no_link) }

        setupLinkCard(R.id.profile_card_linkedin, linkedin)
        setupLinkCard(R.id.profile_card_github, github)

        if (imageUrl.isNotEmpty()) {
            ImageLoader.loadCircleImage(binding.profileImage, imageUrl)
            binding.profileImage.imageTintList = null
        }
    }

    // Show demo profile when no Firestore data exists
    private fun loadDemoProfile() {
        binding.profileTitle.text = getString(R.string.demo_name)
        binding.profileSubtitle.text = getString(R.string.demo_email)
        binding.profileImage.setImageResource(R.drawable.ic_imag_profile)
        binding.profileImage.imageTintList = null
        binding.profileImage.scaleType = ImageView.ScaleType.MATRIX
        binding.profileImage.post {
            val drawable = binding.profileImage.drawable ?: return@post
            val viewW = binding.profileImage.width.toFloat()
            val viewH = binding.profileImage.height.toFloat()
            val drawW = drawable.intrinsicWidth.toFloat()
            val drawH = drawable.intrinsicHeight.toFloat()
            val scale = maxOf(viewW / drawW, viewH / drawH)
            val matrix = android.graphics.Matrix()
            matrix.setScale(scale, scale)
            matrix.postTranslate(
                (viewW - drawW * scale) / 2f,
                (viewH - drawH * scale) / 2f + viewH * 0.25f
            )
            binding.profileImage.imageMatrix = matrix
        }

        findViewById<TextView>(R.id.profile_phone_value)?.text = getString(R.string.demo_phone)
        findViewById<TextView>(R.id.profile_location_value)?.text = getString(R.string.demo_location)
        findViewById<TextView>(R.id.profile_goal_value)?.text = getString(R.string.demo_goal)
        findViewById<TextView>(R.id.profile_linkedin_value)?.text = getString(R.string.demo_linkedin_display)
        findViewById<TextView>(R.id.profile_github_value)?.text = getString(R.string.demo_github_display)

        setupLinkCard(R.id.profile_card_linkedin, "https://linkedin.com/in/ofekfanian")
        setupLinkCard(R.id.profile_card_github, "https://github.com/ofekfanian/Hunter")
    }

    // Make a link card open the URL in the browser when tapped
    private fun setupLinkCard(cardId: Int, url: String) {
        findViewById<View>(cardId)?.setOnClickListener {
            if (url.isNotEmpty()) {
                val fullUrl = if (url.startsWith("http")) url else "https://$url"
                val intent = Intent(Intent.ACTION_VIEW, fullUrl.toUri())
                startActivity(intent)
            }
        }
    }

    // Load statistics — demo for personal email, real data otherwise
    private fun loadStats() {
        val user = auth.currentUser ?: return
        if (user.email == "ofekfanian689@gmail.com") {
            loadDemoStats()
            return
        }

        FirebaseFirestore.getInstance()
            .collection(Constants.FIRESTORE.JOBS)
            .whereEqualTo("userId", user.uid)
            .get()
            .addOnSuccessListener { result ->
                val jobs = result.mapNotNull { it.toObject(JobApplication::class.java) }
                findViewById<TextView>(R.id.stat_total_value).text = jobs.size.toString()
                findViewById<TextView>(R.id.stat_interviews_value).text = jobs.count { it.jobType == "Interview" }.toString()
                findViewById<TextView>(R.id.stat_offers_value).text = jobs.count { it.jobType == "Offer" }.toString()
            }
            .addOnFailureListener { loadDemoStats() }
    }

    // Show demo statistics
    private fun loadDemoStats() {
        findViewById<TextView>(R.id.stat_total_value).text = "8"
        findViewById<TextView>(R.id.stat_interviews_value).text = "2"
        findViewById<TextView>(R.id.stat_offers_value).text = "1"
    }

    // Show the edit dialog pre-filled with current profile data
    private fun showEditProfileDialog() {
        val dialogView = layoutInflater.inflate(R.layout.dialog_edit_profile, null)

        val nameInput = dialogView.findViewById<TextInputEditText>(R.id.edit_name_text)
        val phoneInput = dialogView.findViewById<TextInputEditText>(R.id.edit_phone_text)
        val districtInput = dialogView.findViewById<AutoCompleteTextView>(R.id.edit_district_text)
        val goalInput = dialogView.findViewById<TextInputEditText>(R.id.edit_goal_text)
        val linkedinInput = dialogView.findViewById<TextInputEditText>(R.id.edit_linkedin_text)
        val githubInput = dialogView.findViewById<TextInputEditText>(R.id.edit_github_text)
        val imageContainer = dialogView.findViewById<View>(R.id.edit_profile_image_container)
        editDialogImageView = dialogView.findViewById(R.id.edit_profile_image)

        // Setup district dropdown
        val districts = resources.getStringArray(R.array.districts)
        val districtAdapter = ArrayAdapter(this, android.R.layout.simple_dropdown_item_1line, districts)
        districtInput.setAdapter(districtAdapter)

        // Pre-fill with current data
        val user = auth.currentUser
        nameInput.setText(user?.displayName ?: "")

        // Load existing Firestore data
        user?.uid?.let { uid ->
            FirebaseFirestore.getInstance()
                .collection(Constants.FIRESTORE.USERS)
                .document(uid)
                .get()
                .addOnSuccessListener { doc ->
                    if (doc.exists()) {
                        phoneInput.setText(doc.getString("phone") ?: "")
                        districtInput.setText(doc.getString("district") ?: "", false)
                        goalInput.setText(doc.getString("careerGoal") ?: "")
                        linkedinInput.setText(doc.getString("linkedin") ?: "")
                        githubInput.setText(doc.getString("github") ?: "")
                        val imageUrl = doc.getString("profileImageUrl") ?: ""
                        if (imageUrl.isNotEmpty()) {
                            editDialogImageView?.let {
                                ImageLoader.loadCircleImage(it, imageUrl)
                                it.imageTintList = null
                            }
                        }
                    }
                }
        }

        // Load current profile image if exists
        user?.photoUrl?.let { url ->
            editDialogImageView?.let {
                ImageLoader.loadCircleImage(it, url.toString())
                it.imageTintList = null
            }
        }

        selectedImageUri = null

        val dialog = AlertDialog.Builder(this, R.style.TransparentDialog)
            .setView(dialogView)
            .setCancelable(true)
            .create()

        imageContainer.setOnClickListener {
            pickImageLauncher.launch("image/*")
        }

        dialogView.findViewById<View>(R.id.edit_profile_cancel).setOnClickListener {
            dialog.dismiss()
        }

        dialogView.findViewById<View>(R.id.edit_profile_save).setOnClickListener {
            val name = nameInput.text.toString().trim()
            val phone = phoneInput.text.toString().trim()
            val district = districtInput.text.toString().trim()
            val goal = goalInput.text.toString().trim()
            val linkedin = linkedinInput.text.toString().trim()
            val github = githubInput.text.toString().trim()

            saveProfile(name, phone, district, goal, linkedin, github, dialog)
        }

        dialog.show()
        dialogView.startAnimation(AnimationUtils.loadAnimation(this, R.anim.scale_in))
    }

    // Update the Auth display name, then save remaining fields
    private fun saveProfile(name: String, phone: String, district: String, goal: String, linkedin: String, github: String, dialog: AlertDialog) {
        val user = auth.currentUser ?: return
        val userId = user.uid

        val profileUpdates = UserProfileChangeRequest.Builder()
            .setDisplayName(name.ifEmpty { null })
            .build()

        user.updateProfile(profileUpdates).addOnSuccessListener {
            if (selectedImageUri != null) {
                uploadImageAndSave(userId, phone, district, goal, linkedin, github, dialog)
            } else {
                saveToFirestore(userId, phone, district, goal, linkedin, github, null, dialog)
            }
        }.addOnFailureListener {
            saveToFirestore(userId, phone, district, goal, linkedin, github, null, dialog)
        }
    }

    // Upload the selected image to Storage and then save all fields
    private fun uploadImageAndSave(userId: String, phone: String, district: String, goal: String, linkedin: String, github: String, dialog: AlertDialog) {
        val imageRef = FirebaseStorage.getInstance().reference
            .child("profile_images/$userId.jpg")

        selectedImageUri?.let { uri ->
            imageRef.putFile(uri)
                .addOnSuccessListener {
                    imageRef.downloadUrl.addOnSuccessListener { downloadUrl ->
                        val profileUpdates = UserProfileChangeRequest.Builder()
                            .setPhotoUri(downloadUrl)
                            .build()
                        auth.currentUser?.updateProfile(profileUpdates)

                        saveToFirestore(userId, phone, district, goal, linkedin, github, downloadUrl.toString(), dialog)
                    }
                }
                .addOnFailureListener {
                    saveToFirestore(userId, phone, district, goal, linkedin, github, null, dialog)
                }
        }
    }

    // Merge profile fields into the user's Firestore document
    private fun saveToFirestore(userId: String, phone: String, district: String, goal: String, linkedin: String, github: String, imageUrl: String?, dialog: AlertDialog) {
        val data = mutableMapOf<String, Any>(
            "phone" to phone,
            "district" to district,
            "careerGoal" to goal,
            "linkedin" to linkedin,
            "github" to github,
            "userId" to userId
        )
        imageUrl?.let { data["profileImageUrl"] = it }

        FirebaseFirestore.getInstance()
            .collection(Constants.FIRESTORE.USERS)
            .document(userId)
            .set(data, com.google.firebase.firestore.SetOptions.merge())
            .addOnSuccessListener {
                dialog.dismiss()
                showSuccessDialog()
                loadProfile()
            }
            .addOnFailureListener {
                dialog.dismiss()
            }
    }

    // Show a purple-accented success dialog that auto-dismisses
    private fun showSuccessDialog() {
        val dialogView = layoutInflater.inflate(R.layout.dialog_success, null)
        dialogView.findViewById<TextView>(R.id.dialog_success_title).setText(R.string.profile_updated)
        dialogView.findViewById<TextView>(R.id.dialog_success_message).setText(R.string.profile_updated_msg)

        val card = dialogView.findViewById<MaterialCardView>(R.id.dialog_success_card)
        card.strokeColor = getColor(R.color.primary_purple)
        dialogView.findViewById<View>(R.id.dialog_success_circle)
            .setBackgroundResource(R.drawable.circle_glass_purple)
        val icon = dialogView.findViewById<ImageView>(R.id.dialog_success_icon)
        icon.setImageResource(R.drawable.ic_person)
        icon.imageTintList = android.content.res.ColorStateList.valueOf(getColor(R.color.primary_purple))
        dialogView.findViewById<TextView>(R.id.dialog_success_title)
            .setShadowLayer(10f, 0f, 0f, getColor(R.color.purple_glow))

        val successDialog = AlertDialog.Builder(this, R.style.TransparentDialog)
            .setView(dialogView)
            .setCancelable(false)
            .create()

        successDialog.show()
        dialogView.startAnimation(AnimationUtils.loadAnimation(this, R.anim.scale_in))

        Handler(Looper.getMainLooper()).postDelayed({
            if (!isFinishing) {
                successDialog.dismiss()
            }
        }, 2000)
    }
}
