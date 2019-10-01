package com.sun.noteapp.utils

import android.app.Activity
import android.content.res.Resources
import android.view.View
import android.widget.Toast

fun getScreenWidth() = Resources.getSystem().displayMetrics.widthPixels

fun Activity.showToast(string: CharSequence, length: Int = Toast.LENGTH_SHORT) {
    Toast.makeText(this, string, length).show()
}

fun View.gone() {
    visibility = View.GONE
}

fun View.visible() {
    visibility = View.VISIBLE
}
fun getListColor(): MutableList<Pair<Int?, String>> {
    val colors = mutableListOf<Pair<Int?, String>>()
    for (i in 0..9) {
        colors.add(ColorPicker.getColorWithName(i))
    }
    return colors
}

