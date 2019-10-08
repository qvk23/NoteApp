package com.sun.noteapp.ui.textnote

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.sun.noteapp.R
import com.sun.noteapp.ui.base.BaseRecyclerViewAdapter
import com.sun.noteapp.ui.base.BaseViewHolder
import kotlinx.android.synthetic.main.label_item.view.*


class LabelAdapter : BaseRecyclerViewAdapter<String, LabelAdapter.ViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.label_item, parent, false)
        return ViewHolder(view)
    }

    class ViewHolder(itemView: View) : BaseViewHolder<String>(itemView) {
        override fun onBindData(itemData: String) {
            super.onBindData(itemData)
            itemView.textLabel.apply {
                setBackgroundResource(R.drawable.custom_label)
                text = itemData
            }
        }
    }
}
