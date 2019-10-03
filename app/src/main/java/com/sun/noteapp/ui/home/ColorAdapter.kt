package com.sun.noteapp.ui.home

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import com.sun.noteapp.R
import com.sun.noteapp.ui.base.BaseRecyclerViewAdapter
import com.sun.noteapp.ui.base.BaseViewHolder
import kotlinx.android.synthetic.main.color_item.view.*

class ColorAdapter(
    private val listener: OnItemColorClick
) : BaseRecyclerViewAdapter<Pair<Int?, String>, ColorAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(
            R.layout.color_item,
            parent,
            false
        )
        return ViewHolder(view, parent.context, listener)
    }

    override fun onViewDetachedFromWindow(holder: ViewHolder) {
        super.onViewDetachedFromWindow(holder)
        holder.itemView.clearAnimation()
    }
    class ViewHolder(
        itemView: View,
        private val context: Context,
        private val listener: OnItemColorClick
    ) : BaseViewHolder<Pair<Int?, String>>(itemView) {
        private var lastPosition = -1
        override fun onBindData(itemData: Pair<Int?, String>) {
            super.onBindData(itemData)
            itemData.first?.let { itemView.imageViewColor.setImageResource(it) }
            itemView.textViewColor.text = itemData.second
            if(adapterPosition > lastPosition){
                setAppearAnimation(itemView)
                lastPosition = adapterPosition
            }
        }

        override fun onHandleItemClick(mainItem: Pair<Int?, String>) {
            super.onHandleItemClick(mainItem)
            listener.onClick(adapterPosition)
        }

        private fun setAppearAnimation(itemView: View) {
            val anim = AnimationUtils.loadAnimation(context, R.anim.anim_slide_left_to_right)
            itemView.startAnimation(anim)
        }
    }
    interface OnItemColorClick {
        fun onClick(position: Int)
    }
}
