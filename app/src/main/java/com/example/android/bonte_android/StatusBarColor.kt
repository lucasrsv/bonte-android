package com.example.android.bonte_android

import android.app.Activity
import android.graphics.Color
import android.os.Build
import android.view.WindowManager

fun Activity.changeStatusBarColor() {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
        window.apply {
            statusBarColor = Color.rgb(123, 91, 217)
        }
    }
}
