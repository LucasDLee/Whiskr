package com.example.whiskr_app

import android.os.Bundle
import android.widget.TextView
import com.google.android.material.bottomnavigation.BottomNavigationView
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.example.whiskr_app.databinding.ActivityMainBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlin.random.Random

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Set the custom toolbar
        val toolbar = findViewById<androidx.appcompat.widget.Toolbar>(R.id.custom_toolbar)
        setSupportActionBar(toolbar)

        val navView: BottomNavigationView = binding.navView

        val navHostFragment = supportFragmentManager.findFragmentById(R.id.nav_host_fragment_activity_main) as NavHostFragment
        val navController = navHostFragment.navController

        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        val appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.nav_home, R.id.nav_catmap, R.id.nav_catbot_all_messages
            )
        )
        setupActionBarWithNavController(navController, appBarConfiguration)
        navView.setupWithNavController(navController)

        // Reference to toolbar subtitle
        val toolbarSubtitle: TextView = findViewById(R.id.toolbar_subtitle)
        // Firebase Firestore instance
        val db = FirebaseFirestore.getInstance()

        // Fetch cat facts
        db.collection("cat_facts").get()
            .addOnSuccessListener { documents ->
                if (!documents.isEmpty) {
                    val facts = documents.map { it.getString("fact") ?: "" }
                    val randomFact = facts[Random.nextInt(facts.size)]
                    toolbarSubtitle.text = randomFact
                } else {
                    toolbarSubtitle.text = "No cat facts available!"
                }
            }
            .addOnFailureListener { exception ->
                toolbarSubtitle.text = "Failed to load cat fact."
            }
    }

    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(R.id.nav_host_fragment_activity_main)
        return navController.navigateUp() || super.onSupportNavigateUp()
    }

    /**
     * Calls the current user's Botpress key and returns it (i.e. whoever is signed into Firebase right now)
     */
    fun getBotpressToken(callback: (String?) -> Unit) {
        val firestore = com.google.firebase.firestore.FirebaseFirestore.getInstance()
        val userId = FirebaseAuth.getInstance().currentUser?.uid
        if (userId == null) {
            callback(null) // Return null if user is not logged in
            return
        }

        firestore.collection("users").document(userId).get()
            .addOnSuccessListener { document ->
                if (document.exists()) {
                    val token = document.getString("key")
                    callback(token) // Return the token value
                } else {
                    callback(null) // Return null if document doesn't exist
                }
            }
            .addOnFailureListener { exception ->
                callback(null) // Return null on failure
            }
    }

}