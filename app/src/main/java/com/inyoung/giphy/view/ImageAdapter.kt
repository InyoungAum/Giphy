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
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions.withCrossFade
import com.bumptech.glide.request.transition.DrawableCrossFadeFactory
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
        private const val IMAGE_DOWNSCALE_SIZE = 2
        private val dummy = GifImage()
    }

    fun prepareLoadImage() {
        // add loading progress
        images.add(dummy)
        notifyDataSetChanged()
    }

    fun finishLoadImage() {
        images.remove(dummy)
        notifyDataSetChanged()
    }

    fun addImages(images: List<GifImage>, reload: Boolean = false) {
        this.images.apply {
            if (isNotEmpty()) {
                if (reload) {
                    this.clear()
                }
            }
            addAll(images)
        }
        finishLoadImage()
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
        else (viewHolder as ItemViewHolder).bind(position)

    override fun onViewRecycled(holder: RecyclerView.ViewHolder) {
        super.onViewRecycled(holder)
        if (holder is ItemViewHolder) {
            Glide.with(holder.itemView.context).clear(holder.itemView.image_view)
        }
    }

    private fun isLoadingView(position: Int) = images[position].images == null

    inner class ItemViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val imageView = itemView.findViewById<ImageView>(R.id.image_view)
        private val crossFade = DrawableCrossFadeFactory.Builder().setCrossFadeEnabled(true).build()
        private val placeHolders = listOf(
            ColorDrawable(Color.parseColor("#FF5159")),
            ColorDrawable(Color.parseColor("#9F4EFF")),
            ColorDrawable(Color.parseColor("#5BEFFF")),
            ColorDrawable(Color.parseColor("#42FFAA")),
            ColorDrawable(Color.parseColor("#FFE96D"))
        )

        fun bind(position: Int) {
            val image = images[position]
            val imageWidth = displayMetrics.widthPixels / spanCount
            val imageHeight = image.images?.fixedWidth?.let {
                (imageWidth * it.height.toInt()) / it.width.toInt()
            }
            //TODO: null check 대충한거 수정 하자
            imageView.apply {
                layoutParams.width = imageWidth
                layoutParams.height = imageHeight ?: 0

                setOnClickListener { onItemClickListener?.onItemClick(image.id) }


                if (position % 2 == 0) {
                    setPadding(4, 4, 8, 4)
                } else {
                    setPadding(8, 4, 4, 4)
                }
            }

            image.images?.fixedWidth?.let {
                Glide.with(itemView.context)
                    .load(it.url)
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .transition(withCrossFade(crossFade))
                    .override(
                        imageWidth / IMAGE_DOWNSCALE_SIZE,
                        (imageHeight ?: 0) / IMAGE_DOWNSCALE_SIZE)
                    .placeholder(placeHolders[layoutPosition % 5])
                    .into(imageView)
            }
        }
    }

    inner class LoadViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val progressView = itemView.findViewById<ProgressBar>(R.id.progress_bar)
    }
}