package com.sun.noteapp.data.source

import java.lang.Exception

interface OnDataModifiedCallback<T> {
    fun onSuccess(data: T)
    fun onFailed(exception: Exception)
}
