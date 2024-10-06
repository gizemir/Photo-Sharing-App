package com.gizemir.photosharingapp.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.Adapter
import com.gizemir.photosharingapp.databinding.RecyclerRowBinding
import com.gizemir.photosharingapp.model.Post
import com.squareup.picasso.Picasso

class PostAdapter(private val postList:ArrayList<Post>): RecyclerView.Adapter<PostAdapter.PostViewHolder>() {
    class PostViewHolder(val binding: RecyclerRowBinding): RecyclerView.ViewHolder(binding.root){

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PostViewHolder {
        val binding = RecyclerRowBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return PostViewHolder(binding)
    }

    override fun getItemCount(): Int {
       return  postList.size
    }

    override fun onBindViewHolder(holder: PostViewHolder, position: Int) {
        holder.binding.textViewRecyclerMail.text = postList[position].email
        holder.binding.textViewRecyclerComment.text = postList[position].comment
        Picasso.get().load(postList[position].downloadUrl).into(holder.binding.imageViewRecyclerImage)
    }


}