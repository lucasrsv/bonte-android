package com.example.android.bonte_android

import com.google.firebase.database.FirebaseDatabase

class Firebase : android.app.Application() {
    override fun onCreate() {
        super.onCreate()
        FirebaseDatabase.getInstance().setPersistenceEnabled(true)
    }
}