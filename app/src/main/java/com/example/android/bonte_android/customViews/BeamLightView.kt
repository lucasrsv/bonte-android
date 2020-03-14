package com.example.android.bonte_android.customViews

import android.animation.AnimatorSet
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

class BeamLightView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
    ) : View(context, attrs, defStyleAttr) {

    private var path = Path()
    private var paint = Paint()
    private val dashes = floatArrayOf(670f, 2200f)
    private val activity = context as AppCompatActivity
    private var star: ImageView
    private val res = resources

    init {
        activity.supportFragmentManager.findFragmentById(R.id.onboardingFragment2)
        star = activity.findViewById(R.id.starOffButton)

        paint = Paint().apply {
            isAntiAlias = true
            color = Color.WHITE
            style = Paint.Style.STROKE
            strokeWidth = dpToPx(70).toFloat()
            strokeCap = Paint.Cap.ROUND
        }

        path = Path().apply {
            moveTo(activity.windowManager.defaultDisplay.width.toFloat() + dpToPx(15), star.y + dpToPx(180))
            lineTo(star.x - dpToPx(10), star.y + dpToPx(16))
        }

        val lineAnim = ValueAnimator.ofFloat(1000f, 0f)
        lineAnim.interpolator = LinearInterpolator()
        lineAnim.addUpdateListener {
            paint.pathEffect = DashPathEffect(dashes, lineAnim.animatedValue as Float)
            invalidate()
        }
        lineAnim.duration = 4000

        val fadeAnim = ValueAnimator.ofInt(0, 180)
        fadeAnim.interpolator = LinearInterpolator()
        fadeAnim.addUpdateListener {
            paint.alpha = fadeAnim.animatedValue as Int
        }
        fadeAnim.duration = 5000

        AnimatorSet().apply {
            playTogether(lineAnim, fadeAnim)
            start()
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