package com.example.android.bonte_android.customViews

import android.animation.ValueAnimator
import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.View
import android.view.animation.LinearInterpolator


class StarLineView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    private var path = Path()
    private var paint = Paint()
    private val dashes = floatArrayOf(125f, 125f)

    init {

        paint = Paint().apply {
            isAntiAlias = true
            color = Color.WHITE
            style = Paint.Style.STROKE
            strokeWidth = 10.0f
            pathEffect = CornerPathEffect(10f)
            strokeCap = Paint.Cap.ROUND

        }

        path = Path().apply {
            moveTo(315f, 472f)
            lineTo(315f, 372f)
        }

        val lineAnim = ValueAnimator.ofFloat(100f, 0f)
        lineAnim.interpolator = LinearInterpolator()
        lineAnim.addUpdateListener {
            paint.pathEffect = ComposePathEffect(DashPathEffect(dashes, lineAnim.animatedValue as Float), CornerPathEffect(10f))
            invalidate()
        }

        lineAnim.duration = 2000
        lineAnim.start()
    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
        canvas!!.drawPath(path, paint)
    }
}
