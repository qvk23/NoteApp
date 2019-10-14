package com.sun.noteapp.ui.textnote

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.Context
import android.content.Intent
import android.graphics.Paint
import android.os.Bundle
import android.text.InputType
import android.text.method.PasswordTransformationMethod
import android.view.MenuItem
import android.view.View
import android.widget.EditText
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
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
import com.sun.noteapp.ui.todonote.InputTextDialog
import com.sun.noteapp.ui.todonote.SetLabelDialog
import com.sun.noteapp.utils.*
import kotlinx.android.synthetic.main.activity_text_note.*
import kotlinx.android.synthetic.main.toolbar_text_note.*
import java.util.*
import java.util.concurrent.TimeUnit

class TextNoteActivity : AppCompatActivity(),
    View.OnClickListener,
    TextNoteContract.View,
    Toolbar.OnMenuItemClickListener {
    override fun onMenuItemClick(item: MenuItem?): Boolean {
        openDialog(item?.itemId)
        return true
    }

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
    private val allLabels = mutableListOf<String>()
    private val selectedLabels = mutableListOf<String>()
    private var remindTime = Note.NONE
    private var noteId = 0
    private var noteStatus = "0"
    private var noteColor = 9
    private var notePassword = Note.NONE
    private val workManager = WorkManager.getInstance(this)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_text_note)
        initView()
        noteId = intent.getIntExtra(INTENT_NOTE_ID, 0)
        if (noteId != 0) presenter.getNoteById(noteId)
        presenter.getAllLabels()
    }

    private fun initView() {
        setSupportActionBar(toolbarTitle)
        supportActionBar?.setDisplayShowTitleEnabled(false)
        toolbarBottom.inflateMenu(R.menu.bottom_option_menu)
        toolbarBottom.setOnMenuItemClickListener(this)
        imageButtonHeaderColorTextNote.setOnClickListener(this)
        buttonAlarmTextNote.setOnClickListener(this)
        horizontalScrollLabel.setOnClickListener(this)
        imageButtonBottomSaveTextNote.setOnClickListener(this)
        recyclerTextNoteLabel.adapter = adapter
        if (noteId == 0) adapter.submitList(selectedLabels)
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
        R.id.bottomMenuSetLabel -> {
            showSetLabelDialog()
            true
        }
        R.id.bottomMenuUnlock -> showUnlockDialog()
        else -> false
    }

    private fun showUnlockDialog(): Boolean {
        AlertDialog.Builder(this)
            .setMessage(R.string.message_confirm_delete)
            .setPositiveButton(R.string.button_yes) { _, _ -> notePassword = Note.NONE }
            .setNegativeButton(R.string.button_cancel) { _, _ -> }
            .show()
        return true
    }

    private fun showDeleteAlarmDialog() {
        AlertDialog.Builder(this)
            .setMessage(R.string.message_alarm_off)
            .setPositiveButton(R.string.button_yes) { _, _ ->
                buttonAlarmTextNote.text = null
                remindTime = Note.NONE
                workManager.cancelAllWorkByTag(noteId.toString())
            }
            .setNegativeButton(R.string.button_cancel) { _, _ -> }
            .show()
    }

    private fun showDeleteDialog() {
        AlertDialog.Builder(this)
            .setTitle(R.string.message_confirm_delete)
            .setMessage(R.string.message_delete)
            .setPositiveButton(R.string.button_yes) { _, _ ->
                noteStatus = getCurrentDate()
                workManager.cancelAllWorkByTag(noteId.toString())
                saveNote()
            }
            .setNegativeButton(R.string.button_no) { _, _ -> }
            .show()
    }

    private fun showPassDialog(): Boolean {
        val input = getInputText()
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

    private fun getInputText() = EditText(this).apply {
        layoutParams = ConstraintLayout.LayoutParams(
            ConstraintLayout.LayoutParams.MATCH_CONSTRAINT,
            ConstraintLayout.LayoutParams.MATCH_CONSTRAINT
        )
        inputType = InputType.TYPE_TEXT_VARIATION_PASSWORD
        transformationMethod = PasswordTransformationMethod.getInstance()
        if (notePassword != Note.NONE) {
            setText(notePassword)
            setSelection(notePassword.length)
        }
    }

    override fun initData(note: Note) {
        initTopBar(note.title, note.color)
        initContent(note.content, note.remindTime, note.label)
        initBottomBar(note.password, note.modifyTime)
        updateView(noteColor)
    }

    private fun initTopBar(title: String, color: Int) {
        editTitleTextNote.apply {
            setText(title)
            setSelection(title.length)
        }
        noteColor = color
    }

    override fun gotLabels(labels: List<String>) {
        allLabels.addAll(labels)
    }

    private fun initContent(content: String, remindTime: String, label: String) {
        if (content != Note.NONE) lineContentTextNote.apply {
            setText(content)
            setSelection(content.length)
        }
        if (remindTime != Note.NONE) {
            date.time = remindTime.formatDate()
            buttonAlarmTextNote.text = remindTime
            if (remindTime.formatDate().time < System.currentTimeMillis()) {
                buttonAlarmTextNote.apply {
                    paintFlags = paintFlags or Paint.STRIKE_THRU_TEXT_FLAG
                }
            }
        }
        this.remindTime = remindTime
        if (label != Note.NONE) selectedLabels.addAll(ConvertString.labelStringDataToLabelList(label))
        adapter.apply {
            submitList(selectedLabels)
            notifyDataSetChanged()
        }
    }

    private fun initBottomBar(password: String, modifyTime: String) {
        textUpdateTime.text = modifyTime
        notePassword = password
    }

    override fun onClick(view: View?) {
        when (view?.id) {
            R.id.imageButtonHeaderColorTextNote -> showImageColorDialog()
            R.id.buttonAlarmTextNote -> showDateTimePickerDialog()
            R.id.horizontalScrollLabel -> showSetLabelDialog()
            R.id.imageButtonBottomSaveTextNote -> {
                if (editTitleTextNote.text.isEmpty()) {
                    showToast(resources.getString(R.string.message_error_save))
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
    }

    private fun showDateTimePickerDialog() {
        DatePickerDialog(
            this,
            DatePickerDialog.OnDateSetListener { _, year, monthOfYear, dayOfMonth ->
                date.set(year, monthOfYear, dayOfMonth)
                showTimePickerDialog()
            },
            date.get(Calendar.YEAR),
            date.get(Calendar.MONTH),
            date.get(Calendar.DATE)
        ).show()
    }

    private fun showTimePickerDialog() {
        TimePickerDialog(this, TimePickerDialog.OnTimeSetListener { _, hourOfDay, minutes ->
            date.set(Calendar.HOUR_OF_DAY, hourOfDay)
            date.set(Calendar.MINUTE, minutes)
            buttonAlarmTextNote.text = date.time.formatDate()
            if (date.time.time > System.currentTimeMillis()) {
                buttonAlarmTextNote.apply {
                    paintFlags = 0
                }
            }
            remindTime = date.time.formatDate()
        }, date.get(Calendar.HOUR_OF_DAY), date.get(Calendar.MINUTE), true).show()
    }

    private fun showSetLabelDialog() {
        SetLabelDialog(
            this,
            allLabels,
            selectedLabels,
            false,
            null,
            object : SetLabelDialog.HandleAddLabelDialogEvent {
                override fun getSelectedLabels(selectedLabels: List<String>) {
                    this@TextNoteActivity.selectedLabels.apply {
                        clear()
                        addAll(selectedLabels)
                        adapter.notifyDataSetChanged()
                    }
                }

                override fun addLabel() {
                    showAddLabelDialog()
                }
            }
        ).show()
    }

    private fun showAddLabelDialog() {
        InputTextDialog(
            this,
            getString(R.string.button_new_label),
            null,
            false,
            object : InputTextDialog.HandleInputTextDialogEvent {
                override fun getInputString(text: String) {
                    allLabels.add(text)
                    selectedLabels.add(text)
                    adapter.notifyDataSetChanged()
                }
            }
        ).show()
    }

    private fun saveNote() {
        val note = Note(
            Note.INVALID_ID,
            editTitleTextNote.text.toString(),
            lineContentTextNote.text.toString(),
            TYPE_TEXT_NOTE,
            noteColor,
            ConvertString.labelListToLabelStringData(selectedLabels),
            getCurrentTime(),
            remindTime,
            notePassword,
            noteStatus
        )
        val duration: Long =
            (date.timeInMillis - System.currentTimeMillis()) / MILLISECOND_TO_SECONDS
        if (remindTime != Note.NONE && remindTime.formatDate().time > System.currentTimeMillis()) {
            if (noteId == 0) {
                setReminder(
                    SharePreferencesHelper.count + 1,
                    editTitleTextNote.text.toString(),
                    duration
                )
            } else {
                workManager.cancelAllWorkByTag(noteId.toString())
                setReminder(noteId, editTitleTextNote.text.toString(), duration)
            }
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
            .putInt(KEY_TYPE_NOTE, TYPE_TEXT_NOTE)
            .build()
        val request = OneTimeWorkRequest.Builder(NotificationHandler::class.java)
            .setInitialDelay(duration, TimeUnit.SECONDS)
            .addTag(id.toString())
            .setInputData(data)
            .build()
        workManager.enqueue(request)

    }

    companion object {
        fun getIntent(context: Context, id: Int) =
            Intent(context, TextNoteActivity::class.java).apply {
                putExtra(INTENT_NOTE_ID, id)
            }
    }
}
