package com.sun.noteapp.ui.trash

import android.content.Context
import android.graphics.*
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.Drawable
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import com.sun.noteapp.R

abstract class SwipeItemCallback(val context: Context) : ItemTouchHelper.Callback() {

    private var colorDrawableDelete = ColorDrawable(Color.RED)
    private var colorDrawableRestore = ColorDrawable(Color.GREEN)
    private var clearPaint = Paint()
    private var deleteIcon: Drawable?
    private var restoreIcon: Drawable?
    private var deleteIconHeight = 0
    private var deleteIconWidth = 0
    private var restoreIconHeight = 0
    private var restoreIconWidth = 0
    private var textView: TextView

    init {
        clearPaint.xfermode = PorterDuffXfermode(PorterDuff.Mode.CLEAR)
        deleteIcon = ContextCompat.getDrawable(context, R.drawable.ic_delete_black_24dp)
        deleteIcon?.let {
            deleteIconHeight = it.intrinsicHeight
            deleteIconWidth = it.intrinsicWidth
        }
        restoreIcon = ContextCompat.getDrawable(context, R.drawable.ic_restore_black_24dp)
        restoreIcon?.let {
            restoreIconHeight = it.intrinsicHeight
            restoreIconWidth = it.intrinsicWidth
        }
        textView = TextView(context)
    }

    override fun getMovementFlags(
        recyclerView: RecyclerView,
        viewHolder: RecyclerView.ViewHolder
    ): Int {
        return makeMovementFlags(0, ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT)
    }

    override fun onMove(
        recyclerView: RecyclerView,
        viewHolder: RecyclerView.ViewHolder,
        target: RecyclerView.ViewHolder
    ): Boolean = false

    override fun onChildDraw(
        canvas: Canvas,
        recyclerView: RecyclerView,
        viewHolder: RecyclerView.ViewHolder,
        dX: Float,
        dY: Float,
        actionState: Int,
        isCurrentlyActive: Boolean
    ) {
        val itemView = viewHolder.itemView
        val isCancelled = dX == 0f && !isCurrentlyActive
        if (isCancelled) {
            clearCanvas(
                canvas,
                itemView.right + dX,
                itemView.top.toFloat(),
                itemView.right.toFloat(),
                itemView.bottom.toFloat()
            )
            super.onChildDraw(
                canvas,
                recyclerView,
                viewHolder,
                dX,
                dY,
                actionState,
                isCurrentlyActive
            )
            return
        }
        if (dX > 0) {
            colorDrawableRestore.setBounds(itemView.left, itemView.top, dX.toInt(), itemView.bottom)
            colorDrawableRestore.draw(canvas)
            restoreIcon?.let {
                val iconHeight = it.intrinsicHeight
                val iconWidth = it.intrinsicWidth
                val iconMarginVertical = (itemView.height - iconHeight) / 2
                it.setBounds(
                    itemView.left + iconMarginVertical,
                    itemView.top + iconMarginVertical,
                    itemView.left + iconMarginVertical + iconWidth,
                    itemView.bottom - iconMarginVertical
                )
                it.draw(canvas)
            }
        } else if (dX < 0) {
            colorDrawableDelete.setBounds(
                itemView.right + dX.toInt(),
                itemView.top,
                itemView.right,
                itemView.bottom
            )
            colorDrawableDelete.draw(canvas)
            deleteIcon?.let {
                val iconHeight = it.intrinsicHeight
                val iconWidth = it.intrinsicWidth
                val iconMarginVertical = (itemView.height - iconHeight) / 2
                it.setBounds(
                    itemView.right - iconMarginVertical - iconWidth,
                    itemView.top + iconMarginVertical,
                    itemView.right - iconMarginVertical,
                    itemView.bottom - iconMarginVertical
                )
                it.draw(canvas)
            }
        }
        super.onChildDraw(canvas, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive)
    }

    private fun clearCanvas(canvas: Canvas, left: Float, top: Float, right: Float, bottom: Float) {
        canvas.drawRect(left, top, right, bottom, clearPaint)
    }
}
