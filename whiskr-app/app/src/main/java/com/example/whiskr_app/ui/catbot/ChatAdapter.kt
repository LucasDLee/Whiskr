package com.example.whiskr_app.ui.catbot

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.TextView
import com.example.whiskr_app.R

class ChatAdapter(private val context: Context, private var messages: MutableList<ChatMessage>) :
    BaseAdapter() {

    companion object {
        private const val VIEW_TYPE_USER = 0
        private const val VIEW_TYPE_BOT = 1
    }

    override fun getCount(): Int = messages.size

    override fun getItem(position: Int): ChatMessage = messages[position]

    override fun getItemId(position: Int): Long = position.toLong()

    override fun getViewTypeCount(): Int = 2

    override fun getItemViewType(position: Int): Int {
        return if (messages[position].user) VIEW_TYPE_USER else VIEW_TYPE_BOT
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val message = getItem(position)
        val viewType = getItemViewType(position)

        val view = convertView ?: LayoutInflater.from(context).inflate(
            if (viewType == VIEW_TYPE_USER) R.layout.catbot_user_message else R.layout.catbot_gpt_message,
            parent,
            false
        )

        val messageText = view.findViewById<TextView>(
            if (viewType == VIEW_TYPE_USER) R.id.catbot_user_message else R.id.catbot_bot_message
        )
        messageText.text = message.text

        return view
    }

    /**
     * Display the updated messages onto the app
     */
    fun updateMessages(newMessages: List<ChatMessage>) {
        messages.clear()  // Clear the existing messages
        messages.addAll(newMessages)  // Add the new messages
        notifyDataSetChanged()  // Notify the UI to refresh
    }
}
