package com.sun.noteapp.ui.todonote

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.sun.noteapp.R
import com.sun.noteapp.data.model.Note
import com.sun.noteapp.data.source.local.NoteDatabase
import com.sun.noteapp.ui.base.BaseDialog
import com.sun.noteapp.ui.textnote.ImageColorDialog
import com.sun.noteapp.ui.textnote.LabelAdapter
import com.sun.noteapp.utils.*
import kotlinx.android.synthetic.main.activity_to_do_note.*
import kotlinx.android.synthetic.main.toolbar_text_note.*
import java.text.SimpleDateFormat
import java.util.*

class ToDoNoteActivity : AppCompatActivity(),
    View.OnClickListener {

    private val labelAdapter = LabelAdapter()
    private val date = Calendar.getInstance()
    private var note: Note? = null
    private var listLabel = listOf<String>()
    private var remindTime = NONE
    private var noteId = 0
    private var noteStatus = NoteDatabase.UNHIDE
    private var noteColor = 0
    private var notePassword = NONE
    private val toDos = mutableListOf<Pair<String, Boolean>>()
    private var itemTouchHelper: ItemTouchHelper? = null
    private val simpleDateFormat =
        SimpleDateFormat(DATETIME_FORMAT, Locale.getDefault())

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_to_do_note)
        initViews()
        initData()
    }

    private fun initViews() {
        setSupportActionBar(toolbarTitle)
        supportActionBar?.setDisplayShowTitleEnabled(false)
        toolbarBottom.inflateMenu(R.menu.bottom_option_menu)
        handleEventClick()
        setUpRecycler()
        setUpEventMoveToDo()
    }

    private fun handleEventClick() {
        toolbarBottom.setOnMenuItemClickListener { item: MenuItem? ->
            openDialog(item?.itemId)
        }
        imageButtonHeaderColorTextNote.setOnClickListener(this)
        buttonAlarmToDoNote.setOnClickListener(this)
        imageButtonBottomSaveToDoNote.setOnClickListener(this)
        textAddItem.setOnClickListener(this)
    }

    private fun setUpRecycler() {
        recyclerToDoNoteLabel.adapter = labelAdapter
        val deviderItemDecoration = DividerItemDecoration(this, LinearLayoutManager.VERTICAL)
        recyclerToDo.addItemDecoration(deviderItemDecoration)
    }

    private fun initData() {
        note = intent.getParcelableExtra(INTENT_NOTE_DETAIL)
        note?.let {
            noteId = it.id
            if (it.remindTime != NONE) {
                date.time = simpleDateFormat.parse(it.remindTime)
                buttonAlarmToDoNote.text = it.remindTime
            }
            editTitleTextNote.apply {
                setText(it.title)
                setSelection(it.title.length)
            }
            if (it.remindTime != NONE) buttonAlarmToDoNote.text = it.remindTime
            textUpdateTime.text = it.modifyTime
            noteColor = it.color
            notePassword = it.password
            if (it.label != NONE) listLabel =
                ConvertString.labelStringDataToLabelList(it.label)
            labelAdapter.submitList(listLabel)
            toDos.addAll(ConvertString.actionDataStringToActionCheckList(it.content))
        }
        updateView(noteColor)
    }

    private fun setUpEventMoveToDo() {
        val moveCallBack = object : ItemTouchHelper.Callback() {
            override fun getMovementFlags(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder
            ): Int =
                makeMovementFlags(ItemTouchHelper.UP or ItemTouchHelper.DOWN, 0)

            override fun onMove(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                target: RecyclerView.ViewHolder
            ): Boolean {
                return true
            }

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
            }
        }
        itemTouchHelper = ItemTouchHelper(moveCallBack)
        itemTouchHelper?.attachToRecyclerView(recyclerToDo)
    }

    private fun openDialog(itemId: Int?) = when (itemId) {
        R.id.bottomMenuDelete -> {
            if (noteId != 0) showDeleteDialog()
            true
        }
        R.id.bottomMenuLock -> {
            showPassDialog()
            true
        }
        R.id.bottomMenuDeleteRemind -> {
            if (buttonAlarmToDoNote.text.isNotEmpty()) showDeleteAlarmDialog()
            true
        }
        else -> false
    }

    private fun showDeleteAlarmDialog() {
        AlertDialog.Builder(this)
            .setMessage(R.string.message_alarm_off)
            .setPositiveButton(R.string.button_yes) { _, _ ->
                buttonAlarmToDoNote.text = null
                remindTime = NONE
            }
            .setNegativeButton(R.string.button_cancel) { _, _ -> }
            .show()
    }

    private fun showPassDialog() {
        InputTextDialog(this,
            getString(R.string.message_input_password),
            null,
            object : InputTextDialog.HandleInputTextDialogEvent {
                override fun getInputString(text: String) {
                    notePassword = text
                }
            }).show()
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
        layoutContent.setBackgroundResource(lightColor)
        horizontalScrollLabel.setBackgroundResource(mediumColor)
        buttonAlarmToDoNote.setBackgroundResource(mediumColor)
        toolbarBottom.setBackgroundResource(mediumColor)
        if (listLabel.isEmpty()) horizontalScrollLabel.gone()
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
            buttonAlarmToDoNote.text = simpleDateFormat.format(date.time)
            remindTime = simpleDateFormat.format(date.time)
        }, date.get(Calendar.HOUR_OF_DAY), date.get(Calendar.MINUTE), true).show()
    }

    override fun onClick(view: View?) {
        when (view?.id) {
            R.id.imageButtonHeaderColorTextNote -> showImageColorDialog()
            R.id.buttonAlarmToDoNote -> showDateTimePickerDialog()
            R.id.imageButtonBottomSaveToDoNote -> {
                if (editTitleTextNote.text.isEmpty()) {
                    showToast(getString(R.string.message_error_save))
                } else {
                    saveNote()
                }
            }
        }
    }

    private fun saveNote() {
    }

    companion object {
        fun getIntent(context: Context, note: Note?) =
            Intent(context, ToDoNoteActivity::class.java).apply {
                note?.let {
                    putExtra(INTENT_NOTE_DETAIL, it)
                }
            }
    }
}
