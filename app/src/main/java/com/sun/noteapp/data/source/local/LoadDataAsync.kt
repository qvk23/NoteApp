package com.sun.noteapp.data.source.local

import android.os.AsyncTask
import com.sun.noteapp.data.source.OnDataModifiedCallback
import java.lang.Exception

class LoadDataAsync<P, T>(
    private val handler: LocalDataHandler<P, T>,
    private val callback: OnDataModifiedCallback<T>
) : AsyncTask<P, Void, T?>() {

    private var exception: Exception? = null

    override fun doInBackground(vararg params: P): T? {
        return try {
            handler.execute(params[0])
        } catch (e: Exception) {
            exception = e
            null
        }
    }

    override fun onPostExecute(result: T?) {
        result?.let {
            callback.onSuccess(it)
        } ?: exception?.let {
            callback.onFailed(it)
        }
    }
}
