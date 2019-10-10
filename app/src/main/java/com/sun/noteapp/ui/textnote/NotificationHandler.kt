package com.sun.noteapp.ui.textnote

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.graphics.Color
import android.media.RingtoneManager
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.TaskStackBuilder
import androidx.work.*
import com.sun.noteapp.R
import com.sun.noteapp.ui.todonote.ToDoNoteActivity
import com.sun.noteapp.utils.KEY_ID
import com.sun.noteapp.utils.KEY_TITLE
import com.sun.noteapp.utils.KEY_TYPE_NOTE
import com.sun.noteapp.utils.TYPE_TEXT_NOTE

class NotificationHandler(
    private val context: Context,
    workerParams: WorkerParameters
) : Worker(context, workerParams) {

    override fun doWork(): Result {
        val title = inputData.getString(KEY_TITLE)
        val id = inputData.getInt(KEY_ID, 0)
        val type = inputData.getInt(KEY_TYPE_NOTE, 0)
        sendNotification(id, title, type)
        return Result.success()
    }

    private fun sendNotification(id: Int, title: String?, type: Int) {
        val notificationManager =
            context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val name = context.getString(R.string.channel_name)
            val channel = NotificationChannel(CHANNEL_ID, name, importance)
            notificationManager.createNotificationChannel(channel)
        }
        val notification = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setVibrate(VIBRATE_ALARM)
            .setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM))
            .setLights(Color.RED, 500, 500)
            .setContentTitle(title)
            .setContentIntent(getPendingIntent(id, type))
            .setAutoCancel(true)
            .setChannelId(CHANNEL_ID)
            .build()
        notificationManager.notify(id, notification)
    }

    private fun getPendingIntent(id: Int, type: Int): PendingIntent? {
        var intent = TextNoteActivity.getIntent(context, id)
        val taskStackBuilder = TaskStackBuilder.create(context)
        if (type == TYPE_TEXT_NOTE) {
            taskStackBuilder.addParentStack(TextNoteActivity::class.java)
        }
        else {
            intent = ToDoNoteActivity.getIntent(context, id)
            taskStackBuilder.addParentStack(ToDoNoteActivity::class.java)
        }
        taskStackBuilder.addNextIntent(intent)
        return taskStackBuilder.getPendingIntent(id, PendingIntent.FLAG_UPDATE_CURRENT)
    }

    companion object {
        const val CHANNEL_ID = "123"
        val VIBRATE_ALARM = longArrayOf(1000, 1000, 1000, 1000, 1000)
    }
}
