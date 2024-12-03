package com.example.whiskr_app.ui.catbot

import android.os.Bundle
import android.widget.EditText
import android.widget.ImageButton
import android.widget.ListView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.example.whiskr_app.R
import com.google.firebase.auth.FirebaseAuth
import okhttp3.MediaType
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody
import org.json.JSONObject
import kotlinx.coroutines.*

class CatbotChatActivity : AppCompatActivity() {
    private lateinit var chatViewModel: ChatViewModel
    private lateinit var chatAdapter: ChatAdapter
    private lateinit var chatId: String
    private val auth = FirebaseAuth.getInstance()

    // Botpress API
    private var client: OkHttpClient = OkHttpClient()
    private var mediaType: MediaType? = "application/json".toMediaTypeOrNull()
    private lateinit var chatbotToken: String
    private lateinit var chatbotConnectionUrl: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.catbot_individual_chat)

        // Set up the Toolbar
        val toolbar = findViewById<androidx.appcompat.widget.Toolbar>(R.id.AppBar)
        setSupportActionBar(toolbar)

        // Enable the back button functionality
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)

        // Set the back button click listener
        toolbar.setNavigationOnClickListener {
            finish() // Navigates back to the previous screen
        }

        // Set the token and Botpress URL
        getBotpressToken { token ->
            chatbotToken = token.toString()
        }
        chatbotConnectionUrl = resources.getString(R.string.catbot_webhook_key)

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
                generateBotResponse(chatId, userMessage)
                editText.text.clear()
            }
        }
    }

    /**
     * Calls the current user's Botpress key and returns it (i.e. whoever's signed in the app right now)
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

    /**
     * This function gets the current set of messages and checks if we've updated it with our new message
     * Steps:
     * 1) Get our current message list and return the number of messages in it
     * 2) Post our response to Botpress
     * 3) Wait until our message list has updated with both responses
     */
    private fun generateBotResponse(chatId: String, userMessage: String): String {
        var responseMessage = ""

        runBlocking {
            try {
                // Step 1: Get the initial message count
                val initialCount = withContext(Dispatchers.IO) { getMessageCount(chatId) }

                if (initialCount == null) {
                    responseMessage = "Failed to fetch initial messages."
                    return@runBlocking
                }

                // Step 2: Post the user's message
                val postSuccessful = withContext(Dispatchers.IO) { postUserMessage(chatId, userMessage) }

                if (!postSuccessful) {
                    responseMessage = "Failed to send user message."
                    return@runBlocking
                }

                // Step 3: Poll for updates
                responseMessage = withContext(Dispatchers.IO) { pollForNewMessage(chatId, initialCount) }
            } catch (e: Exception) {
                e.printStackTrace()
                responseMessage = "An error occurred: ${e.message}"
            }
        }

        chatViewModel.addMessageToChat(chatId, responseMessage, isUser = false)
        return responseMessage
    }

    /**
     * Helper function to get the total number of messages
     */
    private fun getMessageCount(chatId: String): Int? {
        val getRequest = Request.Builder()
            .url("https://chat.botpress.cloud/${chatbotConnectionUrl}/conversations/${chatId}/messages")
            .get()
            .addHeader("accept", "application/json")
            .addHeader("x-user-key", chatbotToken)
            .build()

        return try {
            val response = client.newCall(getRequest).execute()
            if (response.isSuccessful) {
                val responseBody = response.body?.string()
                val jsonObject = JSONObject(responseBody ?: "{}")
                val messagesArray = jsonObject.getJSONArray("messages")
                messagesArray.length()
            } else {
                null
            }
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    /**
     * Function posts the message to Botpress
     */
    private fun postUserMessage(chatId: String, userMessage: String): Boolean {
        val body = RequestBody.create(
            mediaType,
            "{\"payload\":{\"type\":\"text\",\"text\":\"${userMessage}\"},\"conversationId\":\"${chatId}\"}"
        )
        val postRequest = Request.Builder()
            .url("https://chat.botpress.cloud/${chatbotConnectionUrl}/messages")
            .post(body)
            .addHeader("accept", "application/json")
            .addHeader("x-user-key", chatbotToken)
            .addHeader("content-type", "application/json")
            .build()

        return try {
            val response = client.newCall(postRequest).execute()
            println("POST REQUESTING ${response}")
            response.isSuccessful
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }

    /**
     * Helper function to get the new list of messages
     */
    private suspend fun pollForNewMessage(chatId: String, initialCount: Int): String {
        val maxRetries = 10
        val delayMillis = 3000L

        repeat(maxRetries) { attempt ->
            val currentCount = getMessageCount(chatId)

            if (currentCount == null) {
                return "Failed to fetch messages after posting."
            }

            // Must do initialCount + 1 as we always get a set of 2 messages (1 from the user, 1 from the bot)
            if (currentCount > initialCount + 1) {
                // Fetch the most recent message
                val getRequest = Request.Builder()
                    .url("https://chat.botpress.cloud/${chatbotConnectionUrl}/conversations/${chatId}/messages")
                    .get()
                    .addHeader("accept", "application/json")
                    .addHeader("x-user-key", chatbotToken)
                    .build()

                val response = client.newCall(getRequest).execute()
                if (response.isSuccessful) {
                    val responseBody = response.body?.string()
                    val jsonObject = JSONObject(responseBody ?: "{}")
                    val messagesArray = jsonObject.getJSONArray("messages")
                    val mostRecentMessage = messagesArray.getJSONObject(0)
                    val payload = mostRecentMessage.getJSONObject("payload")

                    return payload.getString("text")
                }
                return "Failed to fetch the updated message."
            }

            // Wait before trying again
            delay(delayMillis)
            Toast.makeText(this, "Generating Catbot response...", Toast.LENGTH_SHORT).show()
        }

        return "CatBot update timed out after ${maxRetries} attempts."
    }
}
