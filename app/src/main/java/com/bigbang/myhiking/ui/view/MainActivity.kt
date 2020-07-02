package com.bigbang.myhiking.ui.view

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.recyclerview.widget.LinearLayoutManager
import com.bigbang.myhiking.ChatActivity
import com.bigbang.myhiking.ui.view.fragment.AddPostFragment
import com.bigbang.myhiking.R
import com.bigbang.myhiking.model.HikePost
import com.bigbang.myhiking.ui.adapter.PostAdapter
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    private val addPostFragment: AddPostFragment =
        AddPostFragment()

    private val postAdapter: PostAdapter = PostAdapter(mutableListOf())

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        content_recyclerview.layoutManager = LinearLayoutManager(this)
        content_recyclerview.adapter = postAdapter


        FirebaseDatabase.getInstance().reference.child("POSTS")
            .child(FirebaseAuth.getInstance().uid?:"")
            .addValueEventListener(object : ValueEventListener {

                override fun onCancelled(databaseError: DatabaseError) {
                    Log.d("TAG_X", "${databaseError.message}")
                }

                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    val list: MutableList<HikePost> = mutableListOf()

                    dataSnapshot.children.forEach { data ->
                        data.getValue(HikePost::class.java)?.let { hikePost ->
                            list.add(hikePost)
                        }
                    }

                    updateHikes(list)

                }
            })

        open_chat_button.setOnClickListener {
            startActivity(Intent(this, ChatActivity::class.java))
        }

        new_post_button.setOnClickListener {

            supportFragmentManager.beginTransaction()
                .setCustomAnimations(
                    R.anim.slide_in,
                    R.anim.slide_out,
                    R.anim.slide_in,
                    R.anim.slide_out
                )
                .add(R.id.main_frame, addPostFragment)
                .addToBackStack(addPostFragment.tag)
                .commit()
        }


    }

    private fun updateHikes(list: MutableList<HikePost>) {
        postAdapter.list = list
        postAdapter.notifyDataSetChanged()
    }
}