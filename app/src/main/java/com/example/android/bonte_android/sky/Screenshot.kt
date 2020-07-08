package com.example.android.bonte_android.sky

import android.content.ContentValues
import android.content.Context
import android.graphics.Bitmap
import android.graphics.Path
import android.media.MediaScannerConnection
import android.os.Environment
import android.provider.MediaStore
import android.text.format.DateFormat
import android.util.Log
import android.view.View
import androidx.core.content.ContextCompat
import java.io.FileNotFoundException
import java.io.FileOutputStream
import java.io.IOException
import java.io.OutputStream
import java.lang.Exception
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

    fun storeScreenshot(bitmap: Bitmap, context: Context) : Boolean{
        val now = Date()
        DateFormat.format("yyyy-MM-dd_hh:mm:ss", now)
        val path: String = Environment.getExternalStorageDirectory().toString() + "/" + "bontê" + "/" + "bontê " + now + ".jpg"
        var out: OutputStream? = null
        val imageFile = File1(path)

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.Q) {
            val resolver = context.contentResolver
            val contentValues = ContentValues().apply {
                put(MediaStore.MediaColumns.DISPLAY_NAME, "bontê $now")
                put(MediaStore.MediaColumns.MIME_TYPE, "image/jpeg")
                put(MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_DCIM)
            }
            val uri = resolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues)

            try {
                out = uri?.let { resolver.openOutputStream(it) }
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, out)
                out!!.flush()
            } catch (e: FileNotFoundException) {
                Log.d("filenotfound", "a")
            } catch (e: IOException) {
                Log.d("ioexception", "ioexception")
            } finally {

                return try {
                    out?.close()
                    true
                } catch (exc: Exception) {
                    Log.d("finallyex", "exc")
                    false
                }

            }
        } else {

            try {
                out = FileOutputStream(imageFile)
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, out)
                out.flush()
                Log.d("trueea", "a")
            } catch (e: FileNotFoundException) {
                Log.d("filenotfound", "a")
            } catch (e: IOException) {
                Log.d("ioexcp", "a")
            } finally {

                return try {
                    out?.close()
                    Log.d("true", "a")
                    true

                } catch (exc: Exception) {
                    Log.d("exception", "a")
                    false
                }

            }
        }
    }
}