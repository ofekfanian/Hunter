package com.ofek.hunter.activities

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.viewpager2.widget.ViewPager2
import com.ofek.hunter.R
import com.ofek.hunter.adapters.OnboardingAdapter
import com.ofek.hunter.databinding.ActivityOnboardingBinding

/**
 * First-launch onboarding walkthrough with swipeable pages.
 */
class OnboardingActivity : AppCompatActivity() {

    private lateinit var binding: ActivityOnboardingBinding

    private val dotViews by lazy {
        listOf(binding.dot0, binding.dot1, binding.dot2)
    }

    private val buttonGradients = intArrayOf(
        R.drawable.gradient_main_jobs,
        R.drawable.gradient_main_cv,
        R.drawable.gradient_main_community
    )

    private val buttonStrokes = intArrayOf(
        R.color.main_card_stroke_indigo,
        R.color.main_card_stroke_teal,
        R.color.main_card_stroke_rose
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityOnboardingBinding.inflate(layoutInflater)
        setContentView(binding.root)
        initViews()
    }

    // Sets up the ViewPager, dots, skip/next buttons, and entrance animations
    private fun initViews() {
        // Handle system bars (navigation bar) padding
        ViewCompat.setOnApplyWindowInsetsListener(binding.onboardingContainer) { view, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            view.setPadding(0, systemBars.top, 0, systemBars.bottom)
            insets
        }

        binding.onboardingViewPager.adapter = OnboardingAdapter()

        binding.onboardingViewPager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                updateDots(position)
                updateButton(position)
            }
        })

        binding.onboardingBtnSkip.setOnClickListener {
            finishOnboarding()
        }

        binding.onboardingBtnNext.setOnClickListener {
            val current = binding.onboardingViewPager.currentItem
            if (current < 2) {
                binding.onboardingViewPager.currentItem = current + 1
            } else {
                finishOnboarding()
            }
        }

        // Entrance animations
        animateViewIn(binding.onboardingBtnSkip, 300)
        animateViewIn(binding.onboardingDotsContainer, 500)
        animateViewIn(binding.onboardingBtnNext, 700)
    }

    // Highlights the active dot and dims the rest
    private fun updateDots(position: Int) {
        dotViews.forEachIndexed { index, dot ->
            dot.setImageResource(
                if (index == position) R.drawable.dot_active else R.drawable.dot_inactive
            )
        }
    }

    // Swaps the button gradient and label based on the current page
    private fun updateButton(position: Int) {
        binding.onboardingBtnNextText.setBackgroundResource(buttonGradients[position])
        binding.onboardingBtnNext.setStrokeColor(
            android.content.res.ColorStateList.valueOf(getColor(buttonStrokes[position]))
        )
        binding.onboardingBtnNextText.text = if (position == 2) {
            getString(R.string.onboarding_get_started)
        } else {
            getString(R.string.onboarding_next)
        }
    }

    // Marks onboarding as done in prefs and navigates to login
    private fun finishOnboarding() {
        getSharedPreferences("hunter_prefs", MODE_PRIVATE)
            .edit()
            .putBoolean("onboarding_shown", true)
            .apply()
        startActivity(Intent(this, LoginActivity::class.java))
        finish()
    }

    // Fades and slides a view in with a delayed animation
    private fun animateViewIn(view: View, delay: Long) {
        view.alpha = 0f
        val anim = AnimationUtils.loadAnimation(this, R.anim.fade_slide_up).apply {
            startOffset = delay
            setAnimationListener(object : Animation.AnimationListener {
                override fun onAnimationStart(animation: Animation?) {}
                override fun onAnimationRepeat(animation: Animation?) {}
                override fun onAnimationEnd(animation: Animation?) {
                    view.alpha = 1f
                }
            })
        }
        view.startAnimation(anim)
    }
}
