package com.example.android.bonte_android.customViews

import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.util.TypedValue
import android.view.View
import java.nio.file.Path
import kotlin.math.roundToInt
import kotlin.properties.Delegates

class StarOffOuterView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {
    private var size by Delegates.notNull<Float>()
    private  var paint: Paint

    init {
        paint = Paint().apply {
            isAntiAlias = true
            color = Color.WHITE
            style = Paint.Style.STROKE
            pathEffect = DashPathEffect(floatArrayOf(dpToPx(21f).toFloat(), dpToPx(24f).toFloat()), 113f)
            strokeWidth = dpToPx(4.7f).toFloat()
            strokeCap = Paint.Cap.ROUND
        }
    }

    fun setRadius(size: Float) {
        this.size = size
    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
        canvas!!.drawCircle(width/2f, height/2f, dpToPx(size).toFloat(), paint)
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        if (size == 50f) {
            setMeasuredDimension(dpToPx(110f), dpToPx(110f))
        } else {
            setMeasuredDimension(dpToPx(30f), dpToPx(30f))
        }
    }

    private fun dpToPx(dp: Float): Int {
        return TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_DIP,
            dp.toFloat(),
            resources.displayMetrics
        ).roundToInt()
    }

}
