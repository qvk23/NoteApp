package com.sun.noteapp.ui.home

import com.sun.noteapp.data.model.Note
import com.sun.noteapp.data.repository.NoteLocalRepository
import com.sun.noteapp.data.source.OnDataModifiedCallback
import com.sun.noteapp.utils.getLabelsFromLabelDataString
import java.lang.Exception

class MainPresenter(
    private val view: MainContract.View,
    private val repository: NoteLocalRepository
) : MainContract.Presenter {

    override fun getAllNotesWithOption(color: Int, labels: List<String>, sortType: String) {
        repository.getNotesWithOption(
            color,
            labels,
            sortType,
            object : OnDataModifiedCallback<List<Note>> {
                override fun onSuccess(data: List<Note>) {
                    view.showAllNotes(data)
                }

                override fun onFailed(exception: Exception) {
                    view.showError(exception)
                }
            }
        )
    }

    override fun getAllLabels() {
        repository.getAllLabels(object : OnDataModifiedCallback<List<String>> {
            override fun onSuccess(data: List<String>) {
                val labels = getLabelsFromLabelDataString(data)
                view.gettedLabels(labels)
            }

            override fun onFailed(exception: Exception) {

            }
        })
    }
}
