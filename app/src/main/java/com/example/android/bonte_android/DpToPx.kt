package com.example.android.bonte_android

import android.app.Activity
import android.util.TypedValue
import kotlin.math.roundToInt

fun Activity.dpToPx(dp: Int): Int {
    return TypedValue.applyDimension(
        TypedValue.COMPLEX_UNIT_DIP,
        dp.toFloat(),
        resources.displayMetrics
    ).roundToInt()
}