package com.sun.noteapp.ui.home.dialog

import android.content.Context
import androidx.recyclerview.widget.LinearLayoutManager
import com.sun.noteapp.ui.base.BaseDialog
import com.sun.noteapp.ui.home.ColorAdapter
import kotlinx.android.synthetic.main.dialog_label.*

class LabelDialog(
    context: Context,
    layout: Int
) : BaseDialog(context, layout) {

    override fun initListener() {
        val adapter = ColorAdapter(object : ColorAdapter.OnItemColorClick {
            override fun onClick(position: Int) {
                dismiss()
            }
        })
        recyclerViewListLabel.apply {
            layoutManager = LinearLayoutManager(context)
            setHasFixedSize(true)
            setAdapter(adapter)
        }

    }
}
