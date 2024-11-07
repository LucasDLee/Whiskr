package com.example.whiskr_app.ui.catbot

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.ListView
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.example.whiskr_app.R
import com.example.whiskr_app.databinding.FragmentCatbotAllChatsBinding

class CatbotFragment : Fragment() {

    private var _binding: FragmentCatbotAllChatsBinding? = null
    private val binding get() = _binding!!

    private lateinit var allChatMessages: ListView

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentCatbotAllChatsBinding.inflate(inflater, container, false)
        val root: View = binding.root

        allChatMessages = root.findViewById(R.id.catbot_all_chats)

        // Example data for chat sections
        val chatSections = listOf(
            ChatSection("2024-11-01", listOf("Hello!", "How are you?")),
            ChatSection("2024-11-02", listOf("Good morning!", "What's up?"))
        )

        // Set the adapter with chat sections
        val adapter = ParentListAdapter(requireContext(), chatSections)
        allChatMessages.adapter = adapter

//        allChatMessages.setOnItemClickListener { parent, view, position, id ->
//            // Just open the activity when an item is clicked
//            val intent = Intent(requireContext(), CatbotChatActivity::class.java)
//            startActivity(intent)
//        }


        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    // Data model for a chat section with a date and a list of messages
    data class ChatSection(val date: String, val messages: List<String>)

    // Custom adapter for the main ListView that holds sections with dates and messages
    inner class ParentListAdapter(context: Context, private val sections: List<ChatSection>) :
        ArrayAdapter<ChatSection>(context, 0, sections) {

        override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
            val view = convertView ?: LayoutInflater.from(context).inflate(R.layout.catbot_chats_by_day, parent, false)

            val section = sections[position]
            val chatDateText = view.findViewById<TextView>(R.id.catbot_chats_date)
            val nestedChatList = view.findViewById<ListView>(R.id.catbot_all_chats_from_date)

            // Set the date text
            chatDateText.text = section.date

            // Set up the nested ListView with messages
            val nestedAdapter = ArrayAdapter(context, android.R.layout.simple_list_item_1, section.messages)
            nestedChatList.adapter = nestedAdapter

            // Set height based on the content of the nested ListView
            setListViewHeightBasedOnItems(nestedChatList)

            return view
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
    }
}
