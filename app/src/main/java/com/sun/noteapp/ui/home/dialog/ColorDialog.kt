package com.sun.noteapp.ui.home.dialog

import android.content.Context
import androidx.recyclerview.widget.GridLayoutManager
import com.sun.noteapp.ui.base.BaseDialog
import com.sun.noteapp.ui.home.ColorAdapter
import com.sun.noteapp.ui.home.MainActivity.Companion.colors
import kotlinx.android.synthetic.main.dialog_color.*

private const val COLUMN_NUMBER = 2

class ColorDialog(
    context: Context,
    layout: Int,
    private val onUpdateViewByColorDialogCallback: OnLoadDialogCallback<Int>
) : BaseDialog(context, layout) {

    override fun initListener() {
        val adapter = ColorAdapter(object : ColorAdapter.OnItemColorClick {
            override fun onClick(position: Int) {
                onUpdateViewByColorDialogCallback.onSuccess(position)
                dismiss()
            }

        })
        recyclerViewListColor.apply {
            layoutManager = GridLayoutManager(context, COLUMN_NUMBER)
            setAdapter(adapter)
        }
        adapter.submitList(colors)
    }
}
