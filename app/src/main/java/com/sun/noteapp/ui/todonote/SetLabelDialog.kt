package com.sun.noteapp.ui.todonote

import android.content.Context
import android.os.Bundle
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.sun.noteapp.R
import com.sun.noteapp.ui.base.BaseDialog
import com.sun.noteapp.utils.gone
import kotlinx.android.synthetic.main.dialog_set_label.*

class SetLabelDialog(
    context: Context,
    private val labels: List<String>,
    private val selectedLabels: List<String>,
    private val isOnlyPickLabel: Boolean,
    private val buttonSaveTitle: String?,
    private val listener: HandleAddLabelDialogEvent
) : BaseDialog(context, R.layout.dialog_set_label){

    private val selectedLabelsNew = selectedLabels.toMutableList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initView()
    }

    private fun initView(){
        if(isOnlyPickLabel)
            textAddNewLabel.gone()
        val labelAdapter = LabelAdapterVertical(selectedLabels, object : LabelAdapterVertical.HandleLabelItemClick{
            override fun onLabelItemClick(position: Int, label: String) {
                if(selectedLabelsNew.contains(label))
                    selectedLabelsNew.remove(label)
                else
                    selectedLabelsNew.add(label)
            }
        })
        val deviderItemDecoration = DividerItemDecoration(context, LinearLayoutManager.VERTICAL)
        recyclerLabel.apply {
            adapter = labelAdapter
            addItemDecoration(deviderItemDecoration)
        }
        labelAdapter.submitList(labels)
        buttonSaveTitle?.let{
            textSaveLabelDialog.text = it
        }
    }

    override fun initListener() {
        textAddNewLabel.setOnClickListener{
            listener.addLabel()
            dismiss()
        }
        textSaveLabelDialog.setOnClickListener{
            listener.getSelectedLabels(selectedLabelsNew)
            dismiss()
        }
        textCancelLabelDialog.setOnClickListener{
            dismiss()
        }
    }

    interface HandleAddLabelDialogEvent{
        fun getSelectedLabels(selectedLabels: List<String>)
        fun addLabel()
    }
}
