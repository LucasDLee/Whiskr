package com.example.whiskr_app.ui.adoption.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.whiskr_app.R

class CatPictureAdapter(private val imageUrls: List<String?>)
    : RecyclerView.Adapter<CatPictureAdapter.PictureViewHolder>() {

    inner class PictureViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val imageView: ImageView = itemView.findViewById(R.id.layoutDetailsItemPictureImageView)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PictureViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.layout_details_item_picture, parent, false)
        return PictureViewHolder(view)
    }

    override fun onBindViewHolder(holder: PictureViewHolder, position: Int) {
        val imageUrl = imageUrls[position]
        Glide.with(holder.imageView.context)
            .load(imageUrl)
            .placeholder(R.drawable.cat_default)
            .into(holder.imageView)
    }

    override fun getItemCount(): Int {
        return imageUrls.size
    }

}