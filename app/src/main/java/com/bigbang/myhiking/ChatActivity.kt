package com.bigbang.myhiking

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import com.bigbang.myhiking.model.ChatMessage
import com.bigbang.myhiking.ui.AppSingleton
import com.bigbang.myhiking.ui.AppSingleton.my_ID
import com.bigbang.myhiking.ui.adapter.MessageAdapter
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.android.synthetic.main.activity_chat.*

class ChatActivity : AppCompatActivity() {

    val messageAdapter: MessageAdapter = MessageAdapter(mutableListOf())

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat)

        chat_recyclerview.adapter = messageAdapter

        my_ID = FirebaseAuth.getInstance().currentUser?.email?:"1010"

        get_name_view.visibility = View.GONE


        send_message_button.setOnClickListener {

            val message = ChatMessage().also {
                it.messageSender = AppSingleton.my_ID
                it.messageText = message_edittext.text.toString().trim()
                message_edittext.text.clear()
            }

            FirebaseDatabase.getInstance().reference.child("LIVE_MESSAGES")
                .push().setValue(message)

        }

        FirebaseDatabase.getInstance().reference.child("LIVE_MESSAGES").addValueEventListener(
            object : ValueEventListener {
                override fun onCancelled(dataError: DatabaseError) {
                    Log.d("TAG_X", "${dataError.message}")
                }

                override fun onDataChange(snap: DataSnapshot) {
                    val messages = mutableListOf<ChatMessage>()
                    snap.children.forEach {
                        it.getValue(ChatMessage::class.java)?.let { chatMessage ->
                            messages.add(chatMessage)
                        }
                    }

                    updateMessages(messages)
                }
            }
        )
    }

    private fun updateMessages(messages: MutableList<ChatMessage>) {
        messageAdapter.messages = messages
        messageAdapter.notifyDataSetChanged()
        chat_recyclerview.scrollToPosition(messageAdapter.itemCount - 1)
    }


}