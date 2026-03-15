package com.ofek.hunter.activities

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import android.view.animation.AnimationUtils
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.card.MaterialCardView
import androidx.core.net.toUri
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import com.ofek.hunter.R
import com.ofek.hunter.adapters.CVAdapter
import com.ofek.hunter.databinding.ActivityCvManagerBinding
import com.ofek.hunter.interfaces.CVCallback
import com.ofek.hunter.models.CVFile
import com.ofek.hunter.utilities.AnimationHelper
import com.ofek.hunter.utilities.Constants
import com.ofek.hunter.utilities.DrawerHelper
import com.ofek.hunter.utilities.NavigationHelper

/**
 * Manage CV files with upload, view, share, and delete.
 */
class CVManagerActivity : AppCompatActivity() {

    private lateinit var binding: ActivityCvManagerBinding
    private lateinit var cvAdapter: CVAdapter

    private val pickPdfLauncher = registerForActivityResult(ActivityResultContracts.GetContent()) { uri ->
        uri?.let { uploadCV(it) }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCvManagerBinding.inflate(layoutInflater)
        setContentView(binding.root)
        AnimationHelper.enterTransition(this)
        initViews()
    }

    // Set up header, bottom nav, recycler, FAB, and load data
    private fun initViews() {
        binding.root.findViewById<TextView>(R.id.header_title)?.apply {
            text = getString(R.string.cv_manager_title)
            setShadowLayer(12f, 0f, 0f, getColor(R.color.teal_glow))
        }
        binding.root.findViewById<ImageView>(R.id.header_screen_icon)?.setImageResource(R.drawable.ic_cv_document)
        NavigationHelper.setupBottomNavigation(this, "cv")
        // Set up drawer menu navigation
        DrawerHelper.setupDrawer(this, binding.drawerLayout)
        setupRecyclerView()
        setupFAB()
        loadCVFiles()
    }

    override fun finish() {
        super.finish()
        AnimationHelper.exitTransition(this)
    }

    // Wire up the CV list adapter with click callbacks
    private fun setupRecyclerView() {
        cvAdapter = CVAdapter()
        cvAdapter.cvCallback = object : CVCallback {
            override fun itemClicked(cv: CVFile, position: Int) { viewCV(cv) }
            override fun deleteClicked(cv: CVFile, position: Int) { confirmDelete(cv) }
            override fun shareClicked(cv: CVFile, position: Int) { quickShareCV(cv) }
        }
        binding.cvManagerRecyclerView.apply {
            layoutManager = LinearLayoutManager(this@CVManagerActivity)
            adapter = cvAdapter
        }
    }

    // Launch the PDF picker when the upload FAB is tapped
    private fun setupFAB() {
        binding.cvManagerBTNUploadFab.setOnClickListener {
            pickPdfLauncher.launch("application/pdf")
        }
        binding.cvManagerBTNUploadFab.post { AnimationHelper.bounce(binding.cvManagerBTNUploadFab) }
    }

    // Fetch all CV files for the current user from Firestore
    private fun loadCVFiles() {
        val user = FirebaseAuth.getInstance().currentUser
        if (user?.email == "ofekfanian689@gmail.com") {
            loadDemoCVFiles()
            return
        }

        binding.cvManagerProgressBar.visibility = View.VISIBLE

        val userId = user?.uid ?: ""
        FirebaseFirestore.getInstance()
            .collection(Constants.FIRESTORE.CV_FILES)
            .whereEqualTo("userId", userId)
            .get()
            .addOnSuccessListener { result ->
                val files = result.mapNotNull { doc ->
                    doc.toObject(CVFile::class.java)?.also { it.id = doc.id }
                }
                cvAdapter.cvFiles = files
                cvAdapter.notifyDataSetChanged()
                updateUI(files.isEmpty())
            }
            .addOnFailureListener {
                updateUI(true)
                Toast.makeText(this, getString(R.string.cv_load_failed), Toast.LENGTH_SHORT).show()
            }
    }

    private fun loadDemoCVFiles() {
        val demoFiles = listOf(
            CVFile(id = "demo1", fileName = "Ofek_Fanian_Resume_2025.pdf", fileSize = 245_000L, uploadedAt = System.currentTimeMillis() - 86400000 * 2),
            CVFile(id = "demo2", fileName = "Ofek_Fanian_Android_Dev.pdf", fileSize = 312_000L, uploadedAt = System.currentTimeMillis() - 86400000 * 10),
            CVFile(id = "demo3", fileName = "Ofek_Fanian_Cover_Letter.pdf", fileSize = 128_000L, uploadedAt = System.currentTimeMillis() - 86400000 * 30)
        )
        cvAdapter.cvFiles = demoFiles
        cvAdapter.notifyDataSetChanged()
        updateUI(false)
    }

    // Upload a PDF to Firebase Storage and save the record to Firestore
    private fun uploadCV(uri: Uri) {
        val fileName = getFileName(uri) ?: "cv_${System.currentTimeMillis()}.pdf"
        val userId = FirebaseAuth.getInstance().currentUser?.uid ?: ""
        val storageRef = Firebase.storage.reference.child("cvs/$userId/$fileName")
        storageRef.putFile(uri)
            .addOnSuccessListener {
                storageRef.downloadUrl.addOnSuccessListener { downloadUri ->
                    val cv = CVFile(fileName = fileName, downloadUrl = downloadUri.toString(), userId = userId)
                    FirebaseFirestore.getInstance()
                        .collection(Constants.FIRESTORE.CV_FILES)
                        .add(cv)
                        .addOnSuccessListener {
                            showSuccessDialog(
                                R.string.dialog_success_cv_uploaded_title,
                                R.string.dialog_success_cv_uploaded_msg
                            )
                            loadCVFiles()
                        }
                }
            }
            .addOnFailureListener {
                Toast.makeText(this, getString(R.string.cv_upload_failed), Toast.LENGTH_SHORT).show()
            }
    }

    // Show a share options dialog (WhatsApp, email, LinkedIn, generic)
    private fun quickShareCV(cv: CVFile) {
        val options = resources.getStringArray(R.array.cv_share_options)

        AlertDialog.Builder(this)
            .setTitle(getString(R.string.cv_share_title))
            .setItems(options) { _, which ->
                when (which) {
                    0 -> shareVia(cv, "com.whatsapp")
                    1 -> shareEmail(cv)
                    2 -> shareVia(cv, "com.linkedin.android")
                    3 -> shareGeneric(cv)
                }
            }
            .show()
    }

    // Share the CV through a specific app package
    private fun shareVia(cv: CVFile, packageName: String) {
        val intent = Intent(Intent.ACTION_SEND).apply {
            type = "application/pdf"
            setPackage(packageName)
            putExtra(Intent.EXTRA_TEXT, getString(R.string.cv_share_text, cv.fileName))
            putExtra(Intent.EXTRA_STREAM, cv.downloadUrl.toUri())
            flags = Intent.FLAG_GRANT_READ_URI_PERMISSION
        }

        try {
            startActivity(intent)
        } catch (_: Exception) {
            Toast.makeText(this, getString(R.string.cv_app_not_installed), Toast.LENGTH_SHORT).show()
        }
    }

    // Share the CV via an email intent with subject and body
    private fun shareEmail(cv: CVFile) {
        Intent(Intent.ACTION_SEND).apply {
            type = "application/pdf"
            putExtra(Intent.EXTRA_SUBJECT, getString(R.string.cv_email_subject, cv.fileName))
            putExtra(Intent.EXTRA_TEXT, getString(R.string.cv_email_body))
            putExtra(Intent.EXTRA_STREAM, cv.downloadUrl.toUri())
            flags = Intent.FLAG_GRANT_READ_URI_PERMISSION
        }.also { startActivity(Intent.createChooser(it, getString(R.string.cv_send_via_email))) }
    }

    // Open the system share sheet for the CV file
    private fun shareGeneric(cv: CVFile) {
        Intent(Intent.ACTION_SEND).apply {
            type = "application/pdf"
            putExtra(Intent.EXTRA_STREAM, cv.downloadUrl.toUri())
            flags = Intent.FLAG_GRANT_READ_URI_PERMISSION
        }.also { startActivity(Intent.createChooser(it, getString(R.string.cv_share_chooser))) }
    }

    // Open the CV in an external PDF viewer
    private fun viewCV(cv: CVFile) {
        Intent(Intent.ACTION_VIEW).apply {
            setDataAndType(cv.downloadUrl.toUri(), "application/pdf")
            flags = Intent.FLAG_ACTIVITY_NO_HISTORY or Intent.FLAG_GRANT_READ_URI_PERMISSION
        }.also {
            try {
                startActivity(it)
            } catch (_: Exception) {
                Toast.makeText(this, getString(R.string.cv_no_pdf_viewer), Toast.LENGTH_SHORT).show()
            }
        }
    }

    // Show a confirmation dialog before deleting a CV
    private fun confirmDelete(cv: CVFile) {
        AlertDialog.Builder(this)
            .setTitle(getString(R.string.cv_delete_title))
            .setMessage(getString(R.string.cv_delete_message))
            .setPositiveButton(getString(R.string.delete)) { _, _ -> deleteCV(cv) }
            .setNegativeButton(getString(R.string.cancel), null)
            .show()
    }

    // Delete the CV record from Firestore and refresh the list
    private fun deleteCV(cv: CVFile) {
        FirebaseFirestore.getInstance()
            .collection(Constants.FIRESTORE.CV_FILES)
            .document(cv.id)
            .delete()
            .addOnSuccessListener {
                Toast.makeText(this, getString(R.string.deleted), Toast.LENGTH_SHORT).show()
                loadCVFiles()
            }
            .addOnFailureListener {
                Toast.makeText(this, getString(R.string.error_generic), Toast.LENGTH_SHORT).show()
            }
    }

    // Show a teal-accented success dialog that auto-dismisses
    private fun showSuccessDialog(titleRes: Int, messageRes: Int) {
        val dialogView = layoutInflater.inflate(R.layout.dialog_success, null)
        dialogView.findViewById<TextView>(R.id.dialog_success_title).setText(titleRes)
        dialogView.findViewById<TextView>(R.id.dialog_success_message).setText(messageRes)

        // Teal accent for CV
        val card = dialogView.findViewById<MaterialCardView>(R.id.dialog_success_card)
        card.strokeColor = getColor(R.color.teal_stroke)
        dialogView.findViewById<View>(R.id.dialog_success_circle)
            .setBackgroundResource(R.drawable.circle_glass_teal)
        val icon = dialogView.findViewById<ImageView>(R.id.dialog_success_icon)
        icon.setImageResource(R.drawable.ic_upload)
        icon.imageTintList = android.content.res.ColorStateList.valueOf(getColor(R.color.accent_teal_light))
        dialogView.findViewById<TextView>(R.id.dialog_success_title)
            .setShadowLayer(10f, 0f, 0f, getColor(R.color.teal_glow))

        val dialog = AlertDialog.Builder(this, R.style.TransparentDialog)
            .setView(dialogView)
            .setCancelable(true)
            .create()

        dialog.show()
        dialogView.startAnimation(AnimationUtils.loadAnimation(this, R.anim.scale_in))

        Handler(Looper.getMainLooper()).postDelayed({
            if (!isFinishing && dialog.isShowing) {
                dialog.dismiss()
            }
        }, 2000)
    }

    // Resolve the display name of a file from its content URI
    private fun getFileName(uri: Uri): String? {
        return contentResolver.query(uri, null, null, null, null)?.use { cursor ->
            val nameIndex = cursor.getColumnIndexOrThrow(android.provider.OpenableColumns.DISPLAY_NAME)
            cursor.moveToFirst()
            cursor.getString(nameIndex)
        }
    }

    // Toggle between the empty state card and the CV list
    private fun updateUI(isEmpty: Boolean) {
        binding.cvManagerProgressBar.visibility = View.GONE
        binding.cardEmptyState.visibility = if (isEmpty) View.VISIBLE else View.GONE
        binding.cvManagerRecyclerView.visibility = if (isEmpty) View.GONE else View.VISIBLE
    }
}
