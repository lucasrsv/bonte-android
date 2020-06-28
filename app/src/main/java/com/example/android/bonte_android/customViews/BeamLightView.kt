package com.example.android.bonte_android.customViews

import android.animation.AnimatorSet
import android.animation.ValueAnimator
import android.content.Context
import android.content.res.Resources
import android.graphics.*
import android.util.AttributeSet
import android.util.Log
import android.util.TypedValue
import android.view.View
import android.view.animation.LinearInterpolator
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import com.example.android.bonte_android.R
import kotlinx.android.synthetic.main.activity_onboarding.*
import kotlin.math.roundToInt

@Suppress("DEPRECATION")
class BeamLightView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
    ) : View(context, attrs, defStyleAttr) {

    private var path = Path()
    private var paint = Paint()
    private val activity = context as AppCompatActivity
    private val dashes = floatArrayOf((activity.windowManager.defaultDisplay.width.toFloat()*0.65).toFloat(), dpToPx(500).toFloat())

    fun run() {

        paint = Paint().apply {
            isAntiAlias = true
            color = Color.WHITE
            style = Paint.Style.STROKE
            strokeWidth = dpToPx(70).toFloat()
            strokeCap = Paint.Cap.ROUND
        }

        path = Path().apply {
            moveTo(activity.windowManager.defaultDisplay.width.toFloat() + dpToPx(15), (activity.windowManager.defaultDisplay.height*0.645).toFloat()
            )
            lineTo(activity.windowManager.defaultDisplay.width/2.toFloat()*0.99f, activity.starOutter1.y + activity.starOutter1.height/2)
        }

        val lineAnim = ValueAnimator.ofFloat(dpToPx(392).toFloat(), dpToPx(0).toFloat())
        lineAnim.interpolator = LinearInterpolator()
        lineAnim.addUpdateListener {
            paint.pathEffect = DashPathEffect(dashes, lineAnim.animatedValue as Float)
            invalidate()
        }
        lineAnim.duration = 2000

        val fadeAnim = ValueAnimator.ofInt(0, 180)
        fadeAnim.interpolator = LinearInterpolator()
        fadeAnim.addUpdateListener {
            paint.alpha = fadeAnim.animatedValue as Int
            invalidate()
        }
        fadeAnim.duration = 3000

        AnimatorSet().apply {
            playTogether(lineAnim, fadeAnim)
            start()
        }
    }

    fun undo() {
        val lineAnim = ValueAnimator.ofFloat(dpToPx(0).toFloat(), dpToPx(392).toFloat())
        lineAnim.interpolator = LinearInterpolator()
        lineAnim.addUpdateListener {
            paint.pathEffect = DashPathEffect(dashes, lineAnim.animatedValue as Float)
            invalidate()
        }
        lineAnim.duration = 500

        val fadeAnim = ValueAnimator.ofInt(180, 0)
        fadeAnim.interpolator = LinearInterpolator()
        fadeAnim.addUpdateListener {
            paint.alpha = fadeAnim.animatedValue as Int
            invalidate()
        }
        fadeAnim.duration = 1000

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
        return (dp * resources.displayMetrics.scaledDensity).toInt()
    }
}