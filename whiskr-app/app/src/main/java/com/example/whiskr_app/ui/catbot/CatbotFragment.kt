package com.example.whiskr_app.ui.catbot

import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.EditText
import android.widget.ListView
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.core.content.ContextCompat.startActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.whiskr_app.R
import com.example.whiskr_app.databinding.FragmentCatbotAllChatsBinding
import java.util.UUID

class CatbotFragment : Fragment() {

    private var _binding: FragmentCatbotAllChatsBinding? = null
    private val binding get() = _binding!!

    private lateinit var allChatMessages: ListView
    private lateinit var chatViewModel: ChatViewModel
    private lateinit var parentListAdapter: ParentListAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentCatbotAllChatsBinding.inflate(inflater, container, false)
        val root: View = binding.root

        chatViewModel = ViewModelProvider(this).get(ChatViewModel::class.java)

        allChatMessages = binding.catbotAllChats

        // Observe chat sections and update the UI
        chatViewModel.chatTitles.observe(viewLifecycleOwner) { titles ->
            if (titles != null && titles.isNotEmpty()) {
                val chatSections = titles.map { ChatSection(it.key, it.value) }
                parentListAdapter = ParentListAdapter(requireContext(), chatSections)
                allChatMessages.adapter = parentListAdapter

                // Handle clicks on a chat to open the Chat Activity
                allChatMessages.setOnItemClickListener { _, _, position, _ ->
                    val selectedChatId = parentListAdapter.getChatId(position)
                    val selectedChatTitle = parentListAdapter.getItem(position)?.title

                    val intent = Intent(requireContext(), CatbotChatActivity::class.java)
                    intent.putExtra("chat_id", selectedChatId)
                    intent.putExtra("chat_title", selectedChatTitle)
                    startActivity(intent)
                }
            } else {
                // Handle empty list scenario
                Toast.makeText(requireContext(), "No chats available", Toast.LENGTH_SHORT).show()
            }
        }

        // Load the chat sections from the database
        chatViewModel.loadChatSections()

        // Handle start new chat button
        binding.fabNewChat.setOnClickListener {
            showNewChatDialog()
        }

        return root
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    // Custom adapter for the main ListView that holds sections with dates and messages
    inner class ParentListAdapter(context: Context, private val sections: List<ChatSection>) :
        ArrayAdapter<ChatSection>(context, 0, sections) {

        override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
            val view = convertView ?: LayoutInflater.from(context)
                .inflate(R.layout.catbot_chats_by_title, parent, false)

            val section = sections[position]
            val chatTitleText = view.findViewById<TextView>(R.id.catbot_chats_title)
            chatTitleText.text = section.title

            return view
        }

        // Get the chat ID for a specific position
        fun getChatId(position: Int): String = sections[position].chatId
    }


    // Helper function to adjust ListView height based on items
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

    // Show a dialog for the user to enter the title of the new chat
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

    // Create a new chat in the database
    private fun createChatInDatabase(chatTitle: String) {
        val finalTitle = chatTitle ?: "Untitled Chat"
        // Default to "Untitled Chat" if no title is provided
        val chatId = java.util.UUID.randomUUID().toString()
        chatViewModel.addNewChat(chatId, chatTitle)

        // Navigate to the new chat
        val intent = Intent(requireContext(), CatbotChatActivity::class.java)
        intent.putExtra("chat_id", chatId)
        intent.putExtra("chat_title", chatTitle)
        startActivity(intent)
    }
}

