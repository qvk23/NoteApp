package com.sun.noteapp.ui.home

import com.sun.noteapp.data.model.Note
import java.lang.Exception

interface MainContract {
    interface View {
        fun showAllNotes(notes: List<Note>)
        fun showError(exception: Exception)
        fun gettedLabels(labels: List<String>)
    }

    interface Presenter {
        fun getAllNotesWithOption(color: Int, labels: List<String>, sortType: String)
        fun getAllLabels()
    }
}
