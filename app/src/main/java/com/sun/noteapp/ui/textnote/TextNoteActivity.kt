package com.sun.noteapp.ui.textnote

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import com.sun.noteapp.R
import com.sun.noteapp.ui.base.BaseDialog
import com.sun.noteapp.utils.ColorPicker
import com.sun.noteapp.utils.getCurrentTime
import kotlinx.android.synthetic.main.activity_text_note.*
import kotlinx.android.synthetic.main.toolbar_text_note.*
import java.text.SimpleDateFormat
import java.util.*

class TextNoteActivity : AppCompatActivity() {
    private val date = Calendar.getInstance()
    private val simpleDateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault())
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
        imageButtonHeaderColorTextNote.setOnClickListener {
            showImageColorDialog()
        }
        buttonAlarmTextNote.setOnClickListener {
            showDateTimePickerDialog()
        }
        imageButtonBottomSaveTextNote.setOnClickListener {
            textUpdateTime.text = getCurrentTime()
        }
    }

    private fun showDateTimePickerDialog() {
        DatePickerDialog(
            this,
            DatePickerDialog.OnDateSetListener { _, year, monthOfYear, dayOfMonth ->
                date.set(year, monthOfYear, dayOfMonth)
                TimePickerDialog(this, TimePickerDialog.OnTimeSetListener { _, hourOfDay, minutes ->
                    date.set(Calendar.HOUR_OF_DAY, hourOfDay)
                    date.set(Calendar.MINUTE, minutes)
                    buttonAlarmTextNote.text = simpleDateFormat.format(date.time)
                }, date.get(Calendar.HOUR_OF_DAY), date.get(Calendar.MINUTE), true).show()
            },
            date.get(Calendar.YEAR),
            date.get(Calendar.MONTH),
            date.get(Calendar.DATE)
        ).show()
    }

    private fun showImageColorDialog() {
        ImageColorDialog(
            this,
            R.layout.dialog_color,
            object : BaseDialog.OnLoadDialogCallback<Int> {
                override fun onSuccess(parrams: Int) {
                    val mediumColor = ColorPicker.getMediumColor(parrams)
                    val lightColor = ColorPicker.getLightColor(parrams)
                    imageButtonHeaderColorTextNote.setBackgroundResource(mediumColor)
                    toolbarTitle.setBackgroundResource(mediumColor)
                    lineContentTextNote.setBackgroundResource(lightColor)
                    horizontalScrollLabel.setBackgroundResource(mediumColor)
                    buttonAlarmTextNote.setBackgroundResource(mediumColor)
                    toolbarBottom.setBackgroundResource(mediumColor)
                }

            }).show()
    }

    companion object {
        fun getIntent(context: Context) = Intent(context, TextNoteActivity::class.java)
    }
}
