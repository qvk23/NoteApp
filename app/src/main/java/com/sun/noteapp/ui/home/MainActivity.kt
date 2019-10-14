package com.sun.noteapp.ui.home

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.text.InputType
import android.text.method.PasswordTransformationMethod
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.EditText
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.core.view.GravityCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.Adapter
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.google.android.material.navigation.NavigationView
import com.sun.noteapp.R
import com.sun.noteapp.data.model.Note
import com.sun.noteapp.data.model.Note.Companion.NONE
import com.sun.noteapp.data.model.NoteOption
import com.sun.noteapp.data.repository.NoteLocalRepository
import com.sun.noteapp.data.source.local.LocalDataSource
import com.sun.noteapp.data.source.local.NoteDatabase
import com.sun.noteapp.ui.base.BaseDialog
import com.sun.noteapp.ui.home.dialog.ColorDialog
import com.sun.noteapp.ui.home.dialog.CreateNoteDialog
import com.sun.noteapp.ui.home.dialog.SortDialog
import com.sun.noteapp.ui.home.dialog.ViewDialog
import com.sun.noteapp.ui.search.SearchActivity
import com.sun.noteapp.ui.textnote.TextNoteActivity
import com.sun.noteapp.ui.todonote.SetLabelDialog
import com.sun.noteapp.ui.todonote.ToDoNoteActivity
import com.sun.noteapp.ui.trash.TrashActivity
import com.sun.noteapp.utils.*
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.nav_home_screen.*

class MainActivity : AppCompatActivity(),
    MainContract.View,
    NavigationView.OnNavigationItemSelectedListener,
    OnNoteItemClick, View.OnClickListener {
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
    private var option = NoteOption(
        NoteDatabase.DEFAULT_COLOR,
        selectedLabels,
        NoteDatabase.ORDERBY_CREATETIME,
        false
    )

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
        fabAdd.setOnClickListener(this)
        buttonClearOption.setOnClickListener(this)
        recyclerHome.itemAnimator = null
    }

    override fun onClick(item: View?) {
        when (item?.id) {
            R.id.fabAdd -> {
                CreateNoteDialog(this, object : BaseDialog.OnLoadDialogCallback<Int> {
                    override fun onSuccess(params: Int) {
                        val intent = if (params == TEXT_NOTE) {
                            TextNoteActivity.getIntent(this@MainActivity, Note.INVALID_ID)
                        } else {
                            ToDoNoteActivity.getIntent(this@MainActivity, Note.INVALID_ID)
                        }
                        startActivity(intent)
                    }
                }).show()
            }
            R.id.buttonClearOption -> {
                clearOption()
            }
        }
    }

    private fun initData() {
        SharePreferencesHelper.init(this)
        setViewType(SharePreferencesHelper.type)
        showOptionDetail(false)
        setOptionDetail()
    }

    private fun openNoteScreen(noteType: Int, id: Int) {
        if (noteType == TEXT_NOTE) {
            startActivity(TextNoteActivity.getIntent(this@MainActivity, id))
        } else {
            startActivity(ToDoNoteActivity.getIntent(this@MainActivity, id))
        }
    }

    private fun clearOption() {
        SharePreferencesHelper.let {
            it.reset()
            presenter.getAllNotesWithOption(option)
        }
        showOptionDetail(false)
    }

    private fun showOptionDetail(isShow: Boolean) {
        if (isShow) {
            textOption.visible()
            buttonClearOption.visible()
        } else {
            textOption.gone()
            buttonClearOption.gone()
        }
    }

    private fun setOptionDetail() {
        SharePreferencesHelper.let {
            if (it.color != NoteDatabase.DEFAULT_COLOR || it.sortType != NoteDatabase.ORDERBY_CREATETIME) {
                textOption.setBackgroundColor(
                    ContextCompat.getColor(this, ColorPicker.getMediumColor(it.color))
                )
                textOption.text = getString(R.string.sort)
                when (it.sortType) {
                    NoteDatabase.ORDERBY_CREATETIME -> {
                        textOption.append(getString(R.string.view_sort_by_created_time))
                    }
                    NoteDatabase.ORDERBY_REMINDTIME -> {
                        textOption.append(getString(R.string.view_sort_by_reminder_time))
                    }
                    NoteDatabase.ORDERBY_ALPHABETA -> {
                        textOption.append(getString(R.string.view_sort_by_alphabetically))
                    }
                    NoteDatabase.ORDERBY_COLOR -> {
                        textOption.append(getString(R.string.view_sort_by_color))
                    }
                    NoteDatabase.ORDERBY_MODIFYTIME -> {
                        textOption.append(getString(R.string.view_sort_by_modified_time))
                    }
                }
                showOptionDetail(true)
            }
        }
    }

    override fun noteCount(count: Int) {
        SharePreferencesHelper.count = count
    }

    override fun onResume() {
        super.onResume()
        option = NoteOption(
            SharePreferencesHelper.color,
            selectedLabels,
            SharePreferencesHelper.sortType,
            SharePreferencesHelper.isRemind
        )
        presenter.getNoteCount()
        presenter.getAllNotesWithOption(option)
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
        R.id.navAllNote -> {
            SharePreferencesHelper.isRemind = false
            option.isOnlyRemind = false
            presenter.getAllNotesWithOption(option)
            drawerNavigate.closeDrawer(GravityCompat.START)
            true
        }
        R.id.navRemindNote -> {
            SharePreferencesHelper.isRemind = true
            option.isOnlyRemind = true
            presenter.getAllNotesWithOption(option)
            drawerNavigate.closeDrawer(GravityCompat.START)
            true
        }
        else -> false
    }

    override fun showError(exception: Exception) {
        showToast(exception.toString())
    }

    override fun showNoteDetail(position: Int, note: Note) {
        if (note.password == NONE) {
            openNoteScreen(note.type, note.id)
        } else {
            val input = getInput()
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
                                    note.id
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

    private fun getInput() = EditText(this).apply {
        layoutParams = ConstraintLayout.LayoutParams(
            ConstraintLayout.LayoutParams.MATCH_CONSTRAINT,
            ConstraintLayout.LayoutParams.MATCH_CONSTRAINT
        )
        inputType = InputType.TYPE_TEXT_VARIATION_PASSWORD
        transformationMethod = PasswordTransformationMethod.getInstance()
    }

    override fun gettedLabels(labels: List<String>) {
        allLabels.clear()
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
                option.color = params
                presenter.getAllNotesWithOption(option)
                setOptionDetail()
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
        SetLabelDialog(
            this,
            allLabels,
            selectedLabels,
            true,
            getString(R.string.button_ok),
            object : SetLabelDialog.HandleAddLabelDialogEvent {
                override fun getSelectedLabels(selectedLabels: List<String>) {
                    this@MainActivity.selectedLabels.apply {
                        clear()
                        addAll(selectedLabels)
                        presenter.getAllNotesWithOption(option)
                    }
                }

                override fun addLabel() {
                    TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
                }
            }
        ).show()
    }

    private fun showSortDialog() {
        SortDialog(this, R.layout.dialog_sort, object : BaseDialog.OnLoadDialogCallback<String> {
            override fun onSuccess(params: String) {
                SharePreferencesHelper.sortType = params
                option.sortType = params
                presenter.getAllNotesWithOption(option)
                setOptionDetail()
            }
        }).show()
    }

    override fun onDestroy() {
        super.onDestroy()
        SharePreferencesHelper.isRemind = false
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
