package com.sun.noteapp.ui.textnote

import android.content.Context
import androidx.recyclerview.widget.GridLayoutManager
import com.sun.noteapp.ui.base.BaseDialog
import com.sun.noteapp.utils.getMediumColor
import com.sun.noteapp.utils.gone
import kotlinx.android.synthetic.main.dialog_color.*

private const val COLUMN_NUMBER = 3

class ImageColorDialog(
    context: Context,
    layout: Int,
    private val onUpdateColorCallback: OnLoadDialogCallback<Int>
) : BaseDialog(context, layout) {

    init {
        textTitleColorDialog.gone()
    }

    override fun initListener() {
        val adapter = ImageColorAdapter(object : ImageColorAdapter.OnItemColorClick {
            override fun onClick(position: Int) {
                onUpdateColorCallback.onSuccess(position)
                dismiss()
            }

        })
        recyclerViewListColor.apply {
            layoutManager = GridLayoutManager(context, COLUMN_NUMBER)
            setAdapter(adapter)
        }
        val mediumColors = getMediumColor()
        adapter.submitList(mediumColors)
    }
}
