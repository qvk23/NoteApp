package com.sun.noteapp.ui.todonote

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.sun.noteapp.R
import com.sun.noteapp.ui.base.BaseRecyclerViewAdapter
import com.sun.noteapp.ui.base.BaseViewHolder
import kotlinx.android.synthetic.main.item_label.view.*

class LabelAdapterVertical(
    private val selectedLabels: List<String>,
    private val listener: HandleLabelItemClick
) : BaseRecyclerViewAdapter<String, LabelAdapterVertical.ViewHolder>(){

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder =
        ViewHolder(
            LayoutInflater.from(parent.context).inflate(
                R.layout.item_label,
                parent,
                false
            ),
            selectedLabels,
            listener
        )

    class ViewHolder(
        itemView: View,
        private val selectedLabels: List<String>,
        private val listener: HandleLabelItemClick
        ) : BaseViewHolder<String>(itemView){

        private var isSelectedLabel = false

        override fun onBindData(itemData: String) {
            super.onBindData(itemData)
            itemView.apply {
                textLabelName.text = itemData
                if(selectedLabels.contains(itemData))
                    isSelectedLabel = true
                else
                    isSelectedLabel = false
                setIconCheckboxLabel()
            }
        }

        private fun setIconCheckboxLabel(){
            if(isSelectedLabel){
                itemView.iconCheckboxLabel.setImageResource(R.drawable.ic_check_box_black_30dp)
            }else{
                itemView.iconCheckboxLabel.setImageResource(R.drawable.ic_check_box_outline_blank_black_30dp)
            }
        }

        private fun checkSelectedLabel(){
            isSelectedLabel = !isSelectedLabel
            setIconCheckboxLabel()
        }

        override fun onHandleItemClick(mainItem: String) {
            checkSelectedLabel()
            listener.onLabelItemClick(adapterPosition, mainItem)
        }
    }

    interface HandleLabelItemClick{
        fun onLabelItemClick(position: Int, label: String)
    }
}
