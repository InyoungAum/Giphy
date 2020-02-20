package com.inyoung.giphy.view

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.util.DisplayMetrics
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.inyoung.giphy.R
import com.inyoung.giphy.model.GifImage

class SearchImageAdapter(
    private var images: List<GifImage>,
    private var displayMetrics: DisplayMetrics,
    private var spanCount: Int
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
        private val placeHolders = listOf(
            ColorDrawable(Color.parseColor("#FF5159")),
            ColorDrawable(Color.parseColor("#9F4EFF")),
            ColorDrawable(Color.parseColor("#5BEFFF")),
            ColorDrawable(Color.parseColor("#42FFAA")),
            ColorDrawable(Color.parseColor("#FFE96D"))
        )

        fun bind(image: GifImage) {
            val imageWidth = displayMetrics.widthPixels / spanCount
            val imageHeight = image.images.fixedWidth.let {
                (imageWidth  * it.height.toInt()) / it.width.toInt()
            }

            imageView.apply {
                layoutParams.width = imageWidth
                layoutParams.height = imageHeight
            }

            image.images.fixedWidth.let {
                Glide.with(itemView.context)
                    .load(it.url)
                    .placeholder(placeHolders[layoutPosition % 5])
                    .into(imageView)
            }
        }
    }
}