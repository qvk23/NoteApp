package com.sun.noteapp.ui.search

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.widget.SearchView
import androidx.recyclerview.widget.LinearLayoutManager
import com.sun.noteapp.R
import com.sun.noteapp.data.model.Note
import com.sun.noteapp.data.repository.NoteLocalRepository
import com.sun.noteapp.data.source.local.LocalDataSource
import com.sun.noteapp.data.source.local.NoteDatabase
import com.sun.noteapp.ui.textnote.TextNoteActivity
import com.sun.noteapp.ui.todonote.ToDoNoteActivity
import com.sun.noteapp.utils.TEXT_NOTE
import com.sun.noteapp.utils.getScreenWidth
import com.sun.noteapp.utils.showToast
import kotlinx.android.synthetic.main.activity_search.*

class SearchActivity : AppCompatActivity(),
    SearchContract.View,
    SearchAdapter.OnSearchItemClick {

    private val local by lazy {
        LocalDataSource(NoteDatabase(this))
    }
    private val repository by lazy {
        NoteLocalRepository(local)
    }
    private val presenter by lazy {
        SearchPresenter(this, repository)
    }

    private val searchAdapter = SearchAdapter(getScreenWidth(), this)
    private val linearLayoutManager = LinearLayoutManager(this)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search)
        initView()
        initData()
    }

    private fun initView() {
        recyclerSearch.apply {
            adapter = searchAdapter
            layoutManager = linearLayoutManager
            itemAnimator = null
        }
        toolbarSearch.setTitle(getString(R.string.option_menu_item_search))
        setSupportActionBar(toolbarSearch)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

    }

    private fun initData() {
        presenter.getAllNotes()
    }

    private fun openNoteScreen(noteType: Int, note: Note) {
        if (noteType == TEXT_NOTE) {
            startActivity(TextNoteActivity.getIntent(this, note.id))
        } else {
            startActivity(ToDoNoteActivity.getIntent(this, note.id))
        }
    }

    override fun showNotes(notes: List<Note>) {
        searchAdapter.updateData(notes)
    }

    override fun showNoteDetail(position: Int, note: Note) {
        openNoteScreen(position, note)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.option_menu_search, menu)
        val item = menu?.findItem(R.id.option_item_search)
        val searchView = item?.actionView as SearchView
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(queryText: String?): Boolean {
                return false
            }

            override fun onQueryTextChange(queryText: String?): Boolean {
                queryText?.let {
                    presenter.searchNote(it)
                    searchAdapter.setQuerySearch(it)
                }
                return false
            }
        })
        return super.onCreateOptionsMenu(menu)
    }

    companion object {
        fun getIntent(context: Context) = Intent(context, SearchActivity::class.java)
    }
}
