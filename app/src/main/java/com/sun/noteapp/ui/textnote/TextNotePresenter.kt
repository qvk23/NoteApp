package com.sun.noteapp.ui.textnote

import com.sun.noteapp.data.model.Note
import com.sun.noteapp.data.repository.NoteLocalRepository
import com.sun.noteapp.data.source.OnDataModifiedCallback
import java.lang.Exception

class TextNotePresenter(
    private val view: TextNoteContract.View,
    private val repository: NoteLocalRepository
) : TextNoteContract.Presenter {

    override fun editNote(id: Int, note: Note) {
        repository.editNote(id, note, object : OnDataModifiedCallback<Boolean> {
            override fun onSuccess(data: Boolean) {
                view.backToListNote()
            }

            override fun onFailed(exception: Exception) {
                TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
            }

        })
    }

    override fun addNote(note: Note) {
        repository.addNote(note, object : OnDataModifiedCallback<Boolean> {
            override fun onSuccess(data: Boolean) {
                view.backToListNote()
            }

            override fun onFailed(exception: Exception) {
                TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
            }

        })
    }

    override fun getNoteById(id: Int) {
        repository.getNoteById(id, object : OnDataModifiedCallback<Note> {
            override fun onSuccess(data: Note) {
                view.initData(data)
            }

            override fun onFailed(exception: Exception) {
                TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
            }
        })
    }
}
