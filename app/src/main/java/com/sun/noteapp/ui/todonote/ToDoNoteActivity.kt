package com.sun.noteapp.ui.todonote

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.Context
import android.content.Intent
import android.graphics.Paint
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.work.Data
import androidx.work.OneTimeWorkRequest
import androidx.work.WorkManager
import com.sun.noteapp.R
import com.sun.noteapp.data.model.Note
import com.sun.noteapp.data.model.ToDo
import com.sun.noteapp.data.repository.NoteLocalRepository
import com.sun.noteapp.data.source.local.LocalDataSource
import com.sun.noteapp.data.source.local.NoteDatabase
import com.sun.noteapp.ui.base.BaseDialog
import com.sun.noteapp.ui.textnote.*
import com.sun.noteapp.utils.*
import kotlinx.android.synthetic.main.activity_to_do_note.*
import kotlinx.android.synthetic.main.toolbar_text_note.*
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.TimeUnit

class ToDoNoteActivity : AppCompatActivity(),
    View.OnClickListener,
    ToDoAdapter.HandleToDoItemEvent,
    TextNoteContract.View {

    private val local by lazy {
        LocalDataSource(NoteDatabase(this))
    }
    private val repository by lazy {
        NoteLocalRepository(local)
    }
    private val presenter by lazy {
        TextNotePresenter(this, repository)
    }
    private val toDoAdapter = ToDoAdapter(this)
    private val labelAdapter = LabelAdapter()
    private val date = Calendar.getInstance()
    private var allLabels = mutableListOf<String>()
    private var selectedLabels = mutableListOf<String>()
    private var remindTime = Note.NONE
    private var noteId = 0
    private var noteStatus = NoteDatabase.UNHIDE
    private var noteColor = 0
    private var notePassword = Note.NONE
    private val toDos = mutableListOf<ToDo>()
    private var itemTouchHelper: ItemTouchHelper? = null
    private val simpleDateFormat =
        SimpleDateFormat(DATETIME_FORMAT, Locale.getDefault())

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_to_do_note)
        initViews()
        noteId = intent.getIntExtra(INTENT_NOTE_ID, 0)
        if (noteId != 0) presenter.getNoteById(noteId)
        setUpRecycler()
    }

    private fun initViews() {
        setSupportActionBar(toolbarTitle)
        supportActionBar?.setDisplayShowTitleEnabled(false)
        toolbarBottom.inflateMenu(R.menu.bottom_option_menu)
        handleEventClick()
        setUpEventMoveToDo()
        presenter.getAllLabels()
    }

    private fun handleEventClick() {
        toolbarBottom.setOnMenuItemClickListener { item: MenuItem? ->
            openDialog(item?.itemId)
        }
        imageButtonHeaderColorTextNote.setOnClickListener(this)
        buttonAlarmToDoNote.setOnClickListener(this)
        imageButtonBottomSaveToDoNote.setOnClickListener(this)
        textAddItem.setOnClickListener(this)
        horizontalScrollLabel.setOnClickListener(this)
    }

    private fun setUpRecycler() {
        recyclerToDoNoteLabel.adapter = labelAdapter
        val deviderItemDecoration = DividerItemDecoration(this, LinearLayoutManager.VERTICAL)
        recyclerToDo.apply {
            addItemDecoration(deviderItemDecoration)
            adapter = toDoAdapter
        }
        if (noteId == 0) {
            toDoAdapter.submitList(toDos)
            labelAdapter.submitList(selectedLabels)
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

    private fun initContent(content: String, remindTime: String, label: String) {
        if (remindTime != Note.NONE) {
            date.time = remindTime.formatDate()
            buttonAlarmToDoNote.text = remindTime
            if (remindTime.formatDate().time < System.currentTimeMillis()) {
                buttonAlarmToDoNote.apply {
                    paintFlags = paintFlags or Paint.STRIKE_THRU_TEXT_FLAG
                }
            }
        }
        this.remindTime = remindTime
        if (label != Note.NONE) selectedLabels.addAll(ConvertString.labelStringDataToLabelList(label))
        labelAdapter.submitList(selectedLabels)
        toDos.addAll(ConvertString.convertToDoDataStringToList(content))
        toDoAdapter.submitList(toDos)
    }

    private fun initBottomBar(password: String, modifyTime: String) {
        textUpdateTime.text = modifyTime
        notePassword = password
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
                val from = viewHolder.adapterPosition
                val to = target.adapterPosition
                Collections.swap(toDos, from, to)
                toDoAdapter.notifyItemMoved(from, to)
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
        R.id.bottomMenuSetLabel -> {
            showSetLabelDialog()
            true
        }
        R.id.bottomMenuUnlock -> showUnlockDialog()
        else -> false
    }

    private fun showDeleteAlarmDialog() {
        AlertDialog.Builder(this)
            .setMessage(R.string.message_alarm_off)
            .setPositiveButton(R.string.button_yes) { _, _ ->
                buttonAlarmToDoNote.text = null
                remindTime = Note.NONE
            }
            .setNegativeButton(R.string.button_cancel) { _, _ -> }
            .show()
    }

    private fun showPassDialog() {
        InputTextDialog(this,
            getString(R.string.message_input_password),
            null,
            true,
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

    private fun showUnlockDialog(): Boolean {
        AlertDialog.Builder(this)
            .setMessage(R.string.message_confirm_delete)
            .setPositiveButton(R.string.button_yes) { _, _ -> notePassword = Note.NONE }
            .setNegativeButton(R.string.button_cancel) { _, _ -> }
            .show()
        return true
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

    private fun showSetLabelDialog() {
        SetLabelDialog(
            this,
            allLabels,
            selectedLabels,
            false,
            null,
            object : SetLabelDialog.HandleAddLabelDialogEvent {
                override fun getSelectedLabels(selectedLabels: List<String>) {
                    this@ToDoNoteActivity.selectedLabels.apply {
                        clear()
                        addAll(selectedLabels)
                        labelAdapter.notifyDataSetChanged()
                    }
                }

                override fun addLabel() {
                    showAddLabelDialog()
                }
            }
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
                    labelAdapter.notifyDataSetChanged()
                }
            }
        ).show()
    }

    override fun onClick(view: View?) {
        when (view?.id) {
            R.id.imageButtonHeaderColorTextNote -> showImageColorDialog()
            R.id.buttonAlarmToDoNote -> showDateTimePickerDialog()
            R.id.textAddItem -> showAddToDoDialog()
            R.id.horizontalScrollLabel -> showSetLabelDialog()
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
        val note = Note(
            Note.INVALID_ID,
            editTitleTextNote.text.toString(),
            ConvertString.convertToDoListToDataString(toDos),
            TODO_NOTE,
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
                WorkManager.getInstance(this).cancelAllWorkByTag(noteId.toString())
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
            .putInt(KEY_TYPE_NOTE, TYPE_CHECK_NOTE)
            .build()
        val request = OneTimeWorkRequest.Builder(NotificationHandler::class.java)
            .setInitialDelay(duration, TimeUnit.SECONDS)
            .addTag(id.toString())
            .setInputData(data)
            .build()
        WorkManager.getInstance(this).enqueue(request)
    }

    private fun showAddToDoDialog() {
        InputTextDialog(this,
            getString(R.string.dialog_add_to_do),
            getString(R.string.button_add),
            false,
            object : InputTextDialog.HandleInputTextDialogEvent {
                override fun getInputString(text: String) {
                    addToDo(text)
                }
            }).show()
    }

    private fun addToDo(toDoName: String) {
        toDos.add(ToDo(toDoName, false))
        toDoAdapter.notifyItemInserted(toDos.size)
    }

    override fun onItemToDoClick(position: Int) {
        val toDo = ToDo(toDos[position].name, !toDos[position].isComplete)
        toDos.removeAt(position)
        toDos.add(position, toDo)
    }

    override fun changeToDoName(position: Int) {
        InputTextDialog(
            this,
            getString(R.string.dialog_add_to_do),
            null,
            false,
            object : InputTextDialog.HandleInputTextDialogEvent {
                override fun getInputString(text: String) {
                    val toDo = ToDo(text, toDos[position].isComplete)
                    toDos.removeAt(position)
                    toDos.add(position, toDo)
                    toDoAdapter.notifyItemChanged(position)
                }
            }).show()
    }

    override fun deleteToDoItem(position: Int) {
        toDoAdapter.notifyItemRemoved(position)
        toDos.removeAt(position)
    }

    override fun onDragIconMove(holder: ToDoAdapter.ViewHolder) {
        itemTouchHelper?.startDrag(holder)
    }

    override fun gotLabels(labels: List<String>) {
        allLabels.addAll(labels)
    }

    override fun backToListNote() {
        finish()
    }

    companion object {
        fun getIntent(context: Context, id: Int) =
            Intent(context, ToDoNoteActivity::class.java).apply {
                putExtra(INTENT_NOTE_ID, id)
            }
    }
}
