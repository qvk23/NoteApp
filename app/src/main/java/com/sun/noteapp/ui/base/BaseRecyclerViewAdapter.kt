package com.sun.noteapp.ui.base

import android.annotation.SuppressLint
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter

abstract class BaseRecyclerViewAdapter<T, V : BaseViewHolder<T>> :
    ListAdapter<T, V>(DiffUtilCallback<T>()) {

    override fun onBindViewHolder(holder: V, position: Int) {
        getItem(position)?.let { holder.onBindData(it) }
    }

    fun updateData(data: List<T>) = submitList(data)

    class DiffUtilCallback<T> : DiffUtil.ItemCallback<T>() {
        override fun areItemsTheSame(oldItem: T, newItem: T) = oldItem === newItem

        @SuppressLint("DiffUtilEquals")
        override fun areContentsTheSame(oldItem: T, newItem: T) = oldItem === newItem
    }
}
