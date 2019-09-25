package com.sun.noteapp.ui.home

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import com.sun.noteapp.R
import com.sun.noteapp.data.model.Note
import com.sun.noteapp.ui.base.BaseRecyclerViewAdapter
import com.sun.noteapp.ui.base.BaseViewHolder
import com.sun.noteapp.utils.ColorPicker
import com.sun.noteapp.utils.ConvertString
import com.sun.noteapp.utils.gone
import com.sun.noteapp.utils.visible
import kotlinx.android.synthetic.main.item_note_vertical_wide.view.*

const val PADDING_WIDTH = 10
const val LAST_LABEL_WIDTH = 180
const val EMPTY = ""

class NoteVerticalWideAdapter(
    private val listener: OnNoteItemClick,
    private val maxWidthItem: Int
) : BaseRecyclerViewAdapter<Note, NoteVerticalWideAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(
            R.layout.item_note_vertical_wide,
            parent,
            false
        )
        return ViewHolder(view, maxWidthItem, listener)
    }

    class ViewHolder(
        itemView: View,
        private val maxWidthItem: Int,
        private val listener: OnNoteItemClick
    ) : BaseViewHolder<Note>(itemView) {

        override fun onBindData(itemData: Note) {
            super.onBindData(itemData)
            itemView.apply {
                textTitleNoteVerticalWide.setBackgroundResource(
                    ColorPicker.getMediumColor(itemData.color)
                )
                layoutContentNoteVerticalWide.setBackgroundResource(
                    ColorPicker.getLightColor(itemData.color)
                )
                textTitleNoteVerticalWide.text = itemData.title
                val content = if (itemData.type == 1) itemData.content
                else ConvertString.showActionCheckListByDataString(itemData.content)
                textContentNoteVerticalWide.text = content

                textRemindTimeNoteVerticalWide.apply {
                    text = itemData.remindTime
                    if (itemData.remindTime == Note.NONE) gone() else visible()
                }

                layoutLabelNoteVerticalWide.apply {
                    removeAllViewsInLayout()
                    addLabelView(itemData.label, itemView)
                    if (itemData.label == Note.NONE) gone() else visible()
                }

                if (itemData.password != Note.NONE) {
                    iconLockNoteVerticalWide.visible()
                    layoutLabelNoteVerticalWide.gone()
                    textContentNoteVerticalWide.text = EMPTY
                } else {
                    iconLockNoteVerticalWide.gone()
                }
            }
        }

        @SuppressLint("SetTextI18n")
        private fun addLabelView(label: String, view: View) {
            view.apply {
                val labels = ConvertString.labelStringDataToLabelList(label)
                var sum = 0
                for (i in labels.indices) {
                    val tv = TextView(context)
                    val params = LinearLayout.LayoutParams(
                        ViewGroup.LayoutParams.WRAP_CONTENT
                        , ViewGroup.LayoutParams.WRAP_CONTENT
                    )
                    params.setMargins(5, 0, 5, 0)
                    tv.apply {
                        layoutParams = params
                        text = labels[i]
                        setBackgroundResource(R.drawable.custom_label)
                        measure(
                            ViewGroup.LayoutParams.WRAP_CONTENT,
                            ViewGroup.LayoutParams.WRAP_CONTENT
                        )
                    }

                    sum += tv.measuredWidth + PADDING_WIDTH
                    if (sum > maxWidthItem - LAST_LABEL_WIDTH) {
                        if (sum < maxWidthItem && i == labels.size - 1) {
                            layoutLabelNoteVerticalWide.addView(tv)
                        } else {
                            tv.text = "${labels.size - i}+"
                            layoutLabelNoteVerticalWide.addView(tv)
                        }
                        break
                    }
                    layoutLabelNoteVerticalWide.addView(tv)
                }
            }
        }

        override fun onHandleItemClick(mainItem: Note) {
            listener.showNoteDetail(adapterPosition, mainItem)
        }
    }
}
