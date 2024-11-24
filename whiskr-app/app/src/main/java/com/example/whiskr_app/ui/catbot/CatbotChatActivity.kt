package com.example.whiskr_app.ui.catbot

import android.os.Bundle
import android.widget.EditText
import android.widget.ImageButton
import android.widget.ListView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.example.whiskr_app.R
import com.google.firebase.auth.FirebaseAuth

class CatbotChatActivity : AppCompatActivity() {
    private lateinit var chatViewModel: ChatViewModel
    private lateinit var chatAdapter: ChatAdapter
    private lateinit var chatId: String
    private val auth = FirebaseAuth.getInstance()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.catbot_individual_chat)

        if (auth.currentUser == null) {
            finish() // Close the activity if user is not signed in
            return
        }

        // Get the chatId from intent
        chatId = intent.getStringExtra("chat_id") ?: return

        // Initialize ViewModel and Adapter
        chatViewModel = ViewModelProvider(this).get(ChatViewModel::class.java)
        val chatListView: ListView = findViewById(R.id.catbot_conversation)
        chatAdapter = ChatAdapter(this, mutableListOf())
        chatListView.adapter = chatAdapter

        // Observe messages for the selected chatId
        chatViewModel.chatMessages.observe(this) { messages ->
            chatAdapter.updateMessages(messages)
            // Scroll to the bottom to show the latest message
            chatListView.setSelection(chatAdapter.count - 1)
        }

        // Load messages for the selected chatId
        chatViewModel.loadMessagesForChat(chatId)

        // Set up the send button to send messages
        val sendButton: ImageButton = findViewById(R.id.sendButton)
        val editText: EditText = findViewById(R.id.editText)
        sendButton.setOnClickListener {
            val userMessage = editText.text.toString()
            if (userMessage.isNotBlank()) {
                // Add the user's message
                chatViewModel.addMessageToChat(chatId, userMessage, isUser = true)

                // TODO: Replace with actual bot response logic
                val botResponse = generateBotResponse(userMessage)
                chatViewModel.addMessageToChat(chatId, botResponse, isUser = false)

                editText.text.clear()
            }
        }
    }
    private fun generateBotResponse(userMessage: String): String {
        // Implement your bot's response logic here
        return "This is a bot response to: $userMessage"
    }
}
