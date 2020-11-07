package com.example.android.bonte_android.sky

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log

class NotificationAlarmReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent?) {
        val notification = context?.let { Notification(it) }
        notification?.createNotification()
    }
}