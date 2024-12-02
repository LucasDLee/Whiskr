package com.example.whiskr_app.ui.catbot

data class ChatMessage(
    val text: String = "",
    val user: Boolean = true,
    val timestamp: Long = System.currentTimeMillis()
)



