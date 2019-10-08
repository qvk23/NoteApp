package com.sun.noteapp.ui.textnote

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.res.Resources
import android.media.RingtoneManager
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.work.*
import com.sun.noteapp.R
import com.sun.noteapp.utils.KEY_TITLE

class NotificationHandler(
    context: Context,
    workerParams: WorkerParameters
) : Worker(context, workerParams) {
    override fun doWork(): Result {
        val title = inputData.getString(KEY_TITLE)
        sendNotification(title)
        return Result.success()
    }

    private fun sendNotification(title: String?) {
        val notificationManager =
            applicationContext.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val name = Resources.getSystem().getString(R.string.channel_name)
            val channel = NotificationChannel(CHANNEL_ID, name, importance)
            notificationManager.createNotificationChannel(channel)
        }
        val notification = NotificationCompat.Builder(applicationContext, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setVibrate(VIBRATE_ALARM)
            .setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM))
            .setContentTitle(title)
            .setAutoCancel(true)
            .setChannelId(CHANNEL_ID)
            .build()
        notificationManager.notify(NOTIFICATION_ID, notification)
    }

    companion object {
        const val CHANNEL_ID = "123"
        const val NOTIFICATION_ID = 23
        val VIBRATE_ALARM = longArrayOf(1000, 1000, 1000, 1000, 1000)
    }
}
