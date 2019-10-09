package com.sun.noteapp.ui.trash

import com.sun.noteapp.data.model.Note

interface TrashContract {
    interface View {
        fun showNotes(notes: List<Note>)

        fun showMessage(stringId: Int)
    }

    interface Presenter {
        fun getAllHideNote(sortType: String)

        fun deleteNotes(selectedItemPositions: List<Int>)

        fun restoreNotes(selectedItemPositions: List<Int>)
    }
}
