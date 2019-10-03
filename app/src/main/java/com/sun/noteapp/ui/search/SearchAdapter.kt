package com.sun.noteapp.ui.search

import android.annotation.SuppressLint
import android.content.Context
import android.text.SpannableString
import android.text.Spanned
import android.text.style.BackgroundColorSpan
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.content.ContextCompat
import com.sun.noteapp.R
import com.sun.noteapp.data.model.Note
import com.sun.noteapp.data.source.local.NoteDatabase
import com.sun.noteapp.ui.base.BaseRecyclerViewAdapter
import com.sun.noteapp.ui.base.BaseViewHolder
import com.sun.noteapp.utils.*
import kotlinx.android.synthetic.main.item_note_vertical_wide.view.*

class SearchAdapter(
    private val maxWidthItem: Int,
    private val listener: OnSearchItemClick
) : BaseRecyclerViewAdapter<Note, SearchAdapter.ViewHolder>() {

    private var querySearch = EMPTY

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder =
        ViewHolder(
            LayoutInflater.from(parent.context).inflate(
                R.layout.item_note_vertical_wide,
                parent,
                false
            ),
            maxWidthItem,
            listener
        )

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.onBindData(getItem(position), querySearch)
    }

    class ViewHolder(
        itemView: View,
        private val maxWidthItem: Int,
        private val listener: OnSearchItemClick
    ) : BaseViewHolder<Note>(itemView) {

        fun onBindData(itemData: Note, querySearch: String) {
            showDataOnView(itemData, querySearch)
        }

        private fun showDataOnView(itemData: Note, querySearch: String) {
            itemView.apply {
                textTitleNoteVerticalWide.setBackgroundResource(
                    ColorPicker.getMediumColor(itemData.color)
                )
                layoutContentNoteVerticalWide.setBackgroundResource(
                    ColorPicker.getLightColor(itemData.color)
                )

                textTitleNoteVerticalWide.text = itemData.title
                val contentNote =
                    if (itemData.type == NoteDatabase.TEXT_NOTE) {
                        itemData.content
                    } else {
                        ConvertString.showActionCheckListByDataString(itemData.content)
                    }
                textContentNoteVerticalWide.text = contentNote

                if (querySearch.isNotEmpty()) {
                    highlightText(textTitleNoteVerticalWide, itemData.title, querySearch, context)
                    highlightText(textContentNoteVerticalWide, contentNote, querySearch, context)
                }

                textRemindTimeNoteVerticalWide.apply {
                    text = itemData.remindTime
                    if (text == NONE) gone() else visible()
                }

                layoutLabelNoteVerticalWide.apply {
                    removeAllViewsInLayout()
                    addLabelTextView(context, itemData.label, layoutLabelNoteVerticalWide)
                    if (itemData.label == NONE) gone() else visible()
                }

                if (itemData.password != NONE) {
                    iconLockNoteVerticalWide.visible()
                    layoutLabelNoteVerticalWide.gone()
                    textContentNoteVerticalWide.text = EMPTY
                } else {
                    iconLockNoteVerticalWide.gone()
                }
            }
        }

        @SuppressLint("SetTextI18n")
        private fun addLabelTextView(
            context: Context,
            labelStringData: String,
            labelLayout: LinearLayout
        ) {
            val labels = ConvertString.labelStringDataToLabelList(labelStringData)
            var layoutLabelWidth = 0
            for (i in labels.indices) {
                val textLabel = TextView(context)
                textLabel.text = labels[i]
                setUpLabelTextView(textLabel)
                layoutLabelWidth += textLabel.measuredWidth + LABEL_PADDING
                if (layoutLabelWidth > maxWidthItem - LAST_LABEL_WIDTH) {
                    if (layoutLabelWidth < maxWidthItem && i == labels.size - 1) {
                        labelLayout.addView(textLabel)
                    } else {
                        textLabel.text = "${labels.size - i}+"
                        labelLayout.addView(textLabel)
                    }
                    break
                }
                labelLayout.addView(textLabel)
            }
        }

        private fun setUpLabelTextView(textLabel: TextView) {
            val params = LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            )
            params.setMargins(5, 0, 5, 0)
            textLabel.apply {
                layoutParams = params
                setBackgroundResource(R.drawable.custom_label)
                measure(
                    ViewGroup.LayoutParams.WRAP_CONTENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT
                )
            }
        }

        private fun highlightText(
            textView: TextView,
            text: String,
            queryText: String,
            context: Context
        ) {
            val listIndex = text.allIndexOf(queryText)
            val spannableString = SpannableString(text)
            listIndex.forEach {
                spannableString.setSpan(
                    BackgroundColorSpan(ContextCompat.getColor(context, R.color.color_highlight)),
                    it,
                    it + queryText.length,
                    Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
                )
            }
            textView.text = spannableString
        }

        override fun onHandleItemClick(mainItem: Note) {
            listener.showNoteDetail(adapterPosition, mainItem)
        }
    }

    fun setQuerySearch(query: String) {
        querySearch = query
        notifyDataSetChanged()
    }

    interface OnSearchItemClick {
        fun showNoteDetail(position: Int, note: Note)
    }
}
