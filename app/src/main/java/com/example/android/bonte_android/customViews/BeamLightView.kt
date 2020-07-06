package com.example.android.bonte_android.customViews

import android.animation.AnimatorSet
import android.animation.ValueAnimator
import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.util.Log
import android.view.View
import android.view.animation.LinearInterpolator
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_onboarding.*


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
    private var timesClicked = 0
    private val lineAnimUndo = ValueAnimator.ofFloat(dpToPx(0).toFloat(), dpToPx(392).toFloat())
    private val fadeAnimUndo = ValueAnimator.ofInt(180, 0)

    fun getLineAnimUndo(): ValueAnimator? {
        return lineAnimUndo
    }

    fun getfadeAnimUndo(): ValueAnimator? {
        return fadeAnimUndo
    }

    fun run() {

        paint = Paint().apply {
            isAntiAlias = true
            color = Color.WHITE
            style = Paint.Style.STROKE
            strokeWidth = dpToPx(70).toFloat()
            strokeCap = Paint.Cap.ROUND
        }

        path = Path().apply {
            moveTo(activity.windowManager.defaultDisplay.width.toFloat(), (activity.windowManager.defaultDisplay.height*0.645).toFloat()
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
        fadeAnim.duration = 2000

        AnimatorSet().apply {
            playTogether(lineAnim, fadeAnim)
            start()
        }
    }

    fun undo() {
        if (timesClicked == 0) {
            val mMatrix = Matrix()
            val bounds = RectF()
            path.computeBounds(bounds, true)
            mMatrix.postRotate(180f, bounds.centerX(), bounds.centerY())
            path.transform(mMatrix)
            timesClicked++
        }
        lineAnimUndo.interpolator = LinearInterpolator()
        lineAnimUndo.addUpdateListener {
            paint.pathEffect = DashPathEffect(dashes, lineAnimUndo.animatedValue as Float)
            invalidate()
        }
        lineAnimUndo.duration = 500

        fadeAnimUndo.interpolator = LinearInterpolator()
        fadeAnimUndo.addUpdateListener {
            paint.alpha = fadeAnimUndo.animatedValue as Int
            invalidate()
        }
        fadeAnimUndo.duration = 1000

        AnimatorSet().apply {
            //playTogether(lineAnimUndo, fadeAnimUndo)
            play(lineAnimUndo)
            start()
        }
    }

    fun redo() {
        val lineAnimRedo = ValueAnimator.ofFloat(lineAnimUndo.animatedValue as Float, 0f)
        Log.d("lineanimredo", (lineAnimUndo.animatedValue as Float).toString())
        lineAnimRedo.interpolator = LinearInterpolator()
        lineAnimRedo.addUpdateListener {
            paint.pathEffect = DashPathEffect(dashes, lineAnimRedo.animatedValue as Float)
            invalidate()
        }
        lineAnimRedo.duration = 500
        lineAnimRedo.start()
    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
        canvas!!.drawPath(path, paint)
    }

    private fun dpToPx(dp: Int): Int {
        return (dp * resources.displayMetrics.scaledDensity).toInt()
    }
}