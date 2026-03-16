package com.ofek.hunter.utilities

import android.app.Activity
import android.view.View
import android.view.animation.AnimationUtils
import com.ofek.hunter.R

/**
 * Handle all screen transitions and view animations.
 */
object AnimationHelper {

    // Fade+slide-up each view with staggered delay
    @Suppress("UNUSED_PARAMETER")
    fun animateViews(activity: Activity, vararg views: View) {
        views.forEachIndexed { index, view ->
            view.alpha = 0f
            view.translationY = 40f
            view.animate()
                .alpha(1f)
                .translationY(0f)
                .setStartDelay(index * 120L)
                .setDuration(400)
                .start()
        }
    }

    // Slide-in-from-right enter transition
    @Suppress("DEPRECATION")
    fun enterTransition(activity: Activity) {
        activity.overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left)
    }

    // Slide-out-to-right exit transition
    @Suppress("DEPRECATION")
    fun exitTransition(activity: Activity) {
        activity.overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right)
    }

    // Play a bounce animation on a view (used for FABs etc.)
    fun bounce(view: View) {
        val anim = AnimationUtils.loadAnimation(view.context, R.anim.bounce)
        view.startAnimation(anim)
    }
}
