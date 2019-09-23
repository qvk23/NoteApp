package com.sun.noteapp.ui.home

import com.sun.noteapp.data.model.Note

interface MainContract {
    interface View {
        fun showAllNotes(notes: List<Note>)
    }

    interface Presenter {
        fun getAllNotes()
    }
}
