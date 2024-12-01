package com.example.whiskr_app.ui.catbot

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query

class ChatViewModel : ViewModel() {

    private val db = FirebaseFirestore.getInstance()
    private val userId: String?
        get() = FirebaseAuth.getInstance().currentUser?.uid
    val chatTitles = MutableLiveData<Map<String, String>>()
    val chatMessages = MutableLiveData<List<ChatMessage>>()

    /**
     * Load all chat sections (titles) from the database
     */
    fun loadChatSections() {
        val uid = userId ?: return
        db.collection("users").document(uid).collection("chats").get()
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
                chatTitles.postValue(emptyMap())
            }
    }

    /**
     * Load all messages from a given conversation
     */
    fun loadMessagesForChat(chatId: String) {
        val uid = userId ?: return
        db.collection("users").document(uid).collection("chats").document(chatId).collection("messages")
            .orderBy("timestamp", Query.Direction.ASCENDING)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    return@addSnapshotListener
                }
                if (snapshot != null) {
                    val messages = snapshot.documents.mapNotNull { it.toObject(ChatMessage::class.java) }
                    chatMessages.postValue(messages) // Update LiveData with messages
                }
            }
    }

    /**
     * Add a new message to a specific chat
     */
    fun addMessageToChat(chatId: String, text: String, isUser: Boolean) {
        val uid = userId ?: return
        val timestamp = System.currentTimeMillis()
        val message = ChatMessage(text = text, user = isUser, timestamp = timestamp)

        db.collection("users").document(uid).collection("chats").document(chatId).collection("messages")
            .add(message)
    }

    /**
     * Add a new chat to the database
     */
    fun addNewChat(chatId: String, title: String?) {
        val uid = userId ?: return
        val finalTitle = title?.takeIf { it.isNotBlank() } ?: "Untitled Chat" // Default title if none provided
        val chatData = hashMapOf("title" to finalTitle)

        db.collection("users").document(uid).collection("chats").document(chatId)
            .set(chatData)
            .addOnSuccessListener {
                // refresh chat list
                loadChatSections()
            }
            .addOnFailureListener { error ->
                // Handle errors
            }
    }
}