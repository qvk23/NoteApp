package com.sun.noteapp.ui.home

import com.sun.noteapp.data.model.Note
import com.sun.noteapp.data.model.NoteOption
import java.lang.Exception

interface MainContract {
    interface View {
        fun showAllNotes(notes: List<Note>)
        fun showError(exception: Exception)
        fun gettedLabels(labels: List<String>)
        fun noteCount(count: Int)
    }

    interface Presenter {
        fun getAllNotesWithOption(option: NoteOption)
        fun getAllLabels()
        fun getAllNote()
        fun getNoteCount()
    }
}
