package com.sun.noteapp.ui.home.dialog

import android.content.Context
import android.view.View
import com.sun.noteapp.ui.base.BaseDialog
import kotlinx.android.synthetic.main.dialog_sort.*

class SortDialog(
    context: Context,
    layout: Int
) : BaseDialog(context, layout), View.OnClickListener {

    override fun onClick(view: View?) {
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
