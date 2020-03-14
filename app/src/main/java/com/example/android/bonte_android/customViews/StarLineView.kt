package com.example.android.bonte_android.customViews

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
    private val activity = context as AppCompatActivity
    private val res = resources
    private lateinit var star: ImageView

    init {

        activity.supportFragmentManager.findFragmentById(R.id.onboardingFragment2)
        star = activity.findViewById(R.id.starOffButton)

        paint = Paint().apply {
            isAntiAlias = true
            color = Color.WHITE
            style = Paint.Style.STROKE
            strokeWidth = dpToPx(5).toFloat()
            strokeCap = Paint.Cap.ROUND

        }

        path = Path().apply {
            moveTo((star.left.toFloat() + (star.right.toFloat() - star.left.toFloat())/2), star.top.toFloat())
            lineTo((star.left.toFloat() + (star.right.toFloat() - star.left.toFloat())/2), star.top.toFloat() - dpToPx(40))
        }

        val lineAnim = ValueAnimator.ofFloat(100f, 0f)
        lineAnim.interpolator = LinearInterpolator()
        lineAnim.addUpdateListener {
            paint.pathEffect = DashPathEffect(dashes, lineAnim.animatedValue as Float)
            invalidate()
        }

        lineAnim.duration = 2000
        lineAnim.start()
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