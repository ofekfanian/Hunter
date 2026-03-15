package com.ofek.hunter.activities

import android.content.Intent
import android.os.Bundle
import android.transition.TransitionManager
import android.view.View
import android.view.animation.AnimationUtils
import androidx.appcompat.app.AppCompatActivity
import com.firebase.ui.auth.AuthUI
import com.firebase.ui.auth.FirebaseAuthUIActivityResultContract
import com.firebase.ui.auth.data.model.FirebaseAuthUIAuthenticationResult
import com.google.firebase.auth.FirebaseAuth
import com.ofek.hunter.R
import com.ofek.hunter.databinding.ActivityLoginBinding

/**
 * Login screen with email/password and Google sign-in via FirebaseUI.
 */
class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding
    private var isPasswordVisible = false

    private val signInLauncher = registerForActivityResult(FirebaseAuthUIActivityResultContract()) { result ->
        onSignInResult(result)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (FirebaseAuth.getInstance().currentUser != null) {
            navigateToMain()
            return
        }
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)
        initViews()
    }

    // Sets up click listeners for Google sign-in, email sign-in, and register buttons
    private fun initViews() {
        binding.loginCardGoogle.setOnClickListener {
            signInWith(AuthUI.IdpConfig.GoogleBuilder().build())
        }

        binding.loginBtnEmailSignIn.setOnClickListener {
            val email = binding.loginInputEmail.editText?.text.toString().trim()
            if (email.isEmpty()) {
                binding.loginInputEmail.error = getString(R.string.error_empty_email)
                return@setOnClickListener
            }
            binding.loginInputEmail.error = null

            if (!isPasswordVisible) {
                showPasswordField()
            } else {
                val password = binding.loginInputPassword.editText?.text.toString()
                if (password.isEmpty()) {
                    binding.loginInputPassword.error = getString(R.string.error_empty_password)
                    return@setOnClickListener
                }
                if (password.length < 6) {
                    binding.loginInputPassword.error = getString(R.string.error_password_too_short)
                    return@setOnClickListener
                }
                binding.loginInputPassword.error = null
                signInWithEmailPassword(email, password)
            }
        }

        binding.loginTXTRegister.setOnClickListener {
            val email = binding.loginInputEmail.editText?.text.toString().trim()
            if (!isPasswordVisible) {
                showPasswordField()
            } else {
                val password = binding.loginInputPassword.editText?.text.toString()
                if (password.isEmpty() || password.length < 6) {
                    binding.loginInputPassword.error = getString(R.string.error_password_too_short)
                    return@setOnClickListener
                }
                registerWithEmailPassword(email, password)
            }
        }
    }

    // Reveals the password field with a slide-up animation
    private fun showPasswordField() {
        isPasswordVisible = true
        TransitionManager.beginDelayedTransition(binding.root.findViewById(android.R.id.content) ?: binding.root as android.view.ViewGroup)
        binding.loginInputPassword.visibility = View.VISIBLE
        val anim = AnimationUtils.loadAnimation(this, R.anim.fade_slide_up)
        binding.loginInputPassword.startAnimation(anim)
        binding.loginInputPassword.editText?.requestFocus()
    }

    // Attempts Firebase email/password sign-in
    private fun signInWithEmailPassword(email: String, password: String) {
        binding.loginPROGRESS.visibility = View.VISIBLE
        FirebaseAuth.getInstance().signInWithEmailAndPassword(email, password)
            .addOnSuccessListener { navigateToMain() }
            .addOnFailureListener { e ->
                binding.loginPROGRESS.visibility = View.GONE
                binding.loginInputPassword.error = e.localizedMessage
            }
    }

    // Creates a new Firebase account with the given email and password
    private fun registerWithEmailPassword(email: String, password: String) {
        binding.loginPROGRESS.visibility = View.VISIBLE
        FirebaseAuth.getInstance().createUserWithEmailAndPassword(email, password)
            .addOnSuccessListener { navigateToMain() }
            .addOnFailureListener { e ->
                binding.loginPROGRESS.visibility = View.GONE
                binding.loginInputPassword.error = e.localizedMessage
            }
    }

    // Launches the FirebaseUI sign-in flow for the given provider
    private fun signInWith(provider: AuthUI.IdpConfig) {
        binding.loginPROGRESS.visibility = View.VISIBLE
        val signInIntent = AuthUI.getInstance()
            .createSignInIntentBuilder()
            .setAvailableProviders(listOf(provider))
            .setTheme(R.style.LoginTheme)
            .build()
        signInLauncher.launch(signInIntent)
    }

    // Handles the result from the FirebaseUI sign-in intent
    private fun onSignInResult(result: FirebaseAuthUIAuthenticationResult) {
        binding.loginPROGRESS.visibility = View.GONE
        if (result.resultCode == RESULT_OK) {
            navigateToMain()
        }
    }

    // Goes to the main screen and closes this activity
    private fun navigateToMain() {
        startActivity(Intent(this, MainActivity::class.java))
        finish()
    }
}
