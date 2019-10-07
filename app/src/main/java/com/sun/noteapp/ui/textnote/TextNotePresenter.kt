package com.sun.noteapp.ui.textnote

import android.content.ContentValues
import com.sun.noteapp.data.repository.NoteLocalRepository
import com.sun.noteapp.data.source.OnDataModifiedCallback
import java.lang.Exception

class TextNotePresenter(
    private val view: TextNoteContract.View,
    private val repository: NoteLocalRepository
) : TextNoteContract.Presenter {

    override fun editNote(id: Int, values: ContentValues) {
        repository.editNote(id, values, object : OnDataModifiedCallback<Boolean>{
            override fun onSuccess(data: Boolean) {
                view.backToListNote()
            }

            override fun onFailed(exception: Exception) {
                TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
            }

        })
    }

    override fun addNote(values: ContentValues) {
        repository.addNote(values, object : OnDataModifiedCallback<Boolean>{
            override fun onSuccess(data: Boolean) {
                view.backToListNote()
            }

            override fun onFailed(exception: Exception) {
                TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
            }

        })
    }
}
