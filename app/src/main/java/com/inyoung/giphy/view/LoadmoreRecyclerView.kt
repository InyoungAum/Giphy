package com.inyoung.giphy.view

import android.content.Context
import android.util.AttributeSet
import android.view.View
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager

class LoadmoreRecyclerView(
    context: Context,
    attributeSet: AttributeSet
) : RecyclerView(context, attributeSet) {
    interface OnLoadListener {
        fun onLoad(needRefresh: Boolean)
        fun onFinish(isSuccess: Boolean)
    }

    private var isLoad = false

    private var onLoadListener: OnLoadListener? = null

    private var emptyView: View? = null

    private val adapterDataObserver = object : AdapterDataObserver() {
        override fun onChanged() {
            super.onChanged()
            changeViewVisibility()
        }

        override fun onItemRangeInserted(positionStart: Int, itemCount: Int) {
            super.onItemRangeInserted(positionStart, itemCount)
            changeViewVisibility()
        }

        override fun onItemRangeRemoved(positionStart: Int, itemCount: Int) {
            super.onItemRangeRemoved(positionStart, itemCount)
            changeViewVisibility()
        }
    }

    override fun setAdapter(adapter: Adapter<*>?) {
        super.setAdapter(adapter)
        adapter?.registerAdapterDataObserver(adapterDataObserver)
    }

    fun setOnLoadListener(onLoadListener: OnLoadListener) {
        this.onLoadListener = onLoadListener
    }

    private fun canLoadMore(dy: Int): Boolean {
        val layoutManager = layoutManager as StaggeredGridLayoutManager
        val totalItemCount = layoutManager.itemCount
        val lastVisible = layoutManager.findLastCompletelyVisibleItemPositions(null)

        return lastVisible.contains(totalItemCount - 1) && dy > 0 && !isLoad
    }

    fun load(dy: Int = 0, needRefresh: Boolean = false) {
        post {
            if (canLoadMore(dy) || needRefresh) {
                onLoadListener?.onLoad(needRefresh)
                isLoad = true
            }
        }
    }

    fun loadFinish(isSuccess: Boolean) {
        onLoadListener?.onFinish(isSuccess)
        isLoad = false
    }

    private fun isDataNotEmpty() = adapter?.itemCount != 0

    fun setEmptyView(emptyView: View) {
        this.emptyView = emptyView
    }

    private fun changeViewVisibility() {
        emptyView?.visibility = if(!isDataNotEmpty()) View.VISIBLE else View.GONE
        visibility = if(!isDataNotEmpty()) View.GONE else View.VISIBLE
    }

}
