package com.sun.noteapp.ui.base

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.view.Gravity
import android.view.Window
import androidx.appcompat.app.ActionBar

abstract class BaseDialog(
    context: Context,
    layout: Int
) : Dialog(context) {

    init {
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        setContentView(layout)
        setCanceledOnTouchOutside(true)
        window?.run {
            setGravity(Gravity.CENTER)
            setLayout(ActionBar.LayoutParams.MATCH_PARENT, ActionBar.LayoutParams.WRAP_CONTENT)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        initListener()
    }

    abstract fun initListener()
    interface OnLoadDialogCallback<T> {
        fun onSuccess(parrams: T)
    }
}
