package com.sun.noteapp.ui.home.dialog

import android.content.Context
import com.sun.noteapp.R
import com.sun.noteapp.ui.base.BaseDialog
import com.sun.noteapp.utils.TEXT_NOTE
import com.sun.noteapp.utils.TODO_NOTE
import kotlinx.android.synthetic.main.dialog_create_note.*

class CreateNoteDialog(
    context: Context,
    private val onCreateNoteCallback: BaseDialog.OnLoadDialogCallback<Int>
) : BaseDialog(context, R.layout.dialog_create_note) {

    override fun initListener() {
        buttonCreateTextNote.setOnClickListener {
            onCreateNoteCallback.onSuccess(TEXT_NOTE)
            dismiss()
        }
        buttonCreateToDoNote.setOnClickListener {
            onCreateNoteCallback.onSuccess(TODO_NOTE)
            dismiss()
        }
    }
}
