package com.inyoung.giphy.view

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.inyoung.giphy.R
import com.inyoung.giphy.model.GifImage

class SearchImageAdapter(
    private var images: List<GifImage>
) : RecyclerView.Adapter<SearchImageAdapter.ViewHolder>() {

    fun setImages(images: List<GifImage>) {
        this.images = images
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        LayoutInflater.from(parent.context)
            .inflate(R.layout.view_search_image, parent, false)
            .let { ViewHolder(it) }

    override fun getItemCount() = images.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) = holder.bind(images[position])

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val imageView = itemView.findViewById<ImageView>(R.id.image_view)
        fun bind(image: GifImage) {
            Glide.with(itemView.context).load(image.images.fixedWidth.url).into(imageView)
        }
    }
}