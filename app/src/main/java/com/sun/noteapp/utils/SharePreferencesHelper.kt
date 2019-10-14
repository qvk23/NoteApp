package com.sun.noteapp.utils

import android.content.Context
import android.content.SharedPreferences
import com.sun.noteapp.data.source.local.NoteDatabase

object SharePreferencesHelper {
    private const val NAME = "AppSetting"
    private const val MODE = Context.MODE_PRIVATE
    private lateinit var preferences: SharedPreferences
    private val TYPE = Pair("type", TYPE_TEXT_NOTE)
    private val COLOR = Pair("color", NoteDatabase.DEFAULT_COLOR)
    private val SORT = Pair("sort", NoteDatabase.ORDERBY_CREATETIME)
    private val COUNT = Pair("count", DEFAULT_LIST_NOTE)
    private val REMIND = Pair("remind", IS_REMIND)

    var count: Int
        get() = preferences.getInt(COUNT.first, COUNT.second)
        set(value) = preferences.edit {
            it.putInt(COUNT.first, value)
        }
    var isRemind: Boolean
        get() = preferences.getBoolean(REMIND.first, REMIND.second)
        set(value) = preferences.edit {
            it.putBoolean(REMIND.first, value)
        }
    var type: Int
        get() = preferences.getInt(TYPE.first, TYPE.second)
        set(value) = preferences.edit {
            it.putInt(TYPE.first, value)
        }

    var color: Int
        get() = preferences.getInt(COLOR.first, COLOR.second)
        set(value) = preferences.edit {
            it.putInt(COLOR.first, value)
        }

    fun reset() {
        color = NoteDatabase.DEFAULT_COLOR
        sortType = NoteDatabase.ORDERBY_CREATETIME
    }

    @Suppress("NULLABILITY_MISMATCH_BASED_ON_JAVA_ANNOTATIONS")
    var sortType: String
        get() = preferences.getString(SORT.first, SORT.second)
        set(value) = preferences.edit {
            it.putString(SORT.first, value)
        }

    fun init(context: Context) {
        preferences = context.getSharedPreferences(NAME, MODE)
    }

    private inline fun SharedPreferences.edit(operation: (SharedPreferences.Editor) -> Unit) {
        val editor = edit()
        operation(editor)
        editor.apply()
    }
}
