package com.example.whiskr_app.ui.catbot

import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.example.whiskr_app.R

class CatbotChatActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.catbot_individual_chat)

        // Get the data passed from the previous activity
//        val selectedItem = intent.getStringExtra("selected_item")
//
//        // Display the data in a TextView (or any other UI element)
//        val textView = findViewById<TextView>(R.id.chatDetailTextView)
//        textView.text = selectedItem
    }
}