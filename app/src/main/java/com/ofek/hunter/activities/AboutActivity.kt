package com.ofek.hunter.activities

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.net.toUri
import com.ofek.hunter.R
import com.ofek.hunter.databinding.ActivityAboutBinding
import com.ofek.hunter.utilities.AnimationHelper
import com.ofek.hunter.utilities.DrawerHelper
import com.ofek.hunter.utilities.NavigationHelper

/**
 * About screen with app info and contact email.
 */
class AboutActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAboutBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAboutBinding.inflate(layoutInflater)
        setContentView(binding.root)
        AnimationHelper.enterTransition(this)
        initViews()
    }

    // Sets the header title/icon and wires up the contact email card
    private fun initViews() {
        binding.root.findViewById<TextView>(R.id.header_title)?.apply {
            text = getString(R.string.about_title)
            setShadowLayer(12f, 0f, 0f, getColor(R.color.purple_glow))
        }
        binding.root.findViewById<ImageView>(R.id.header_screen_icon)?.setImageResource(R.drawable.ic_info)
        // Set up drawer menu navigation
        DrawerHelper.setupDrawer(this, binding.drawerLayout)
        NavigationHelper.setupBottomNavigation(this, "")

        binding.cardContactEmail.setOnClickListener { openEmail() }
    }

    override fun finish() {
        super.finish()
        AnimationHelper.exitTransition(this)
    }

    // Opens the email client with a pre-filled subject line
    private fun openEmail() {
        val emailUri = "mailto:${getString(R.string.about_email_address)}".toUri()
        val intent = Intent(Intent.ACTION_SENDTO).apply {
            data = emailUri
            putExtra(Intent.EXTRA_SUBJECT, getString(R.string.about_email_subject))
        }
        try {
            startActivity(intent)
        } catch (_: Exception) {
            Toast.makeText(this, "No email app found", Toast.LENGTH_SHORT).show()
        }
    }
}
