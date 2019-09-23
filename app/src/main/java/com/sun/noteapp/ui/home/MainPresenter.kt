package com.sun.noteapp.ui.home

import com.sun.noteapp.data.model.Note
import com.sun.noteapp.data.repository.NoteLocalRepository
import com.sun.noteapp.data.source.OnDataModifiedCallback
import java.lang.Exception

class MainPresenter(
    private val view: MainContract.View,
    private val repository: NoteLocalRepository
) : MainContract.Presenter {

    override fun getAllNotes() {
        repository.getAllNotes(
            object : OnDataModifiedCallback<List<Note>> {
                override fun onSuccess(data: List<Note>) {
                    view.showAllNotes(data)
                }

                override fun onFailed(exception: Exception) {

                }
            }
        )
    }
}
