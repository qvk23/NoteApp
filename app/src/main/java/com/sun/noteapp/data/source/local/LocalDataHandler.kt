package com.sun.noteapp.data.source.local

interface LocalDataHandler<P, T> {
    fun execute(params: P): T
}
