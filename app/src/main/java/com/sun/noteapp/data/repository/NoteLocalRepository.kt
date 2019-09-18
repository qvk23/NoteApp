package com.sun.noteapp.data.repository

import com.sun.noteapp.data.source.NoteDataSource
import com.sun.noteapp.data.source.local.LocalDataSource

class NoteLocalRepository(private val dataSource: LocalDataSource): NoteDataSource
