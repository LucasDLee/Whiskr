package com.example.whiskr_app.ui.catbot

import android.os.Bundle
import android.widget.EditText
import android.widget.ImageButton
import android.widget.ListView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.example.whiskr_app.MainActivity
import com.example.whiskr_app.R
import com.google.firebase.auth.FirebaseAuth
import okhttp3.MediaType
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody
import org.json.JSONObject

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

        (applicationContext as? MainActivity)?.getBotpressToken { token ->
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

                // TODO: Replace with actual bot response logic
                val botResponse = generateBotResponse(chatId, userMessage)
                chatViewModel.addMessageToChat(chatId, botResponse, isUser = false)

                editText.text.clear()
            }
        }
    }

    /**
     * Function posts the message to Botpress and gets the list of messages from the conversation.
     * We get the most recent message as our response as there's always one user message for every bot message
     */
    private fun generateBotResponse(chatId: String, userMessage: String): String {
        // Implement your bot's response logic here

        // Step 1: Post the user's message to the conversation
        val body = RequestBody.create(mediaType, "{\"payload\":{\"type\":\"text\",\"text\":\"${userMessage}\"}}")
        val postRequest = Request.Builder()
            .url("https://chat.botpress.cloud/${chatbotConnectionUrl}/messages")
            .post(body)
            .addHeader("accept", "application/json")
            .addHeader("content-type", "application/json")
            .build()

        try {
            val postResponse = client.newCall(postRequest).execute()
            if (!postResponse.isSuccessful) {
                return "Failed to send message: ${postResponse.message}"
            }
        } catch (e: Exception) {
            e.printStackTrace()
            return "An error occurred: ${e.message}"
        }

        // Step 2: Fetch the updated conversation messages
        val getRequest = Request.Builder()
            .url("https://chat.botpress.cloud/${chatbotConnectionUrl}/conversations/${chatId}/messages")
            .get()
            .addHeader("accept", "application/json")
            .addHeader("x-user-key", chatbotToken)
            .build()

        try {
            val getResponse = client.newCall(getRequest).execute()
            if (!getResponse.isSuccessful) {
                return "Failed to fetch messages: ${getResponse.message}"
            }

            val responseBody = getResponse.body?.string() ?: return "Empty response"
            val jsonObject = JSONObject(responseBody)

            // Access the "messages" array
            val messagesArray = jsonObject.getJSONArray("messages")
            if (messagesArray.length() == 0) {
                return "No messages found"
            }

            // Get the first message
            val mostRecentMessage = messagesArray.getJSONObject(0)
            val payload = mostRecentMessage.getJSONObject("payload")
            val text = payload.getString("text")

            return text
        } catch (e: Exception) {
            e.printStackTrace()
            return "An error occurred while fetching the bot response: ${e.message}"
        }
    }
}
