package com.example.android.bonte_android.customViews

import android.animation.TimeInterpolator
import android.animation.ValueAnimator
import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.util.TypedValue
import android.view.View
import android.view.animation.LinearInterpolator
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import com.example.android.bonte_android.R
import kotlin.math.roundToInt


class StarLineView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    private var path = Path()
    private var paint = Paint()
    private val dashes = floatArrayOf(125f, 125f)
    private var onSky = false
    private val activity = context as AppCompatActivity

    init {

        if (!onSky) {
            activity.supportFragmentManager.findFragmentById(R.id.onboardingFragment2)

            paint = Paint().apply {
                isAntiAlias = true
                color = Color.WHITE
                style = Paint.Style.STROKE
                strokeWidth = dpToPx(5).toFloat()
                strokeCap = Paint.Cap.ROUND

            }

            path = Path().apply {
                moveTo(
                    activity.windowManager.defaultDisplay.width.toFloat() / 2,
                    (activity.windowManager.defaultDisplay.height / 2 - dpToPx(62).toFloat())
                )
                lineTo(
                    activity.windowManager.defaultDisplay.width.toFloat() / 2,
                    (activity.windowManager.defaultDisplay.height / 2 - dpToPx(115).toFloat())
                )
            }

            val lineAnim = ValueAnimator.ofFloat(100f, 0f)
            lineAnim.interpolator = LinearInterpolator()
            lineAnim.addUpdateListener {
                paint.pathEffect = DashPathEffect(dashes, lineAnim.animatedValue as Float)
                invalidate()
            }

            lineAnim.duration = 2000
            lineAnim.start()
        } else {

            paint = Paint().apply {
                isAntiAlias = true
                color = Color.WHITE
                style = Paint.Style.STROKE
                strokeWidth = dpToPx(5).toFloat()
                strokeCap = Paint.Cap.ROUND

            }

            path = Path().apply {
                moveTo(
                    activity.windowManager.defaultDisplay.width.toFloat() / 2,
                    (activity.windowManager.defaultDisplay.height / 2 - dpToPx(62).toFloat())
                )
                lineTo(
                    activity.windowManager.defaultDisplay.width.toFloat() / 2,
                    (activity.windowManager.defaultDisplay.height / 2 - dpToPx(115).toFloat())
                )
            }
        }
    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
        canvas!!.drawPath(path, paint)
    }

    private fun dpToPx(dp: Int): Int {
        return TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_DIP,
            dp.toFloat(),
            resources.displayMetrics
        ).roundToInt()
    }
}