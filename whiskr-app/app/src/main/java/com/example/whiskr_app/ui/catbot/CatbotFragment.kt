package com.example.whiskr_app.ui.catbot

import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.ListView
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.whiskr_app.MainActivity
import com.example.whiskr_app.R
import com.example.whiskr_app.databinding.FragmentCatbotAllChatsBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import okhttp3.MediaType
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody
import org.json.JSONObject

class CatbotFragment : Fragment() {

    private var _binding: FragmentCatbotAllChatsBinding? = null
    private val binding get() = _binding!!

    private lateinit var allChatMessages: ListView
    private lateinit var chatViewModel: ChatViewModel
    private lateinit var parentListAdapter: ParentListAdapter
    private val auth = FirebaseAuth.getInstance()
    private val selectedChatIds = mutableListOf<String>()


    // Botpress API
    private var client: OkHttpClient = OkHttpClient()
    private var mediaType: MediaType? = "application/json".toMediaTypeOrNull()
    private lateinit var chatbotToken: String
    private lateinit var chatbotConnectionUrl: String

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentCatbotAllChatsBinding.inflate(inflater, container, false)
        val root: View = binding.root

        if (auth.currentUser == null) {
            redirectToLogin()
            return root
        }

        // See if we need to create a Botpress user for the email
        checkIfBotpressTokenIsEmpty { isEmpty ->
            if (isEmpty) {
                createBotpressUser()
            }
        }

        // Set the token assuming we've already created the user
        (activity as? MainActivity)?.getBotpressToken { token ->
            chatbotToken = token.toString()
        }

        chatbotConnectionUrl = resources.getString(R.string.catbot_webhook_key)

        chatViewModel = ViewModelProvider(this).get(ChatViewModel::class.java)

        allChatMessages = binding.catbotAllChats

        // Observe chat sections and update the UI
        chatViewModel.chatTitles.observe(viewLifecycleOwner) { titles ->
            val chatSections = titles?.map { ChatSection(it.key, it.value) } ?: emptyList()
            parentListAdapter = ParentListAdapter(requireContext(), chatSections)
            allChatMessages.adapter = parentListAdapter
        }

        // Load the chat sections from the database
        chatViewModel.loadChatSections()

        // Handle start new chat button
        binding.fabNewChat.setOnClickListener {
            showNewChatDialog()
        }

        // Handle Sign Out button
        val signOutButton: Button = binding.signOutButton
        signOutButton.setOnClickListener {
            signOutUser()
        }

        val deleteButton: Button = binding.deleteButton
        deleteButton.setOnClickListener {
            if (selectedChatIds.isNotEmpty()) {
                // Confirm deletion
                AlertDialog.Builder(requireContext())
                    .setTitle("Delete Chats")
                    .setMessage("Are you sure you want to delete the selected chats?")
                    .setPositiveButton("Delete") { _, _ ->
                        deleteSelectedChats()
                    }
                    .setNegativeButton("Cancel", null)
                    .show()
            } else {
                Toast.makeText(requireContext(), "Hold down a chat to select for deletion.", Toast.LENGTH_SHORT).show()
            }
        }

        return root
    }

    override fun onResume() {
        super.onResume()
        if (auth.currentUser == null) {
            redirectToLogin()
        } else {
            chatViewModel.loadChatSections()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    /**
     * Calls the Botpress API to build a JWT key for the user
     */
    private fun createBotpressUser() {
        val userId = FirebaseAuth.getInstance().currentUser?.uid
        if (userId != null) {
            val body = RequestBody.create(mediaType, "{\"name\":\"${userId}\",\"id\":\"${userId}\"}")
            val request = Request.Builder()
                .url("https://chat.botpress.cloud/${chatbotConnectionUrl}/users")
                .post(body)
                .addHeader("accept", "application/json")
                .addHeader("content-type", "application/json")
                .build()

            // Run the network request asynchronously using a background thread
            Thread {
                try {
                    val getRequest = client.newCall(request).execute()
                    val responseBody = getRequest.body?.string() ?: "Null"
                    val jsonObject = JSONObject(responseBody)

                    // Extract the key
                    val key = jsonObject.getString("key")

                    // Now update Firestore with the extracted key
                    val firestore = com.google.firebase.firestore.FirebaseFirestore.getInstance()
                    val userRef = firestore.collection("users").document(userId)

                    // Data to add or update
                    val userData = hashMapOf(
                        "key" to key
                    )

                    userRef.set(userData).addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            println("User data updated with key.")
                        } else {
                            println("Failed to update user data: ${task.exception?.message}")
                        }
                    }

                    // Set the token after creation
                    (activity as? MainActivity)?.getBotpressToken { token ->
                        chatbotToken = token.toString()
                    }
                } catch (e: Exception) {
                    println("Error: ${e.message}")
                }
            }.start()
        } else {
            println("User is not logged in.")
        }
    }

    /**
     * Checks if we've created a Botpress account with a user's email
     * Returns a BOOLEAN to indicate a yes/no answer
     * Yes: No Botpress token made yet
     * No: We've already made a Botpress token so don't make it again
     */
    private fun checkIfBotpressTokenIsEmpty(callback: (Boolean) -> Unit) {
        val firestore = com.google.firebase.firestore.FirebaseFirestore.getInstance()
        val userId = FirebaseAuth.getInstance().currentUser?.uid
        if (userId == null) {
            Toast.makeText(requireContext(), "User not logged in", Toast.LENGTH_SHORT).show()
            callback(false) // Return false since the user is not logged in
            return
        }

        firestore.collection("users").document(userId).get()
            .addOnSuccessListener { document ->
                if (document.exists()) {
                    val username = document.getString("key")
                    if (username.isNullOrEmpty()) {
                        callback(true) // Token is empty
                    } else {
                        callback(false) // Token is not empty
                    }
                } else {
                    callback(true) // Assume empty if document doesn't exist
                }
            }
            .addOnFailureListener {
                callback(false) // On failure, assume not empty to avoid unintended consequences
            }
    }

    /**
     * Signs the user out of Firebase
     */
    private fun signOutUser() {
        auth.signOut()
        Toast.makeText(requireContext(), "You have been signed out.", Toast.LENGTH_SHORT).show()
        // Navigate back to Home Page
        val intent = Intent(requireContext(), MainActivity::class.java)
        startActivity(intent)
    }

    /**
     * Sends the user to the Firebase login page
     */
    private fun redirectToLogin() {
        val intent = Intent(requireContext(), SignInActivity::class.java)
        startActivity(intent)
    }

    /**
     * Custom adapter for the main ListView that holds sections with dates and messages
     */
    inner class ParentListAdapter(context: Context, private val sections: List<ChatSection>) :
        ArrayAdapter<ChatSection>(context, 0, sections) {

        override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
            val view = convertView ?: LayoutInflater.from(context)
                .inflate(R.layout.catbot_chats_by_title, parent, false)

            val section = sections[position]
            val chatTitleText = view.findViewById<TextView>(R.id.catbot_chats_title)
            chatTitleText.text = section.title

            val chatTitleTextEdit = view.findViewById<ImageView>(R.id.edit_chat_name)

            // Set up long-press event for selection
            chatTitleText.setOnLongClickListener {
                if (selectedChatIds.contains(section.chatId)) {
                    selectedChatIds.remove(section.chatId) // Deselect chat
                    view.setBackgroundColor(context.getColor(android.R.color.transparent))
                } else {
                    selectedChatIds.add(section.chatId) // Select chat
                    view.setBackgroundColor(context.getColor(android.R.color.holo_orange_light))
                }
                notifyDataSetChanged()
                handleDeleteButtonVisibility()
                true // Indicate the long-click is handled
            }

            // Set up regular click event
            chatTitleText.setOnClickListener {
                if (selectedChatIds.isNotEmpty()) {
                    // In selection mode, clicking toggles selection
                    if (selectedChatIds.contains(section.chatId)) {
                        selectedChatIds.remove(section.chatId)
                        view.setBackgroundColor(context.getColor(android.R.color.transparent))
                    } else {
                        selectedChatIds.add(section.chatId)
                        view.setBackgroundColor(context.getColor(android.R.color.holo_orange_light))
                    }
                    notifyDataSetChanged()
                    handleDeleteButtonVisibility()
                } else {
                    // Not in selection mode, proceed to chat activity
                    val intent = Intent(context, CatbotChatActivity::class.java)
                    intent.putExtra("chat_id", section.chatId)
                    intent.putExtra("chat_title", section.title)
                    context.startActivity(intent)
                }
            }

            // Set up edit title button
            chatTitleTextEdit.setOnClickListener {
                editChatDialog(section.chatId, section.title)
            }

            return view
        }

        fun getChatId(position: Int): String = sections[position].chatId
    }

    /**
     * Helper function to adjust ListView height based on items
     */
    private fun setListViewHeightBasedOnItems(listView: ListView) {
        val listAdapter = listView.adapter ?: return
        var totalHeight = 0
        for (i in 0 until listAdapter.count) {
            val listItem = listAdapter.getView(i, null, listView)
            listItem.measure(0, 0)
            totalHeight += listItem.measuredHeight
        }
        val params = listView.layoutParams
        params.height = totalHeight + (listView.dividerHeight * (listAdapter.count - 1))
        listView.layoutParams = params
    }

    /**
     * Displays the dialog for the user to edit their chat title
     */
    private fun editChatDialog(chatId: String, currentTitle: String) {
        val builder = AlertDialog.Builder(requireContext())
        builder.setTitle("Edit Chat Title")

        val input = EditText(requireContext())
        input.setText(currentTitle)
        input.hint = "Enter new chat title"
        builder.setView(input)

        builder.setPositiveButton("Save") { _, _ ->
            val newChatTitle = input.text.toString().trim()
            if (newChatTitle.isNotEmpty()) {
                updateChatTitle(chatId, newChatTitle)
                Toast.makeText(requireContext(), "Chat title updated.", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(requireContext(), "Chat title cannot be empty.", Toast.LENGTH_SHORT).show()
            }
        }

        builder.setNegativeButton("Cancel") { dialog, _ ->
            dialog.cancel()
        }

        builder.show()
    }

    /**
     * Changes the chat title in the app and Firebase
     */
    fun updateChatTitle(chatId: String, newTitle: String) {
        val userId = FirebaseAuth.getInstance().currentUser?.uid ?: return
        val db = FirebaseFirestore.getInstance()

        val chatRef = db.collection("users").document(userId).collection("chats").document(chatId)
        chatRef.update("title", newTitle)
            .addOnSuccessListener {
                // Reload chat sections to reflect the changes
                chatViewModel.loadChatSections()
            }
            .addOnFailureListener { e ->
                Log.e("ChatViewModel", "Failed to update chat title: ${e.message}")
            }
    }

    /**
     * Show a dialog for the user to enter the title of the new chat
     */
    private fun showNewChatDialog() {
        val builder = AlertDialog.Builder(requireContext())
        builder.setTitle("Start New Chat")

        val input = EditText(requireContext())
        input.hint = "Enter chat title (optional)"
        builder.setView(input)

        builder.setPositiveButton("Create") { _, _ ->
            val chatTitle = input.text.toString().trim()
            createChatInDatabase(chatTitle)
        }

        builder.setNegativeButton("Cancel") { dialog, _ ->
            dialog.cancel()
        }

        builder.show()
    }

    /**
     * Create a new chat in the database
     */
    private fun createChatInDatabase(chatTitle: String) {
        val chatId = java.util.UUID.randomUUID().toString()

        // Create the conversation in Botpress
        val body = RequestBody.create(mediaType, "{\"id\":\"${chatId}\"}")
        val request = Request.Builder()
            .url("https://chat.botpress.cloud/${chatbotConnectionUrl}/conversations")
            .post(body)
            .addHeader("accept", "application/json")
            .addHeader("x-user-key", chatbotToken)
            .addHeader("content-type", "application/json")
            .build()

        Thread {
            client.newCall(request).execute()
        }.start()

        chatViewModel.addNewChat(chatId, chatTitle)

        // Navigate to the new chat
        val intent = Intent(requireContext(), CatbotChatActivity::class.java)
        intent.putExtra("chat_id", chatId)
        intent.putExtra("chat_title", chatTitle)
        startActivity(intent)
    }

    /**
     * Find a chat and delete it in both Firebase and Botpress
     */
    private fun deleteSelectedChats() {
        val userId = FirebaseAuth.getInstance().currentUser?.uid
        if (userId == null) {
            Toast.makeText(requireContext(), "User not logged in.", Toast.LENGTH_SHORT).show()
            return
        }

        val db = FirebaseFirestore.getInstance()
        val userChatsRef = db.collection("users").document(userId).collection("chats")

        // Delete each selected chat
        for (chatId in selectedChatIds) {
            // Delete the messages sub-collection
            val messagesRef = userChatsRef.document(chatId).collection("messages")
            messagesRef.get().addOnSuccessListener { snapshot ->
                for (document in snapshot.documents) {
                    document.reference.delete() // Delete each message document
                }

                // Delete the chat document after deleting messages
                userChatsRef.document(chatId).delete()
                    .addOnSuccessListener {
                        // Chat deleted successfully
                        selectedChatIds.remove(chatId)
                        val deleteConversationInBotPress = Request.Builder()
                            .url("https://chat.botpress.cloud/${chatbotConnectionUrl}/conversations/${chatId}")
                            .delete(null)
                            .addHeader("accept", "application/json")
                            .addHeader("x-user-key", chatbotToken)
                            .build()

                        Thread {
                            client.newCall(deleteConversationInBotPress).execute()
                        }.start()
                        chatViewModel.loadChatSections() // Reload chat list
                        Toast.makeText(requireContext(), "Chat deleted.", Toast.LENGTH_SHORT).show()
                    }
                    .addOnFailureListener { e ->
                        // Handle failure
                        Toast.makeText(requireContext(), "Failed to delete chat: ${e.message}", Toast.LENGTH_SHORT).show()
                    }
            }.addOnFailureListener { e ->
                // Handle failure to retrieve sub-collection
                Toast.makeText(requireContext(), "Failed to load messages for deletion: ${e.message}", Toast.LENGTH_SHORT).show()
            }
        }

        // Clear selection after deletion
        selectedChatIds.clear()
        handleDeleteButtonVisibility()
    }

    private fun handleDeleteButtonVisibility() {
        if (selectedChatIds.isNotEmpty()) {
            binding.deleteButton.visibility = View.VISIBLE
        } else {
            binding.deleteButton.visibility = View.VISIBLE // change VISIBLE to GONE if you don't want it to show up
        }
    }
}