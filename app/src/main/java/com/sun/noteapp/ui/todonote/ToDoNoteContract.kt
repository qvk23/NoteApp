package com.sun.noteapp.ui.todonote

import com.sun.noteapp.data.model.Note

interface ToDoNoteContract {
    interface View {
        fun backToListNote()
    }

    interface Presenter {
        fun addNote(note: Note)
        fun editNote(id: Int, note: Note)
    }
}
