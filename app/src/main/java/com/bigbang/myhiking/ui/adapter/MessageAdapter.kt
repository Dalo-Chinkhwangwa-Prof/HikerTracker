package com.bigbang.myhiking.ui.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bigbang.myhiking.R
import com.bigbang.myhiking.model.ChatMessage
import com.bigbang.myhiking.ui.AppSingleton
import kotlinx.android.synthetic.main.my_message_item_layout.view.*

class MessageAdapter(var messages: List<ChatMessage>) :
    RecyclerView.Adapter<MessageAdapter.MessageViewHolder>() {

    inner class MessageViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MessageViewHolder {

        val view = if (viewType == 0)
            LayoutInflater.from(parent.context)
                .inflate(R.layout.my_message_item_layout, parent, false)
        else
            LayoutInflater.from(parent.context)
                .inflate(R.layout.receive_message_item_layout, parent, false)
        return MessageViewHolder(view)
    }


    override fun getItemViewType(position: Int): Int {
        if (messages[position].messageSender == AppSingleton.my_ID)
            return 0
        else
            return 1
    }

    override fun getItemCount(): Int = messages.size

    override fun onBindViewHolder(messageViewHolder: MessageViewHolder, position: Int) {

        val string = "${messages[position].messageSender}: ${messages[position].messageText}"
        messageViewHolder.itemView.message_textview.text =
            if (string.length > 100) "${string.substring(0, 100)}...[more]"
            else string
    }
}