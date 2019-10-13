package com.sun.noteapp.ui.trash

import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.sun.noteapp.R
import com.sun.noteapp.data.model.Note
import com.sun.noteapp.data.repository.NoteLocalRepository
import com.sun.noteapp.data.source.local.LocalDataSource
import com.sun.noteapp.data.source.local.NoteDatabase
import com.sun.noteapp.ui.base.BaseDialog
import com.sun.noteapp.ui.home.dialog.SortDialog
import com.sun.noteapp.ui.textnote.TextNoteActivity
import com.sun.noteapp.utils.SWIPE_RTL
import com.sun.noteapp.utils.gone
import com.sun.noteapp.utils.visible
import kotlinx.android.synthetic.main.activity_trash.*

class TrashActivity : AppCompatActivity(),
    TrashContract.View,
    TrashAdapter.HandleItemClick {

    private val local by lazy {
        LocalDataSource(NoteDatabase(this))
    }
    private val repository by lazy {
        NoteLocalRepository(local)
    }
    private val presenter by lazy {
        TrashPresenter(this, repository)
    }

    private val trashAdapter = TrashAdapter(this)
    private val layoutRecycler = LinearLayoutManager(this)
    private val selectedItems = mutableListOf<Int>()
    private var sortType = NoteDatabase.ORDERBY_CREATETIME

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_trash)
        initView()
    }

    private fun initView() {
        toolbarTrash.title = getString(R.string.activity_trash)
        setSupportActionBar(toolbarTrash)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        setUpToolbarSelector()
        recyclerTrash.apply {
            adapter = trashAdapter
            layoutManager = layoutRecycler
            itemAnimator = null
        }
        presenter.getAllHideNote(sortType)
        enableSwipeToDelete()
    }

    private fun setUpToolbarSelector() {
        toolbarSelector.setNavigationIcon(R.drawable.ic_close_black_24dp)
        toolbarSelector.setNavigationOnClickListener {
            selectedItems.clear()
            trashAdapter.removeAllSelection()
            changeToolbar()
        }
        toolbarSelector.inflateMenu(R.menu.option_menu_selector)
        toolbarSelector.setOnMenuItemClickListener { item ->
            when (item?.itemId) {
                R.id.option_item_delete_forever -> {
                    createDeleteDialog(selectedItems)
                }
                R.id.option_item_restore -> {
                    createRestoreDialog(selectedItems)
                }
                R.id.option_item_select_all -> {
                    selectedItems.clear()
                    for (position in 0 until trashAdapter.itemCount)
                        selectedItems.add(position)
                    trashAdapter.selectAll()
                }
            }
            false
        }
        toolbarSelector.gone()
    }

    fun closeToolBarSelector() {
        selectedItems.clear()
        supportActionBar?.show()
        toolbarSelector.gone()
    }

    private fun changeToolbar() {
        if (selectedItems.isEmpty()) {
            supportActionBar?.show()
            toolbarSelector.gone()
        } else {
            supportActionBar?.hide()
            toolbarSelector.visible()
        }
    }

    private fun enableSwipeToDelete() {
        val swipeItemCallback = object : SwipeItemCallback(this) {
            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                val position = viewHolder.adapterPosition
                if (direction == SWIPE_RTL) {
                    createDeleteDialog(arrayListOf(position))
                } else {
                    createRestoreDialog(arrayListOf(position))
                }
            }
        }
        val itemTouchHelper = ItemTouchHelper(swipeItemCallback)
        itemTouchHelper.attachToRecyclerView(recyclerTrash)
    }

    private fun showSortDialog() {
        SortDialog(this, R.layout.dialog_sort, object : BaseDialog.OnLoadDialogCallback<String> {
            override fun onSuccess(params: String) {
                sortType = params
                presenter.getAllHideNote(sortType)
            }
        }).show()
    }

    private fun createDeleteDialog(selectedItems: List<Int>) {
        AlertDialog.Builder(this)
            .setMessage(getString(R.string.question_detele))
            .setPositiveButton(
                getString(R.string.answer_yes),
                object : DialogInterface.OnClickListener {
                    override fun onClick(p0: DialogInterface?, p1: Int) {
                        presenter.deleteNotes(selectedItems)
                        closeToolBarSelector()
                    }
                })
            .setNegativeButton(
                getString(R.string.answer_no),
                object : DialogInterface.OnClickListener {
                    override fun onClick(p0: DialogInterface?, p1: Int) {
                        trashAdapter.removeAllSelection()
                        closeToolBarSelector()
                    }
                })
            .show()
    }

    private fun createRestoreDialog(selectedItems: List<Int>) {
        AlertDialog.Builder(this)
            .setMessage(R.string.question_restore)
            .setPositiveButton(
                getString(R.string.answer_yes),
                object : DialogInterface.OnClickListener {
                    override fun onClick(p0: DialogInterface?, p1: Int) {
                        presenter.restoreNotes(selectedItems)
                        closeToolBarSelector()
                    }
                })
            .setNegativeButton(
                getString(R.string.answer_no),
                object : DialogInterface.OnClickListener {
                    override fun onClick(p0: DialogInterface?, p1: Int) {
                        trashAdapter.removeAllSelection()
                        closeToolBarSelector()
                    }
                })
            .show()
    }

    override fun showNotes(notes: List<Note>) =
        trashAdapter.run {
            removeAllSelection()
            updateData(notes)
        }

    override fun showNoteDetail(position: Int, item: Note) {
        startActivity(TextNoteActivity.getIntent(this, item.id))
    }

    override fun setUpToolbarSelector(selectedItems: List<Int>) {
        this.selectedItems.clear()
        this.selectedItems.addAll(selectedItems)
        changeToolbar()
        trashAdapter.setUpSelection(selectedItems)
    }

    override fun showMessage(stringId: Int) {
        Toast.makeText(this, getString(stringId), Toast.LENGTH_SHORT).show()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.option_menu_trash, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        when (item?.itemId) {
            R.id.option_item_sort -> {
                showSortDialog()
            }
        }
        return super.onOptionsItemSelected(item)
    }

    companion object {
        fun getIntent(context: Context) = Intent(context, TrashActivity::class.java)
    }
}
