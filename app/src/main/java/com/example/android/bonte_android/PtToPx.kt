package com.example.android.bonte_android

import android.app.Activity
import android.util.TypedValue
import kotlin.math.roundToInt

fun Activity.ptToPx(pt: Int): Int {
    return TypedValue.applyDimension(
        TypedValue.COMPLEX_UNIT_PT,
        pt.toFloat(),
        resources.displayMetrics
    ).roundToInt()
}