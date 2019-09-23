package com.sun.noteapp.ui.home.dialog

import android.content.Context
import com.sun.noteapp.ui.base.BaseDialog
import com.sun.noteapp.ui.home.MainActivity.Companion.DETAIL
import com.sun.noteapp.ui.home.MainActivity.Companion.GRID
import com.sun.noteapp.ui.home.MainActivity.Companion.LIST
import kotlinx.android.synthetic.main.dialog_view.*

class ViewDialog(
    context: Context,
    layout: Int,
    private val onUpdateViewTypeDialogCallback: OnLoadDialogCallback<Int>
) : BaseDialog(context, layout) {

    override fun initListener() {
        buttonSetViewList.setOnClickListener {
            onUpdateViewTypeDialogCallback.onSuccess(LIST)
            dismiss()
        }
        buttonSetViewDetail.setOnClickListener {
            onUpdateViewTypeDialogCallback.onSuccess(DETAIL)
            dismiss()
        }
        buttonSetViewGrid.setOnClickListener {
            onUpdateViewTypeDialogCallback.onSuccess(GRID)
            dismiss()
        }
    }
}
