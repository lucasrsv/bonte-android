package com.example.android.bonte_android.customViews

import android.animation.AnimatorSet
import android.animation.ValueAnimator
import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.view.animation.LinearInterpolator
import com.example.android.bonte_android.databinding.FragmentOnboarding2Binding
import com.example.android.bonte_android.onboarding.OnboardingFragment2


class BeamLightView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
    ) : View(context, attrs, defStyleAttr) {

    private var path = Path()
    private var paint = Paint()
    private val dashes = floatArrayOf(600f, 2000f)

    init {
        paint = Paint().apply {
            isAntiAlias = true
            color = Color.WHITE
            style = Paint.Style.STROKE
            strokeWidth = 140f
            strokeCap = Paint.Cap.ROUND
            alpha = 35
        }

        path = Path().apply {
            moveTo(width + 800f, 800f)
            lineTo(378f, 580f)
        }

        val lineAnim = ValueAnimator.ofFloat(1000f, 0f)
        lineAnim.interpolator = LinearInterpolator()
        lineAnim.addUpdateListener {
            paint.pathEffect = DashPathEffect(dashes, lineAnim.animatedValue as Float)
            invalidate()
        }
        lineAnim.duration = 5000

        val fadeAnim = ValueAnimator.ofInt(35, 100)
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


}