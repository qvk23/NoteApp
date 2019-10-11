package com.sun.noteapp.ui.textnote

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.InputType
import android.text.method.PasswordTransformationMethod
import android.view.MenuItem
import android.view.View
import android.widget.EditText
import androidx.appcompat.app.AlertDialog
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.work.Data
import androidx.work.OneTimeWorkRequest
import androidx.work.WorkManager
import com.sun.noteapp.R
import com.sun.noteapp.data.model.Note
import com.sun.noteapp.data.repository.NoteLocalRepository
import com.sun.noteapp.data.source.local.LocalDataSource
import com.sun.noteapp.data.source.local.NoteDatabase
import com.sun.noteapp.ui.base.BaseDialog
import com.sun.noteapp.utils.*
import kotlinx.android.synthetic.main.activity_text_note.*
import kotlinx.android.synthetic.main.toolbar_text_note.*
import java.util.*
import java.util.concurrent.TimeUnit

class TextNoteActivity : AppCompatActivity(),
    View.OnClickListener,
    TextNoteContract.View {
    override fun backToListNote() {
        finish()
    }

    private val local by lazy {
        LocalDataSource(NoteDatabase(this))
    }
    private val repository by lazy {
        NoteLocalRepository(local)
    }
    private val presenter by lazy {
        TextNotePresenter(this, repository)
    }
    private val adapter = LabelAdapter()
    private val date = Calendar.getInstance()
    private var note: Note? = null
    private var listLabel = listOf<String>()
    private var remindTime = NONE
    private var noteId = 0
    private var noteStatus = "0"
    private var noteColor = 9
    private var notePassword = NONE
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_text_note)
        initView()
        initData()
    }

    private fun initView() {
        setSupportActionBar(toolbarTitle)
        supportActionBar?.setDisplayShowTitleEnabled(false)
        toolbarBottom.inflateMenu(R.menu.bottom_option_menu)
        toolbarBottom.setOnMenuItemClickListener { item: MenuItem? ->
            openDialog(item?.itemId)
        }
        imageButtonHeaderColorTextNote.setOnClickListener(this)
        buttonAlarmTextNote.setOnClickListener(this)
        imageButtonBottomSaveTextNote.setOnClickListener(this)
        recyclerTextNoteLabel.adapter = adapter
    }

    private fun openDialog(itemId: Int?) = when (itemId) {
        R.id.bottomMenuDelete -> {
            if (noteId != 0) showDeleteDialog()
            true
        }
        R.id.bottomMenuLock -> showPassDialog()
        R.id.bottomMenuDeleteRemind -> {
            if (buttonAlarmTextNote.text.isNotEmpty()) showDeleteAlarmDialog()
            true
        }
        R.id.bottomMenuUnlock -> showUnlockDialog()
        else -> false
    }

    private fun showUnlockDialog(): Boolean {
        AlertDialog.Builder(this)
            .setMessage(R.string.message_confirm_delete)
            .setPositiveButton(R.string.button_yes) { _, _ -> notePassword = NONE }
            .setNegativeButton(R.string.button_cancel) { _, _ -> }
            .show()
        return true
    }

    private fun showDeleteAlarmDialog() {
        AlertDialog.Builder(this)
            .setMessage(R.string.message_alarm_off)
            .setPositiveButton(R.string.button_yes) { _, _ ->
                buttonAlarmTextNote.text = null
                remindTime = NONE
            }
            .setNegativeButton(R.string.button_cancel) { _, _ -> }
            .show()
    }

    private fun showPassDialog(): Boolean {
        val input = EditText(this)
        input.apply {
            layoutParams = ConstraintLayout.LayoutParams(
                ConstraintLayout.LayoutParams.MATCH_CONSTRAINT,
                ConstraintLayout.LayoutParams.MATCH_CONSTRAINT
            )
            inputType = InputType.TYPE_TEXT_VARIATION_PASSWORD
            transformationMethod = PasswordTransformationMethod.getInstance()
            if (notePassword != NONE) {
                setText(notePassword)
                setSelection(notePassword.length)
            }
        }

        AlertDialog.Builder(this)
            .setMessage(R.string.message_input_password)
            .setView(input)
            .setPositiveButton(R.string.button_set) { _, _ ->
                notePassword = input.text.toString()
            }
            .setNegativeButton(R.string.button_cancel) { _, _ -> }
            .show()
        return true
    }

    private fun showDeleteDialog() {
        AlertDialog.Builder(this)
            .setTitle(R.string.message_confirm_delete)
            .setMessage(R.string.message_delete)
            .setPositiveButton(R.string.button_yes) { _, _ ->
                noteStatus = getCurrentDate()
                saveNote()
            }
            .setNegativeButton(R.string.button_no) { _, _ -> }
            .show()
    }

    private fun initData() {
        note = intent.getParcelableExtra(INTENT_NOTE_DETAIL)
        note?.let {
            noteId = it.id
            if (it.remindTime != NONE) {
                date.time = it.remindTime.formatDate()
                buttonAlarmTextNote.text = it.remindTime
            }
            editTitleTextNote.apply {
                setText(it.title)
                setSelection(it.title.length)
            }
            if (it.content != NONE) lineContentTextNote.apply {
                setText(it.content)
                setSelection(it.content.length)
            }
            if (it.remindTime != NONE) buttonAlarmTextNote.text = it.remindTime
            textUpdateTime.text = it.modifyTime
            noteColor = it.color
            notePassword = it.password
            if (it.label != NONE) listLabel =
                ConvertString.labelStringDataToLabelList(it.label)
            adapter.submitList(listLabel)
        }
        updateView(noteColor)
    }

    override fun onClick(view: View?) {
        when (view?.id) {
            R.id.imageButtonHeaderColorTextNote -> showImageColorDialog()
            R.id.buttonAlarmTextNote -> showDateTimePickerDialog()
            R.id.imageButtonBottomSaveTextNote -> {
                if (editTitleTextNote.text.isEmpty()) {
                    showToast(R.string.message_error_save.toString())
                } else {
                    saveNote()
                }
            }
        }
    }

    private fun showImageColorDialog() {
        ImageColorDialog(
            this,
            R.layout.dialog_color,
            object : BaseDialog.OnLoadDialogCallback<Int> {
                override fun onSuccess(params: Int) {
                    updateView(params)
                }
            }).show()
    }

    private fun updateView(params: Int) {
        noteColor = params
        val mediumColor = ColorPicker.getMediumColor(params)
        val lightColor = ColorPicker.getLightColor(params)
        imageButtonHeaderColorTextNote.setBackgroundResource(mediumColor)
        toolbarTitle.setBackgroundResource(mediumColor)
        lineContentTextNote.setBackgroundResource(lightColor)
        horizontalScrollLabel.setBackgroundResource(mediumColor)
        buttonAlarmTextNote.setBackgroundResource(mediumColor)
        toolbarBottom.setBackgroundResource(mediumColor)
        if (listLabel.isEmpty()) horizontalScrollLabel.gone()
    }

    private fun showDateTimePickerDialog() {
        DatePickerDialog(
            this,
            DatePickerDialog.OnDateSetListener { _, year, monthOfYear, dayOfMonth ->
                date.set(year, monthOfYear, dayOfMonth)
                TimePickerDialog(this, TimePickerDialog.OnTimeSetListener { _, hourOfDay, minutes ->
                    date.set(Calendar.HOUR_OF_DAY, hourOfDay)
                    date.set(Calendar.MINUTE, minutes)
                    buttonAlarmTextNote.text = date.time.formatDate()
                    remindTime = date.time.formatDate()
                }, date.get(Calendar.HOUR_OF_DAY), date.get(Calendar.MINUTE), true).show()
            },
            date.get(Calendar.YEAR),
            date.get(Calendar.MONTH),
            date.get(Calendar.DATE)
        ).show()
    }

    private fun saveNote() {
        val note = Note(
            Note.INVALID_ID,
            editTitleTextNote.text.toString(),
            lineContentTextNote.text.toString(),
            TYPE_TEXT_NOTE,
            noteColor,
            NONE,
            getCurrentTime(),
            remindTime,
            notePassword,
            noteStatus
        )
        val duration: Long =
            (date.timeInMillis - System.currentTimeMillis()) / MILLISECOND_TO_SECONDS
        if (remindTime != NONE) {
            WorkManager.getInstance(this).cancelAllWorkByTag(noteId.toString())
            setReminder(noteId, editTitleTextNote.text.toString(), duration)
        }
        if (noteId == 0) {
            presenter.addNote(note)
        } else {
            presenter.editNote(noteId, note)
        }
    }

    private fun setReminder(id: Int, title: String, duration: Long) {
        val data = Data.Builder()
            .putInt(KEY_ID, id)
            .putString(KEY_TITLE, title)
            .build()
        val request = OneTimeWorkRequest.Builder(NotificationHandler::class.java)
            .setInitialDelay(duration, TimeUnit.SECONDS)
            .addTag(id.toString())
            .setInputData(data)
            .build()
        WorkManager.getInstance(this).enqueue(request)

    }

    companion object {
        fun getIntent(context: Context, note: Note?) =
            Intent(context, TextNoteActivity::class.java).apply {
                putExtra(INTENT_NOTE_DETAIL, note)
            }
    }
}
