package com.sun.noteapp.ui.home.dialog

import android.content.Context
import android.view.View
import com.sun.noteapp.R
import com.sun.noteapp.data.source.local.NoteDatabase
import com.sun.noteapp.ui.base.BaseDialog
import kotlinx.android.synthetic.main.dialog_sort.*

class SortDialog(
    context: Context,
    layout: Int,
    private val sortListener: OnLoadDialogCallback<String>
) : BaseDialog(context, layout), View.OnClickListener {

    override fun onClick(view: View?) {
        val sortType = when(view?.id){
            R.id.buttonSortByUpdateTime -> NoteDatabase.ORDERBY_MODIFYTIME
            R.id.buttonSortByCreatedTime -> NoteDatabase.ORDERBY_CREATETIME
            R.id.buttonSortByAlphabet -> NoteDatabase.ORDERBY_ALPHABETA
            R.id.buttonSortByColor -> NoteDatabase.ORDERBY_COLOR
            R.id.buttonSortByReminderTime -> NoteDatabase.ORDERBY_REMINDTIME
            else -> NoteDatabase.ORDERBY_CREATETIME
        }
        sortListener.onSuccess(sortType)
        dismiss()
    }

    override fun initListener() {
        buttonSortByUpdateTime.setOnClickListener(this)
        buttonSortByCreatedTime.setOnClickListener(this)
        buttonSortByAlphabet.setOnClickListener(this)
        buttonSortByColor.setOnClickListener(this)
        buttonSortByReminderTime.setOnClickListener(this)
    }
}
