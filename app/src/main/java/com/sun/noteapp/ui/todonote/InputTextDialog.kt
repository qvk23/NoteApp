package com.sun.noteapp.ui.todonote

import android.content.Context
import android.os.Bundle
import android.text.method.PasswordTransformationMethod
import com.sun.noteapp.R
import com.sun.noteapp.ui.base.BaseDialog
import kotlinx.android.synthetic.main.dialog_inputtext.*

class InputTextDialog(
    context: Context,
    private val dialogTitle: String,
    private val buttonSaveTitle: String?,
    private val isPassword: Boolean,
    private val listener: HandleInputTextDialogEvent
) : BaseDialog(context, R.layout.dialog_inputtext) {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        textTitleDialog.text = dialogTitle
        buttonSaveTitle?.let {
            buttonSave.text = it
        }
        if(isPassword)
            edtInput.transformationMethod = PasswordTransformationMethod.getInstance()
    }

    override fun initListener() {
        buttonSave.setOnClickListener {
            val text = edtInput.text.toString()
            listener.getInputString(text)
            dismiss()
        }
        buttonCancel.setOnClickListener {
            dismiss()
        }
    }

    interface HandleInputTextDialogEvent {
        fun getInputString(text: String)
    }
}
