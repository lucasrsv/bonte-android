package com.example.android.bonte_android.sky

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.example.android.bonte_android.R

class NotificationAlarmReceiver : BroadcastReceiver() {
    private var CHANNEL_ID = "1000"
    override fun onReceive(context: Context?, intent: Intent?) {
        if (intent?.action == "android.intent.action.BOOT_COMPLETED") {
            val intent1 = Intent(context, SkyActivity::class.java).apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK
            }
            val pendingIntent: PendingIntent = PendingIntent.getActivity(context, 0, intent1, 0)

            val builder: NotificationCompat.Builder? =
                context?.let {
                    NotificationCompat.Builder(it, CHANNEL_ID)
                        .setSmallIcon(R.mipmap.bonte_icon)
                        .setContentTitle("Notificação teste")
                        .setColor(Color.argb(255, 123, 91, 217))
                        .setContentText("Teste")
                        .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                        .setContentIntent(pendingIntent)
                        .setAutoCancel(true)
                }

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                val name = "Bontê notification channel"
                val descriptionText = "Bontê notification channel"
                val importance = NotificationManager.IMPORTANCE_DEFAULT
                val channel = NotificationChannel(CHANNEL_ID, name, importance).apply {
                    description = descriptionText
                }
                val notificationManager: NotificationManager? = context?.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager?
                notificationManager?.createNotificationChannel(channel)
            }

            if (builder != null) {
                with(context.let { NotificationManagerCompat.from(it) }) {
                    this.notify(1, builder.build())
                }
            }
        }
    }
}
