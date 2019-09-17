package com.sun.noteapp.data.model

data class Note(
    val id: Int,
    val title: String,
    val content: String,
    val type: Int,
    val color: Int,
    val label: String?,
    val date: String?,
    val password: String?
)
