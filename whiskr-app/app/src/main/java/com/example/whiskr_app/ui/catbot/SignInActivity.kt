package com.example.whiskr_app.ui.catbot

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.whiskr_app.MainActivity
import com.example.whiskr_app.R
import com.example.whiskr_app.databinding.FragmentCatbotAllChatsBinding
import com.google.firebase.auth.FirebaseAuth
import com.firebase.ui.auth.AuthUI
import com.firebase.ui.auth.FirebaseAuthUIActivityResultContract

class SignInActivity : AppCompatActivity() {
    private val auth: FirebaseAuth by lazy { FirebaseAuth.getInstance() }
    private val signInLauncher = registerForActivityResult(
        FirebaseAuthUIActivityResultContract()
    ) { result ->
        if (result.resultCode == RESULT_OK) {
            // Sign-in succeeded, back to chat fragment
            val intent = Intent(this, FragmentCatbotAllChatsBinding::class.java)
            startActivity(intent)
            finish() // Close the SignInActivity
        } else {
            // fail to sign in
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            finish()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (auth.currentUser != null) {
            return
        }

        val providers = arrayListOf(
            AuthUI.IdpConfig.EmailBuilder().build(),
            AuthUI.IdpConfig.GoogleBuilder().build()
        )

        val signInIntent = AuthUI.getInstance()
            .createSignInIntentBuilder()
            .setLogo(R.drawable.icon_sign_in_text)
            .setTheme(R.style.Theme_Whiskrapp)
            .setAvailableProviders(providers)
            .setIsSmartLockEnabled(false)
            .build()

        signInLauncher.launch(signInIntent)
    }
}
