package com.bigbang.myhiking.ui.view.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.bigbang.myhiking.R
import com.bigbang.myhiking.model.HikePost
import com.google.firebase.database.FirebaseDatabase
import kotlinx.android.synthetic.main.add_post_fragment_layout.*

class AddPostFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View = inflater.inflate(R.layout.add_post_fragment_layout, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        upload_post_button.setOnClickListener {
            //Upload to firebase

            val newHikePost = HikePost().also {
                it.postCaption = caption_edittext.text.toString().trim()
            }
            FirebaseDatabase.getInstance().reference.child("POSTS").child("Luis!!!222").push().setValue(newHikePost)
            caption_edittext.text.clear()
            activity?.supportFragmentManager?.popBackStack()
        }
    }
}
