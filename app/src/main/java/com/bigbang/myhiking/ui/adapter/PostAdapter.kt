package com.bigbang.myhiking.ui.adapter

import android.app.Activity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.ablanco.zoomy.Zoomy
import com.bigbang.myhiking.R
import com.bigbang.myhiking.model.HikePost
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import kotlinx.android.synthetic.main.hiking_post_item_layout.view.*

class PostAdapter(var list: List<HikePost>) : RecyclerView.Adapter<PostAdapter.PostViewHolder>() {

    inner class PostViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        fun bind(hikePost: HikePost) = with(itemView) {
            post_caption_textview.text = hikePost.postCaption
            Glide.with(context)
                .applyDefaultRequestOptions(RequestOptions().centerCrop())
                .load(hikePost.imageUrl)
                .into(post_imageview)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PostViewHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.hiking_post_item_layout, parent, false)
        return PostViewHolder(itemView)
    }

    override fun getItemCount(): Int {
        return list.size
    }

    override fun onBindViewHolder(holder: PostViewHolder, position: Int) {
        val zoomyBuilder: Zoomy.Builder = Zoomy.Builder(holder.itemView.context as Activity)
            .target(holder.itemView.post_imageview)
        zoomyBuilder.register()
        holder.bind(list[position])
    }
}