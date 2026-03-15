package com.ofek.hunter.utilities

import android.app.Dialog
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.view.LayoutInflater
import android.view.View
import android.view.Window
import android.widget.ImageView
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import com.google.firebase.auth.FirebaseAuth
import com.ofek.hunter.R
import com.ofek.hunter.activities.AboutActivity
import com.ofek.hunter.activities.CVManagerActivity
import com.ofek.hunter.activities.CommunityActivity
import com.ofek.hunter.activities.FavoritesActivity
import com.ofek.hunter.activities.InterviewsActivity
import com.ofek.hunter.activities.JobListActivity
import com.ofek.hunter.activities.LoginActivity
import com.ofek.hunter.activities.ProfileActivity
import com.ofek.hunter.activities.SettingsActivity
import com.ofek.hunter.activities.StatisticsActivity

/**
 * Wires up the drawer menu hamburger button, item clicks, and back press handling.
 */
object DrawerHelper {

    // Set up hamburger button, menu item navigation, and back press for the drawer
    fun setupDrawer(activity: AppCompatActivity, drawerLayout: DrawerLayout) {
        setupHamburgerButton(activity, drawerLayout)
        setupMenuItems(activity, drawerLayout)
        setupBackPress(activity, drawerLayout)
    }

    // Opens drawer when the hamburger icon is tapped
    private fun setupHamburgerButton(activity: AppCompatActivity, drawerLayout: DrawerLayout) {
        val button = activity.findViewById<ImageView>(R.id.header_menu_button) ?: return
        button.setOnClickListener {
            drawerLayout.openDrawer(GravityCompat.START)
        }
    }

    // Hooks each drawer menu item to its target activity
    private fun setupMenuItems(activity: AppCompatActivity, drawerLayout: DrawerLayout) {
        val container = activity.findViewById<View>(R.id.drawer_menu_container) ?: return

        val menuMap = mapOf(
            R.id.drawer_menu_job_tracker to JobListActivity::class.java,
            R.id.drawer_menu_interviews to InterviewsActivity::class.java,
            R.id.drawer_menu_cv_manager to CVManagerActivity::class.java,
            R.id.drawer_menu_community to CommunityActivity::class.java,
            R.id.drawer_menu_favorites to FavoritesActivity::class.java,
            R.id.drawer_menu_statistics to StatisticsActivity::class.java,
            R.id.drawer_menu_profile to ProfileActivity::class.java,
            R.id.drawer_menu_settings to SettingsActivity::class.java,
            R.id.drawer_menu_about to AboutActivity::class.java
        )

        for ((viewId, targetClass) in menuMap) {
            container.findViewById<View>(viewId)?.setOnClickListener {
                if (activity::class.java != targetClass) {
                    activity.startActivity(Intent(activity, targetClass))
                }
                drawerLayout.closeDrawer(GravityCompat.START)
            }
        }

        // Logout shows confirmation dialog then signs out
        container.findViewById<View>(R.id.drawer_menu_logout)?.setOnClickListener {
            showLogoutDialog(activity)
        }
    }

    // Closes drawer on back press, or finishes the activity
    private fun setupBackPress(activity: AppCompatActivity, drawerLayout: DrawerLayout) {
        activity.onBackPressedDispatcher.addCallback(activity, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
                    drawerLayout.closeDrawer(GravityCompat.START)
                } else {
                    activity.finish()
                }
            }
        })
    }

    // Shows custom logout confirmation dialog and redirects to login on confirm
    private fun showLogoutDialog(activity: AppCompatActivity) {
        val dialog = Dialog(activity)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        val view = LayoutInflater.from(activity).inflate(R.layout.dialog_logout_confirmation, null)
        dialog.setContentView(view)
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        view.findViewById<View>(R.id.btnCancel).setOnClickListener {
            dialog.dismiss()
        }

        view.findViewById<View>(R.id.btnLogout).setOnClickListener {
            dialog.dismiss()
            FirebaseAuth.getInstance().signOut()
            val intent = Intent(activity, LoginActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            activity.startActivity(intent)
            activity.finish()
        }

        dialog.show()
        dialog.window?.setLayout(
            (activity.resources.displayMetrics.widthPixels * 0.85).toInt(),
            android.view.ViewGroup.LayoutParams.WRAP_CONTENT
        )
    }
}
