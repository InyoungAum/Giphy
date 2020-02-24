package com.inyoung.giphy.view

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.util.DisplayMetrics
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.ProgressBar
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.inyoung.giphy.model.GifImage
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.inyoung.giphy.R
import kotlinx.android.synthetic.main.view_search_image.view.*

class ImageAdapter(
    private val images: MutableList<GifImage>,
    private val displayMetrics: DisplayMetrics,
    private val spanCount: Int,
    private val onItemClickListener: OnItemClickListener? = null
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    interface OnItemClickListener {
        fun onItemClick(id: String)
    }

    companion object {
        private const val VIEW_TYPE_ITEM = 0
        private const val VIEW_TYPE_LOADER = 1
        private val dummy = GifImage()
    }

    fun loadImage() {
        // add loading progress
        images.add(dummy)
        notifyDataSetChanged()
    }

    fun addImages(images: List<GifImage>) {
        this.images.apply {
            if (isNotEmpty()) {
                remove(dummy)
            }
            addAll(images)
        }
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        when(viewType) {
            VIEW_TYPE_LOADER ->
                LayoutInflater.from(parent.context)
                    .inflate(R.layout.view_search_progress, parent, false)
                    .let { LoadViewHolder(it) }
            else ->
                LayoutInflater.from(parent.context)
                    .inflate(R.layout.view_search_image, parent, false)
                    .let { ItemViewHolder(it) }
        }

    override fun getItemViewType(position: Int) =
        if (images[position].images == null) VIEW_TYPE_LOADER
        else VIEW_TYPE_ITEM

    override fun getItemCount() = images.size

    override fun onBindViewHolder(viewHolder: RecyclerView.ViewHolder, position: Int) =
        if (isLoadingView(position)) {
            val layoutParams =
                viewHolder.itemView.layoutParams as StaggeredGridLayoutManager.LayoutParams
            layoutParams.setFullSpan(true)
        }
        else (viewHolder as ItemViewHolder).bind(images[position])

    override fun onViewRecycled(holder: RecyclerView.ViewHolder) {
        super.onViewRecycled(holder)
        if (holder is ItemViewHolder) {
            Glide.with(holder.itemView.context).clear(holder.itemView.image_view)
        }
    }

    private fun isLoadingView(position: Int) = images[position].images == null

    inner class ItemViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
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
            val imageHeight = image.images?.fixedWidth?.let {
                (imageWidth * it.height.toInt()) / it.width.toInt()
            }
            //TODO: null check 대충한거 수정 하자
            imageView.apply {
                layoutParams.width = imageWidth
                layoutParams.height = imageHeight ?: 0
                setOnClickListener { onItemClickListener?.onItemClick(image.id) }
            }

            image.images?.fixedWidth?.let {
                Glide.with(itemView.context)
                    .load(it.url)
                    .override(imageWidth, imageHeight ?: 0)
                    .placeholder(placeHolders[layoutPosition % 5])
                    .into(imageView)
            }
        }
    }

    inner class LoadViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val progressView = itemView.findViewById<ProgressBar>(R.id.progress_bar)
    }
}