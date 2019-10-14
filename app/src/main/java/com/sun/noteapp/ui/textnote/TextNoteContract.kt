package com.sun.noteapp.ui.textnote

import com.sun.noteapp.data.model.Note

interface TextNoteContract {
    interface View {
        fun backToListNote()
        fun initData(note: Note)
        fun gotLabels(labels: List<String>)
    }

    interface Presenter {
        fun addNote(note: Note)
        fun editNote(id: Int, note: Note)
        fun getNoteById(id: Int)
        fun getAllLabels()
    }
}
