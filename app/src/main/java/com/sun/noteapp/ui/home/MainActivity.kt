package com.sun.noteapp.ui.home

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.InputType
import android.text.method.PasswordTransformationMethod
import android.view.Menu
import android.view.MenuItem
import android.widget.EditText
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AlertDialog
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.view.GravityCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.Adapter
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.google.android.material.navigation.NavigationView
import com.sun.noteapp.R
import com.sun.noteapp.data.model.Note
import com.sun.noteapp.data.repository.NoteLocalRepository
import com.sun.noteapp.data.source.local.LocalDataSource
import com.sun.noteapp.data.source.local.NoteDatabase
import com.sun.noteapp.ui.base.BaseDialog
import com.sun.noteapp.ui.home.dialog.*
import com.sun.noteapp.ui.search.SearchActivity
import com.sun.noteapp.ui.textnote.TextNoteActivity
import com.sun.noteapp.ui.trash.TrashActivity
import com.sun.noteapp.utils.*
import com.sun.noteapp.ui.todonote.ToDoNoteActivity
import com.sun.noteapp.utils.TEXT_NOTE
import com.sun.noteapp.utils.getScreenWidth
import com.sun.noteapp.utils.getListColor
import com.sun.noteapp.utils.showToast
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.nav_home_screen.*
import java.lang.Exception

class MainActivity : AppCompatActivity(),
    MainContract.View,
    NavigationView.OnNavigationItemSelectedListener,
    OnNoteItemClick {
    private val local by lazy {
        LocalDataSource(NoteDatabase(this))
    }

    private val repository by lazy {
        NoteLocalRepository(local)
    }
    private val presenter by lazy {
        MainPresenter(this, repository)
    }
    private val adapterVertical = NoteVerticalAdapter(this)

    private val adapterVerticalWide = NoteVerticalWideAdapter(
        this,
        getScreenWidth()
    )
    private val adapterStaggeredGrid = NoteVerticalWideAdapter(
        this,
        getScreenWidth() / 2
    )
    private val linearLayoutManager = LinearLayoutManager(this)

    private val staggeredGridLayoutManager = StaggeredGridLayoutManager(
        COLUMN_NUMBER,
        LinearLayoutManager.VERTICAL
    )
    private var selectedLabels = mutableListOf<String>()
    private var allLabels = mutableListOf<String>()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        initView()
        initData()
    }

    private fun initView() {
        setSupportActionBar(toolbarHome)
        supportActionBar?.setTitle(R.string.nav_header_name)
        val toggle = ActionBarDrawerToggle(
            this,
            drawerNavigate,
            toolbarHome,
            R.string.navigation_drawer_open,
            R.string.navigation_drawer_close
        )
        navigationView.setNavigationItemSelectedListener(this)
        drawerNavigate.addDrawerListener(toggle)
        toggle.syncState()
        fabAdd.setOnClickListener {
            CreateNoteDialog(this, object : BaseDialog.OnLoadDialogCallback<Int> {
                override fun onSuccess(params: Int) {
                    openNoteScreen(params, null)
                }
            }).show()
        }
    }

    private fun initData() {
        SharePreferencesHelper.init(this)
        setViewType(SharePreferencesHelper.type)
    }

    private fun openNoteScreen(noteType: Int, note: Note?) {
        if (noteType == TEXT_NOTE) {
            startActivity(TextNoteActivity.getIntent(this@MainActivity, note))
        } else {
            startActivity(ToDoNoteActivity.getIntent(this@MainActivity, note))
        }
    }

    override fun onResume() {
        super.onResume()
        presenter.getAllNotesWithOption(
            SharePreferencesHelper.color,
            selectedLabels,
            SharePreferencesHelper.sortType
        )
        presenter.getAllLabels()
    }

    override fun showAllNotes(notes: List<Note>) {
        adapterVertical.updateData(notes)
        adapterVerticalWide.updateData(notes)
        adapterStaggeredGrid.updateData(notes)
    }

    override fun onNavigationItemSelected(item: MenuItem) = when (item.itemId) {
        R.id.navItemTrash -> {
            startActivity(TrashActivity.getIntent(this))
            true
        }
        else -> false
    }

    override fun showError(exception: Exception) {
        showToast(exception.toString())
    }

    override fun showNoteDetail(position: Int, note: Note) {
        if (note.password == NONE) {
            openNoteScreen(note.type, note)
        } else {
            val input = EditText(this).apply {
                layoutParams = ConstraintLayout.LayoutParams(
                    ConstraintLayout.LayoutParams.MATCH_CONSTRAINT,
                    ConstraintLayout.LayoutParams.MATCH_CONSTRAINT
                )
                inputType = InputType.TYPE_TEXT_VARIATION_PASSWORD
                transformationMethod = PasswordTransformationMethod.getInstance()
            }
            val dialog = AlertDialog.Builder(this)
                .setMessage(R.string.message_input_password)
                .setView(input)
                .setPositiveButton(R.string.button_ok, null)
                .setNegativeButton(R.string.button_cancel) { _, _ -> }
                .create()
            dialog.setOnShowListener {
                dialog.getButton(AlertDialog.BUTTON_POSITIVE)
                    .setOnClickListener {
                        if (input.text.toString() == note.password) {
                            startActivity(
                                TextNoteActivity.getIntent(
                                    this,
                                    note
                                )
                            )
                            dialog.dismiss()
                        } else {
                            showToast(resources.getString(R.string.message_wrong))
                        }
                    }
            }
            dialog.show()
        }

    }

    override fun gettedLabels(labels: List<String>) {
        allLabels.addAll(labels)
    }

    override fun onBackPressed() {
        if (drawerNavigate.isDrawerOpen(GravityCompat.START)) {
            drawerNavigate.closeDrawer(GravityCompat.START)
        } else {
            super.onBackPressed()
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.option_menu, menu)
        return true
    }

    private fun <T : RecyclerView.ViewHolder> setUpRecycler(
        newLayoutManager: RecyclerView.LayoutManager,
        newAdapter: Adapter<T>
    ) {
        recyclerHome.apply {
            layoutManager = newLayoutManager
            adapter = newAdapter
        }
    }

    private fun setViewType(type: Int) {
        when (type) {
            LIST -> setUpRecycler(linearLayoutManager, adapterVertical)
            DETAIL -> setUpRecycler(linearLayoutManager, adapterVerticalWide)
            GRID -> setUpRecycler(staggeredGridLayoutManager, adapterStaggeredGrid)
        }
    }

    override fun onOptionsItemSelected(item: MenuItem?) = when (item?.itemId) {
        R.id.option_item_color -> {
            showColorDialog()
            true
        }
        R.id.option_item_sort -> {
            showSortDialog()
            true
        }
        R.id.option_item_view -> {
            showViewDialog()
            true
        }
        R.id.option_item_label -> {
            showLabelDialog()
            true
        }
        R.id.option_item_search -> {
            startActivity(SearchActivity.getIntent(this))
            true
        }
        else -> false
    }

    private fun showColorDialog() {
        ColorDialog(this, R.layout.dialog_color, object : BaseDialog.OnLoadDialogCallback<Int> {
            override fun onSuccess(params: Int) {
                SharePreferencesHelper.color = params
                presenter.getAllNotesWithOption(
                    params,
                    selectedLabels,
                    SharePreferencesHelper.sortType
                )
            }
        }).show()
    }

    private fun showViewDialog() {
        ViewDialog(this, R.layout.dialog_view, object : BaseDialog.OnLoadDialogCallback<Int> {
            override fun onSuccess(params: Int) {
                setViewType(params)
                SharePreferencesHelper.type = params
            }
        }).show()
    }

    private fun showLabelDialog() {
        LabelDialog(this, R.layout.dialog_label).show()
    }

    private fun showSortDialog() {
        SortDialog(this, R.layout.dialog_sort, object : BaseDialog.OnLoadDialogCallback<String> {
            override fun onSuccess(params: String) {
                SharePreferencesHelper.sortType = params
                presenter.getAllNotesWithOption(
                    SharePreferencesHelper.color,
                    selectedLabels,
                    params
                )
            }
        }).show()
    }

    companion object {

        const val COLUMN_NUMBER = 2
        const val LIST = 1
        const val DETAIL = 2
        const val GRID = 3
        val colors = getListColor()
        fun getIntent(context: Context) = Intent(context, MainActivity::class.java)
    }
}
