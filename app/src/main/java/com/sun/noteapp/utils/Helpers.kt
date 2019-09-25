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

