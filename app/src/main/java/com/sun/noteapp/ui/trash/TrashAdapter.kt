package com.sun.noteapp.ui.trash

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import com.sun.noteapp.R
import com.sun.noteapp.data.model.Note
import com.sun.noteapp.ui.base.BaseRecyclerViewAdapter
import com.sun.noteapp.ui.base.BaseViewHolder
import com.sun.noteapp.utils.ColorPicker
import com.sun.noteapp.utils.WHITE_COLOR
import kotlinx.android.synthetic.main.item_note_trash.view.*

class TrashAdapter(
    private val listener: HandleItemClick
) : BaseRecyclerViewAdapter<Note, TrashAdapter.ViewHolder>() {

    private var selectedItems = mutableListOf<Int>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder =
        ViewHolder(
            LayoutInflater.from(parent.context).inflate(
                R.layout.item_note_trash,
                parent,
                false
            ),
            listener
        )

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.onBind(getItem(position), selectedItems)
    }

    class ViewHolder(
        itemView: View,
        private val listener: HandleItemClick
    ) : BaseViewHolder<Note>(itemView),
        View.OnClickListener,
        View.OnLongClickListener {

        private var color = WHITE_COLOR
        private var selectedItemsTemp = mutableListOf<Int>()
        private var noteTemp: Note? = null

        fun onBind(note: Note, selectedItems: List<Int>) {
            color = note.color
            itemView.apply {
                textTitleItem.text = note.title
                textDeleteTime.text = note.hide
                noteTemp = note
                selectedItemsTemp.clear()
                selectedItemsTemp.addAll(selectedItems)
                cardViewItem.setCardBackgroundColor(
                    ContextCompat.getColor(context, ColorPicker.getMediumColor(note.color))
                )
                if (selectedItems.contains(adapterPosition))
                    setBackgroundColor(true, context)
                else
                    setBackgroundColor(false, context)
                setOnClickListener(this@ViewHolder)
                setOnLongClickListener(this@ViewHolder)
            }
        }

        private fun setBackgroundColor(isSelected: Boolean, context: Context) {
            itemView.apply {
                if (isSelected) {
                    cardViewItem.setCardBackgroundColor(
                        ContextCompat.getColor(context, R.color.color_selected)
                    )
                    layoutItem.setBackgroundResource(R.drawable.custom_border_layout)
                } else {
                    cardViewItem.setCardBackgroundColor(
                        ContextCompat.getColor(context, ColorPicker.getMediumColor(color))
                    )
                    layoutItem.background = null
                }
            }
        }

        private fun checkSelectedItem() {
            if (selectedItemsTemp.contains(adapterPosition)) {
                selectedItemsTemp.remove(adapterPosition)
            } else {
                selectedItemsTemp.add(adapterPosition)
            }
            listener.setUpToolbarSelector(selectedItemsTemp)
        }

        override fun onClick(view: View?) {
            if (selectedItemsTemp.isNotEmpty()) {
                checkSelectedItem()
            } else {
                noteTemp?.let { listener.showNoteDetail(adapterPosition, it) }
            }
        }

        override fun onLongClick(p0: View?): Boolean =
            if (selectedItemsTemp.isEmpty()) {
                selectedItemsTemp.add(adapterPosition)
                listener.setUpToolbarSelector(selectedItemsTemp)
                setBackgroundColor(true, itemView.context)
                true
            } else {
                false
            }
    }

    fun setUpSelection(selections: List<Int>) {
        selectedItems.clear()
        selectedItems.addAll(selections)
        notifyDataSetChanged()
    }

    fun removeAllSelection() {
        selectedItems.clear()
        notifyDataSetChanged()
    }

    fun selectAll() {
        selectedItems.clear()
        for (position in 0 until itemCount)
            selectedItems.add(position)
        notifyDataSetChanged()
    }

    interface HandleItemClick {
        fun showNoteDetail(position: Int, item: Note)
        fun setUpToolbarSelector(selectedItems: List<Int>)
    }
}
