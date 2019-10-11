package com.sun.noteapp.ui.todonote

import android.graphics.Color
import android.text.SpannableString
import android.text.Spanned
import android.text.style.StrikethroughSpan
import android.view.*
import android.widget.PopupMenu
import com.sun.noteapp.R
import com.sun.noteapp.data.model.ToDo
import com.sun.noteapp.ui.base.BaseRecyclerViewAdapter
import com.sun.noteapp.ui.base.BaseViewHolder
import com.sun.noteapp.utils.EMPTY
import kotlinx.android.synthetic.main.item_todo.view.*

class ToDoAdapter(
    private val listener: HandleToDoItemEvent
) : BaseRecyclerViewAdapter<ToDo, ToDoAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder =
        ViewHolder(
            LayoutInflater.from(parent.context).inflate(
                R.layout.item_todo,
                parent,
                false
            ),
            listener
        )

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.onBind(getItem(position))
    }

    class ViewHolder(
        itemView: View,
        private val listener: HandleToDoItemEvent
    ) : BaseViewHolder<ToDo>(itemView) {
        private var isItemSelected = false
        private var toDoName = EMPTY

        init {
            itemView.apply {
                setOnClickListener {
                    isItemSelected = !isItemSelected
                    checkItemSelected()
                    listener.onItemToDoClick(adapterPosition)
                }
                iconMoveToDo.setOnTouchListener { _, motionEvent ->
                    if (motionEvent.actionMasked == MotionEvent.ACTION_DOWN || motionEvent.actionMasked == MotionEvent.ACTION_UP)
                        listener.onDragIconMove(this@ViewHolder)
                    false
                }
                iconSettingToDo.setOnClickListener {
                    showPopupMenuToDo()
                }
            }
        }

        fun onBind(itemData: ToDo) {
            itemView.apply {
                toDoName = itemData.name
                isItemSelected = itemData.isComplete
                checkItemSelected()
            }
        }

        private fun checkItemSelected() {
            itemView.apply {
                if (!isItemSelected) {
                    textTitleToDo.setTextColor(Color.BLACK)
                    imageCheckBoxToDo.setImageResource(R.drawable.ic_check_box_outline_blank_black_30dp)
                    iconMoveToDo.setImageResource(R.drawable.ic_swap_vert_black_30dp)
                    iconSettingToDo.setImageResource(R.drawable.ic_more_vert_black_30dp)
                    textTitleToDo.text = toDoName
                } else {
                    textTitleToDo.setTextColor(Color.GRAY)
                    imageCheckBoxToDo.setImageResource(R.drawable.ic_check_box_gray_30dp)
                    iconMoveToDo.setImageResource(R.drawable.ic_swap_vert_gray_30dp)
                    iconSettingToDo.setImageResource(R.drawable.ic_more_vert_gray_30dp)
                    val spannableString = SpannableString(toDoName)
                    spannableString.setSpan(
                        StrikethroughSpan(),
                        0,
                        toDoName.length,
                        Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
                    )
                    textTitleToDo.text = spannableString
                }
            }
        }

        private fun showPopupMenuToDo() {
            val popupMenu = PopupMenu(itemView.context, itemView.iconSettingToDo)
            popupMenu.menuInflater.inflate(R.menu.popup_menu_todo, popupMenu.menu)
            popupMenu.setOnMenuItemClickListener { item ->
                when (item?.itemId) {
                    R.id.option_item_delete -> listener.deleteToDoItem(adapterPosition)
                    R.id.option_item_rename -> listener.changeToDoName(adapterPosition)
                }
                false
            }
            popupMenu.show()
        }
    }

    interface HandleToDoItemEvent {
        fun onItemToDoClick(position: Int)
        fun changeToDoName(position: Int)
        fun deleteToDoItem(position: Int)
        fun onDragIconMove(holder: ViewHolder)
    }
}
