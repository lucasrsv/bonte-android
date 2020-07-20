package com.example.android.bonte_android

import android.app.Application
import android.app.Service
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.IBinder
import android.util.Log
import com.example.android.bonte_android.sky.BackgroundSongService
import com.google.firebase.database.FirebaseDatabase
import java.security.Provider

class MyApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        FirebaseDatabase.getInstance().setPersistenceEnabled(true)
    }

}