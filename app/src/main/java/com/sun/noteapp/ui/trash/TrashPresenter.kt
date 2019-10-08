package com.sun.noteapp.ui.trash

import com.sun.noteapp.R
import com.sun.noteapp.data.model.Note
import com.sun.noteapp.data.repository.NoteLocalRepository
import com.sun.noteapp.data.source.OnDataModifiedCallback
import com.sun.noteapp.data.source.local.NoteDatabase
import java.lang.Exception

class TrashPresenter(
    private val view: TrashContract.View,
    private val repository: NoteLocalRepository
) : TrashContract.Presenter {

    private val hideNotes = mutableListOf<Note>()
    private var sortType = NoteDatabase.ORDERBY_CREATETIME

    override fun getAllHideNote(sortType: String) {
        this.sortType = sortType
        repository.getAllHidedNotes(sortType, object : OnDataModifiedCallback<List<Note>> {
            override fun onSuccess(data: List<Note>) {
                view.showNotes(data)
                hideNotes.clear()
                hideNotes.addAll(data)
            }

            override fun onFailed(exception: Exception) {
            }
        })
    }

    override fun deleteNotes(selectedItemPositions: List<Int>) {
        val noteIds = mutableListOf<Int>()
        selectedItemPositions.forEach {
            noteIds.add(hideNotes[it].id)
        }
        repository.deleteNotes(
            noteIds,
            object : OnDataModifiedCallback<List<Boolean>> {
                override fun onSuccess(data: List<Boolean>) {
                    getAllHideNote(sortType)
                    view.showMessage(R.string.message_delete_successful)
                }

                override fun onFailed(exception: Exception) {
                    view.showMessage(R.string.message_delete_failure)
                }
            }
        )
    }

    override fun restoreNotes(selectedItemPositions: List<Int>) {
        val noteIds = mutableListOf<Int>()
        selectedItemPositions.forEach {
            noteIds.add(hideNotes[it].id)
        }
        repository.restoreNotes(
            noteIds,
            object : OnDataModifiedCallback<List<Boolean>> {
                override fun onSuccess(data: List<Boolean>) {
                    getAllHideNote(sortType)
                    view.showMessage(R.string.message_restore_successful)
                }

                override fun onFailed(exception: Exception) {
                    view.showMessage(R.string.message_restore_failure)
                }
            }
        )
    }
}
