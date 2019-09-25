package com.sun.noteapp.ui.home

import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout.LayoutParams
import com.sun.noteapp.R
import com.sun.noteapp.data.model.Note
import com.sun.noteapp.ui.base.BaseRecyclerViewAdapter
import com.sun.noteapp.ui.base.BaseViewHolder
import com.sun.noteapp.utils.ColorPicker
import kotlinx.android.synthetic.main.item_note_vertical.view.*

class NoteVerticalAdapter(
    private val listener: OnNoteItemClick
) : BaseRecyclerViewAdapter<Note, NoteVerticalAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(
            R.layout.item_note_vertical,
            parent,
            false
        )
        return ViewHolder(view, listener)
    }

    class ViewHolder(
        itemView: View,
        private val listener: OnNoteItemClick
    ) : BaseViewHolder<Note>(itemView) {
        override fun onBindData(itemData: Note) {
            super.onBindData(itemData)
            itemView.apply {
                setBackgroundResource(ColorPicker.getMediumColor(itemData.color))
                val iconType = if (itemData.type == 1) {
                    R.drawable.ic_text_black_24dp
                } else {
                    R.drawable.ic_check_box_black_24dp
                }
                textTitleNoteVertical.apply {
                    text = itemData.title
                    setCompoundDrawablesRelativeWithIntrinsicBounds(iconType, 0, 0, 0)
                    textTitleNoteVertical.compoundDrawablePadding = 10
                }
                layoutIcon.removeAllViewsInLayout()
                if (itemData.password != Note.NONE)
                    addIcon(itemView, R.drawable.ic_lock_black_24dp)
                if (itemData.remindTime != Note.NONE)
                    addIcon(itemView, R.drawable.ic_timer_black_24dp)
            }
        }

        private fun addIcon(view: View, imageResource: Int) {
            val icon = ImageView(view.context)
            icon.setImageResource(imageResource)
            val params = LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT
                , ViewGroup.LayoutParams.WRAP_CONTENT
            )
            params.gravity = Gravity.CENTER_VERTICAL
            params.setMargins(5, 0, 5, 0)
            icon.layoutParams = params
            view.layoutIcon.addView(icon)
        }

        override fun onHandleItemClick(mainItem: Note) {
            listener.showNoteDetail(adapterPosition, mainItem)
        }
    }
}
