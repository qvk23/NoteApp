package com.sun.noteapp.ui.textnote

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import com.sun.noteapp.R
import kotlinx.android.synthetic.main.activity_text_note.*
import kotlinx.android.synthetic.main.toolbar_text_note.*

class TextNoteActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_text_note)
        initView()
    }

    private fun initView() {
        setSupportActionBar(toolbarTitle)
        supportActionBar?.setDisplayShowTitleEnabled(false)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        toolbarBottom.inflateMenu(R.menu.bottom_option_menu)
        recyclerTextNoteLabel.apply {
            layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
        }

    }

    companion object {
        fun getIntent(context: Context) = Intent(context, TextNoteActivity::class.java)
    }
}
