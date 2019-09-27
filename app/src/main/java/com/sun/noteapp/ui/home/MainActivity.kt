package com.sun.noteapp.ui.home


import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.core.view.GravityCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.Adapter
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.sun.noteapp.R
import com.sun.noteapp.data.model.Note
import com.sun.noteapp.data.repository.NoteLocalRepository
import com.sun.noteapp.data.source.local.LocalDataSource
import com.sun.noteapp.data.source.local.NoteDatabase
import com.sun.noteapp.utils.getScreenWidth
import com.sun.noteapp.utils.showToast
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.nav_home_screen.*

class MainActivity : AppCompatActivity(),
    MainContract.View,
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

    private var viewType = LIST

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
        drawerNavigate.addDrawerListener(toggle)
        toggle.syncState()
        setViewType(viewType)
    }

    private fun initData() {
        presenter.getAllNotes()
    }

    override fun showAllNotes(notes: List<Note>) {
        adapterVertical.updateData(notes)
        adapterVerticalWide.updateData(notes)
        adapterStaggeredGrid.updateData(notes)
    }

    override fun showNoteDetail(position: Int, note: Note) {
        showToast(note.toString())
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
        viewType = type
        when (type) {
            LIST -> setUpRecycler(linearLayoutManager, adapterVertical)
            DETAIL -> setUpRecycler(linearLayoutManager, adapterVerticalWide)
            GRID -> setUpRecycler(staggeredGridLayoutManager, adapterStaggeredGrid)
        }
    }

    companion object {

        const val COLUMN_NUMBER = 2
        const val LIST = 1
        const val DETAIL = 2
        const val GRID = 3

        @JvmStatic
        fun getIntent(context: Context) = Intent(context, MainActivity::class.java)
    }
}
