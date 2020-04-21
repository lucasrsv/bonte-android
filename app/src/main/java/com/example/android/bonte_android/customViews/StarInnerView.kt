package com.example.android.bonte_android.customViews

import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.util.TypedValue
import android.view.View
import java.nio.file.Path
import kotlin.math.roundToInt
import kotlin.properties.Delegates

class StarInnerView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {
    private var size by Delegates.notNull<Float>()

    private  var paint: Paint = Paint().apply {
        isAntiAlias = true
        color = Color.WHITE
        style = Paint.Style.FILL_AND_STROKE
    }


    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
        canvas!!.translate(width /2f, height /2f);
        canvas!!.drawCircle(0f, 0f, dpToPx(size).toFloat(), paint) //14f

    }

    fun setRadius(size: Float) {
        this.size = size
    }

    private fun dpToPx(dp: Float): Int {
        return TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_DIP,
            dp.toFloat(),
            resources.displayMetrics
        ).roundToInt()
    }

}
