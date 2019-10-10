package com.sun.noteapp.data.model

data class NoteOption(
    var color: Int,
    val labels: List<String>,
    var sortType: String,
    var isOnlyRemind: Boolean
)
