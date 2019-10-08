package com.sun.noteapp.utils

import android.app.Activity
import android.content.res.Resources
import android.view.Gravity
import android.view.View
import android.widget.Toast
import java.text.SimpleDateFormat
import java.util.*

fun getScreenWidth() = Resources.getSystem().displayMetrics.widthPixels

fun Activity.showToast(string: CharSequence, length: Int = Toast.LENGTH_SHORT) {
    Toast.makeText(this, string, length).apply {
        setGravity(Gravity.CENTER_HORIZONTAL, 0, 0)
        show()
    }
}

fun View.gone() {
    visibility = View.GONE
}

fun View.visible() {
    visibility = View.VISIBLE
}

fun getListColor(): MutableList<Pair<Int?, String>> {
    val colors = mutableListOf<Pair<Int?, String>>()
    for (index in 0..9) {
        colors.add(ColorPicker.getColorWithName(index))
    }
    return colors
}
fun getMediumColor(): MutableList<Int> {
    val colors = mutableListOf<Int>()
    for (index in 1..9) {
        colors.add(ColorPicker.getMediumColor(index))
    }
    return colors
}

fun getCurrentTime(): String =
    SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(Date())

fun getCurrentDate(): String =
    SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())

fun Date.formatDate(): String = this.let {
    SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault()).format(it)
}
fun String.formatDate(): Date = this.let {
    SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault()).parse(it)
}
fun String.allIndexOf(word: String): List<Int> {
    val results = mutableListOf<Int>()
    var position = 0
    while (position < this.length - word.length) {
        val check = this.indexOf(word, position, true)
        if (check != -1) {
            results.add(check)
            position = check + word.length
        } else
            position++
    }
    return results
}

fun getLabelsFromLabelDataString(labelDataStrings: List<String>): List<String> {
    val results = mutableListOf<String>()
    labelDataStrings.forEach {
        val labels = it.split("$UNDER_STROKE")
        results.addAll(labels)
    }
    results.apply {
        sort()
        distinct()
    }
    return results
}
