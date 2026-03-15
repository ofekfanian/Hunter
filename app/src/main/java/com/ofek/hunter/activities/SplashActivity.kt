package com.ofek.hunter.activities

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import android.view.animation.AccelerateDecelerateInterpolator
import android.view.animation.DecelerateInterpolator
import android.view.animation.OvershootInterpolator
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.ofek.hunter.databinding.ActivitySplashBinding

/**
 * Animated splash screen shown on app launch before navigating forward.
 */
@SuppressLint("CustomSplashScreen")
class SplashActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySplashBinding
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySplashBinding.inflate(layoutInflater)
        setContentView(binding.root)
        auth = FirebaseAuth.getInstance()
        startAnimations()
        navigateToNextScreen()
    }

    // Kicks off all splash animation phases in sequence
    private fun startAnimations() {
        // ===== Phase 1: Rings expand outward (0ms) =====
        animateRing(binding.splashRingOuter, 0, 1000)
        animateRing(binding.splashRingMiddle, 150, 900)

        // ===== Phase 2: Glass circle + Logo appear with bounce (300ms) =====
        val glassCircle = binding.splashGlassCircle
        glassCircle.scaleX = 0f
        glassCircle.scaleY = 0f
        glassCircle.alpha = 0f

        AnimatorSet().apply {
            playTogether(
                ObjectAnimator.ofFloat(glassCircle, "scaleX", 0f, 1.15f, 1f).apply {
                    duration = 900; interpolator = OvershootInterpolator(2f)
                },
                ObjectAnimator.ofFloat(glassCircle, "scaleY", 0f, 1.15f, 1f).apply {
                    duration = 900; interpolator = OvershootInterpolator(2f)
                },
                ObjectAnimator.ofFloat(glassCircle, "alpha", 0f, 1f).apply {
                    duration = 500
                }
            )
            startDelay = 300
            start()
        }

        // ===== Phase 3: Logo spins inside the glass circle (400ms) =====
        val logo = binding.splashIMGLogo
        ObjectAnimator.ofFloat(logo, "rotation", -30f, 360f).apply {
            duration = 1200; startDelay = 400; interpolator = DecelerateInterpolator(1.5f)
            start()
        }

        // ===== Phase 4: Floating particles fade in and drift (600ms) =====
        animateParticle(binding.splashParticle1, -20f, -30f, 600)
        animateParticle(binding.splashParticle2, 25f, -20f, 750)
        animateParticle(binding.splashParticle3, 15f, 25f, 900)
        animateParticle(binding.splashParticle4, -25f, 15f, 1050)

        // ===== Phase 5: Title slides up with glow (1000ms) =====
        animateTextIn(binding.splashTXTAppName, 1000, 50f)

        // ===== Phase 6: Tagline fades in (1300ms) =====
        animateTextIn(binding.splashTXTTagline, 1300, 30f)

        // ===== Phase 7: Decorative line expands (1500ms) =====
        val line = binding.splashLine
        line.scaleX = 0f
        line.alpha = 0f
        AnimatorSet().apply {
            playTogether(
                ObjectAnimator.ofFloat(line, "scaleX", 0f, 1f).apply {
                    duration = 600; interpolator = DecelerateInterpolator()
                },
                ObjectAnimator.ofFloat(line, "alpha", 0f, 1f).apply {
                    duration = 400
                }
            )
            startDelay = 1500
            start()
        }

        // ===== Phase 8: Loading dots with staggered pulse (1700ms) =====
        val container = binding.splashLoadingContainer
        container.alpha = 0f
        ObjectAnimator.ofFloat(container, "alpha", 0f, 1f).apply {
            duration = 400; startDelay = 1700; start()
        }
        animateLoadingDot(binding.splashDot1, 1800)
        animateLoadingDot(binding.splashDot2, 2000)
        animateLoadingDot(binding.splashDot3, 2200)

        // ===== Continuous: Rings slowly rotate =====
        ObjectAnimator.ofFloat(binding.splashRingOuter, "rotation", 0f, 360f).apply {
            duration = 20000; repeatCount = ObjectAnimator.INFINITE
            interpolator = AccelerateDecelerateInterpolator(); start()
        }
        ObjectAnimator.ofFloat(binding.splashRingMiddle, "rotation", 0f, -360f).apply {
            duration = 25000; repeatCount = ObjectAnimator.INFINITE
            interpolator = AccelerateDecelerateInterpolator(); start()
        }
    }

    // Scales and fades in a ring from small to full size
    private fun animateRing(view: View, delay: Long, dur: Long) {
        view.scaleX = 0.3f
        view.scaleY = 0.3f
        view.alpha = 0f
        AnimatorSet().apply {
            playTogether(
                ObjectAnimator.ofFloat(view, "scaleX", 0.3f, 1f).apply {
                    duration = dur; interpolator = DecelerateInterpolator(2f)
                },
                ObjectAnimator.ofFloat(view, "scaleY", 0.3f, 1f).apply {
                    duration = dur; interpolator = DecelerateInterpolator(2f)
                },
                ObjectAnimator.ofFloat(view, "alpha", 0f, 0.7f).apply {
                    duration = dur / 2
                }
            )
            startDelay = delay
            start()
        }
    }

    // Fades in a particle and drifts it in the given direction
    private fun animateParticle(view: View, driftX: Float, driftY: Float, delay: Long) {
        view.alpha = 0f
        AnimatorSet().apply {
            playTogether(
                ObjectAnimator.ofFloat(view, "alpha", 0f, 0.8f, 0f).apply { duration = 2000 },
                ObjectAnimator.ofFloat(view, "translationX", 0f, driftX).apply { duration = 2000 },
                ObjectAnimator.ofFloat(view, "translationY", 0f, driftY).apply { duration = 2000 }
            )
            startDelay = delay
            start()
        }
    }

    // Slides a text view up from below while fading it in
    private fun animateTextIn(view: View, delay: Long, slideFrom: Float) {
        view.alpha = 0f
        view.translationY = slideFrom
        AnimatorSet().apply {
            playTogether(
                ObjectAnimator.ofFloat(view, "alpha", 0f, 1f).apply { duration = 600 },
                ObjectAnimator.ofFloat(view, "translationY", slideFrom, 0f).apply {
                    duration = 700; interpolator = DecelerateInterpolator(2f)
                }
            )
            startDelay = delay
            start()
        }
    }

    // Pulses a loading dot with a repeating scale and alpha loop
    private fun animateLoadingDot(view: View, delay: Long) {
        val alphaAnim = ObjectAnimator.ofFloat(view, "alpha", 0.3f, 1f, 0.3f).apply {
            duration = 800
            repeatCount = ObjectAnimator.INFINITE
        }
        val scaleXAnim = ObjectAnimator.ofFloat(view, "scaleX", 1f, 1.5f, 1f).apply {
            duration = 800
            repeatCount = ObjectAnimator.INFINITE
        }
        val scaleYAnim = ObjectAnimator.ofFloat(view, "scaleY", 1f, 1.5f, 1f).apply {
            duration = 800
            repeatCount = ObjectAnimator.INFINITE
        }
        AnimatorSet().apply {
            playTogether(alphaAnim, scaleXAnim, scaleYAnim)
            startDelay = delay
            start()
        }
    }

    // After a delay, routes to onboarding, login, or main based on state
    private fun navigateToNextScreen() {
        Handler(Looper.getMainLooper()).postDelayed({
            if (isFinishing) return@postDelayed
            val onboardingShown = getSharedPreferences("hunter_prefs", MODE_PRIVATE)
                .getBoolean("onboarding_shown", false)
            val intent = if (!onboardingShown) {
                Intent(this, OnboardingActivity::class.java)
            } else if (auth.currentUser != null) {
                Intent(this, MainActivity::class.java)
            } else {
                Intent(this, LoginActivity::class.java)
            }
            startActivity(intent)
            finish()
        }, 3000)
    }
}
