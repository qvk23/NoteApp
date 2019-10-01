package com.sun.noteapp.ui.search

import com.sun.noteapp.data.model.Note
import com.sun.noteapp.data.repository.NoteLocalRepository
import com.sun.noteapp.data.source.OnDataModifiedCallback
import java.lang.Exception

class SearchPresenter(
    private val view: SearchContract.View,
    private val repository: NoteLocalRepository
) : SearchContract.Presenter {

    private val notes = mutableListOf<Note>()

    override fun getAllNotes() {
        repository.getAllNotes(object : OnDataModifiedCallback<List<Note>> {
            override fun onSuccess(data: List<Note>) {
                notes.addAll(data)
            }

            override fun onFailed(exception: Exception) {
                TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
            }
        })
    }

    override fun searchNote(word: String) {
        var searchNotes = listOf<Note>()
        if (word.isNotEmpty())
            searchNotes = notes.filter { note ->
                note.title.contains(word, true) || note.content.contains(word, true)
            }
        view.showNotes(searchNotes)
    }
}
