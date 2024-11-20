package com.yunussevimli.photosharingwithfirebase.adaptor

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.squareup.picasso.Picasso
import com.yunussevimli.photosharingwithfirebase.databinding.RecyclerRowBinding
import com.yunussevimli.photosharingwithfirebase.model.Post

class PostAdaptor (val postList : ArrayList<Post>) : RecyclerView.Adapter<PostAdaptor.postsViewHolder>(){
    class postsViewHolder(val binding: RecyclerRowBinding): RecyclerView.ViewHolder(binding.root){
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): postsViewHolder {
        val binding = RecyclerRowBinding.inflate(LayoutInflater.from(parent.context),parent,false)
        return postsViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return postList.size
    }

    override fun onBindViewHolder(holder: postsViewHolder, position: Int) {
        holder.binding.recyclerCommentText.text = postList[position].comment
        holder.binding.recyclerEmail.text = postList[position].email
        Picasso.get().load(postList[position].downloadURL).into(holder.binding.recyclerImageView)
    }

}

