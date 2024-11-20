package com.example.whiskr_app.ui.catbot

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query

class ChatViewModel : ViewModel() {

    private val db = FirebaseFirestore.getInstance()
    val chatTitles = MutableLiveData<Map<String, String>>() // Chat ID to Title map
    val chatMessages = MutableLiveData<List<ChatMessage>>()

    // Load all chat sections (titles) from the database
    fun loadChatSections() {
        db.collection("chats").get()
            .addOnSuccessListener { documents ->
                val sections = mutableMapOf<String, String>() // Chat ID to Title map
                for (document in documents) {
                    val chatId = document.id
                    val title = document.getString("title") ?: "Untitled Chat"
                    sections[chatId] = title
                }
                chatTitles.postValue(sections) // Update LiveData with chat titles
            }
            .addOnFailureListener { error ->
                // Handle errors (optional: log the error)
            }
    }

    fun loadMessagesForChat(chatId: String) {
        db.collection("chats").document(chatId).collection("messages")
            .orderBy("timestamp", Query.Direction.ASCENDING)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    // Handle errors (optional: log the error)
                    return@addSnapshotListener
                }
                if (snapshot != null) {
                    val messages = snapshot.documents.mapNotNull { it.toObject(ChatMessage::class.java) }
                    chatMessages.postValue(messages) // Update LiveData with messages
                }
            }
    }

    // Add a new message to a specific chat
    fun addMessageToChat(chatId: String, text: String, isUser: Boolean) {
        val timestamp = System.currentTimeMillis()
        val message = ChatMessage(text = text, user = isUser, timestamp = timestamp)

        db.collection("chats").document(chatId).collection("messages")
            .add(message)
            .addOnSuccessListener {
                // Message added successfully (optional: update UI)
            }
            .addOnFailureListener { error ->
                // Handle errors (optional: log the error)
            }
    }

    // Add a new chat to the database
    fun addNewChat(chatId: String, title: String?) {
        val finalTitle = title?.takeIf { it.isNotBlank() } ?: "Untitled Chat" // Default title if none provided
        val chatData = hashMapOf("title" to finalTitle)

        db.collection("chats").document(chatId).set(chatData)
            .addOnSuccessListener {
                // Chat added successfully (optional: refresh chat list)
                loadChatSections()
            }
            .addOnFailureListener { error ->
                // Handle errors (optional: log the error)
            }
    }
}