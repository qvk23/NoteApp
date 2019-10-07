package com.sun.noteapp.ui.textnote

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.sun.noteapp.R
import com.sun.noteapp.ui.base.BaseRecyclerViewAdapter
import com.sun.noteapp.ui.base.BaseViewHolder
import kotlinx.android.synthetic.main.color_image_item.view.*

class ImageColorAdapter(
    val onItemClicked: (position: Int) -> Unit
) : BaseRecyclerViewAdapter<Int, ImageColorAdapter.ViewHolder>() {

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(
            R.layout.color_image_item,
            parent,
            false
        )
        return ViewHolder(view, onItemClicked)
    }

    class ViewHolder(
        itemView: View,
        val onItemClicked: (position: Int) -> Unit
    ) : BaseViewHolder<Int>(itemView) {

        override fun onBindData(itemData: Int) {
            super.onBindData(itemData)
            itemView.imageViewItemImageColor.setImageResource(itemData)
        }

        override fun onHandleItemClick(mainItem: Int) {
            super.onHandleItemClick(mainItem)
            onItemClicked(adapterPosition + 1)
        }
    }

}
