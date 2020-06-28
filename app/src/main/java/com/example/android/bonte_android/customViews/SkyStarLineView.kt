package com.example.android.bonte_android.customViews

import android.animation.ValueAnimator
import android.content.Context
import android.graphics.*
import android.renderscript.Sampler
import android.util.AttributeSet
import android.util.Log
import android.util.TypedValue
import android.view.View
import android.view.animation.LinearInterpolator
import kotlin.math.roundToInt

class SkyStarLineView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr){
    private var path = Path()
    private var paint = Paint()
    private lateinit var lineAnim: ValueAnimator
    private val dashes = floatArrayOf(dpToPx(20f), dpToPx(100f))

    fun getAnim(): ValueAnimator {
        return lineAnim
    }

    fun setValues(xCoordinate: Int, yCoordinate: Int) {
        paint = Paint().apply {
            isAntiAlias = true
            isDither = true
            color = Color.WHITE
            style = Paint.Style.STROKE
            strokeWidth = dpToPx(2.5f)
            strokeCap = Paint.Cap.ROUND
        }

        path = Path().apply {
            moveTo(xCoordinate.toFloat() + dpToPx(13f), yCoordinate.toFloat() - dpToPx(12f))
            lineTo(xCoordinate.toFloat() + dpToPx(13f), yCoordinate.toFloat() - dpToPx(42f))
        }

        lineAnim = ValueAnimator.ofFloat(100f, 0f)
        lineAnim.interpolator = LinearInterpolator()
        lineAnim.addUpdateListener {
            paint.pathEffect = DashPathEffect(dashes, lineAnim.animatedValue as Float)
            invalidate()
        }

        lineAnim.duration = 2000
        lineAnim.start()
    }

    fun undo() {
        lineAnim = ValueAnimator.ofFloat(0f, 100f)
        lineAnim.interpolator = LinearInterpolator()
        lineAnim.addUpdateListener {
            paint.pathEffect = DashPathEffect(dashes, lineAnim.animatedValue as Float)
            invalidate()
        }

        lineAnim.duration = 500
        lineAnim.start()
    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
        Log.d("stroke", paint.strokeWidth.toString())
        canvas!!.drawPath(path, paint)
    }

    private fun dpToPx(dp: Float): Float {
        return TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_DIP,
            dp.toFloat(),
            resources.displayMetrics
        )
    }
}