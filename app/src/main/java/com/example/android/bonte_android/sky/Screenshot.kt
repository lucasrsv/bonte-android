package com.example.android.bonte_android.sky

import android.graphics.Bitmap
import android.os.Environment
import android.provider.MediaStore
import android.text.format.DateFormat
import android.util.Log
import android.view.View
import java.io.FileNotFoundException
import java.io.FileOutputStream
import java.io.IOException
import java.io.OutputStream
import java.util.*
import java.io.File as File1


class Screenshot {
    private fun takescreenshot(v: View): Bitmap {
        v.isDrawingCacheEnabled = true
        v.buildDrawingCache(true)
        val b = Bitmap.createBitmap(v.drawingCache)
        v.isDrawingCacheEnabled = false
        return b
    }

    fun takescreenshotOfRootView(v: View): Bitmap {
        return takescreenshot(v.rootView)
    }

    fun storeScreenshot(bitmap: Bitmap) {
        val now = Date()
        DateFormat.format("yyyy-MM-dd_hh:mm:ss", now)
        val path: String = Environment.getExternalStorageDirectory().toString() + "/" + "bontê " + now + ".jpg"
        var out: OutputStream? = null
        val imageFile = File1(path)

            out = FileOutputStream(imageFile)
            // choose JPEG format
            bitmap.compress(Bitmap.CompressFormat.PNG , 100, out)
            Log.d("tirou", path)
            out.flush()

    }
}