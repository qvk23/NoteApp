package com.sun.noteapp.ui.home

import com.sun.noteapp.data.model.Note

interface OnNoteItemClick {
    fun showNoteDetail(position: Int, note: Note)
}
