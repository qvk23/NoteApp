package com.sun.noteapp.ui.textnote

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Rect
import android.util.AttributeSet
import androidx.appcompat.widget.AppCompatEditText

class LineEditText(context: Context, attr: AttributeSet) : AppCompatEditText(context, attr) {
    private val rect = Rect()
    private val paint = Paint().apply {
        style = Paint.Style.FILL_AND_STROKE
        color = Color.BLACK
    }

    override fun onDraw(canvas: Canvas?) {
        var count = height / lineHeight
        if (lineCount > count) count = lineCount
        var baseLine = getLineBounds(0, rect)
        for (i in 0 until count) {
            canvas?.drawLine(
                rect.left.toFloat(),
                (baseLine + 1).toFloat(),
                rect.right.toFloat(),
                (baseLine + 1).toFloat(),
                paint
            )
            baseLine += lineHeight
        }
        super.onDraw(canvas)
    }
}
