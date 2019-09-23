package com.sun.noteapp.utils

import com.sun.noteapp.R

class ColorPicker {

    fun getMediumColor(number: Int): Int =
        when (number) {
            1 -> R.color.color_medium_red
            2 -> R.color.color_medium_orange
            3 -> R.color.color_medium_yellow
            4 -> R.color.color_medium_green
            5 -> R.color.color_medium_blue
            6 -> R.color.color_medium_violet
            7 -> R.color.color_medium_black
            8 -> R.color.color_medium_slate_gray
            else -> R.color.color_white
        }

    fun getLightColor(number: Int): Int =
        when (number) {
            1 -> R.color.color_light_red
            2 -> R.color.color_light_orange
            3 -> R.color.color_light_yellow
            4 -> R.color.color_light_green
            5 -> R.color.color_light_blue
            6 -> R.color.color_light_violet
            7 -> R.color.color_light_black
            8 -> R.color.color_light_slate_gray
            else -> R.color.color_white
        }

    fun getColor(number: Int) {
        when (number) {
            1 -> R.color.color_red
            2 -> R.color.color_orange
            3 -> R.color.color_yellow
            4 -> R.color.color_green
            5 -> R.color.color_blue
            6 -> R.color.color_violet
            7 -> R.color.color_black
            8 -> R.color.color_slate_gray
            else -> R.color.color_white
        }
    }

    fun getColorWithName(number: Int): Pair<Int, String> =
        when (number) {
            1 -> Pair(R.color.color_red, "Red")
            2 -> Pair(R.color.color_orange, "Orange")
            3 -> Pair(R.color.color_yellow, "Yellow")
            4 -> Pair(R.color.color_green, "Green")
            5 -> Pair(R.color.color_blue, "Blue")
            6 -> Pair(R.color.color_violet, "Violet")
            7 -> Pair(R.color.color_black, "Black")
            8 -> Pair(R.color.color_gray, "Gray")
            else -> Pair(R.color.color_white, "White")
        }
}
