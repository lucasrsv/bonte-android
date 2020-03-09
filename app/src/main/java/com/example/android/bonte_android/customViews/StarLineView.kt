package com.example.android.bonte_android.customViews

import android.animation.ValueAnimator
import android.content.Context
import android.graphics.*
import android.media.Image
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.view.animation.LinearInterpolator
import android.widget.ImageView
import android.widget.RelativeLayout
import com.example.android.bonte_android.R
import kotlinx.android.synthetic.main.fragment_onboarding2.view.*

class StarLineView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : RelativeLayout(context, attrs, defStyleAttr) {

    interface StarView {
        fun onStar()
    }

    private var path = Path()
    private var paint = Paint()
    private val dashes = floatArrayOf(90f, 90f)
    private val view: View = inflate(context, R.layout.fragment_onboarding2, this)
    private val starOffButton = view.findViewById<ImageView>(R.id.starOffButton)
    private lateinit var mCallBack: StarView

    fun setCallBack(callback: StarView) {
        mCallBack = callback
    }

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
            moveTo(100f, 100f)
            lineTo(100f, 0f)
        }

        val lineAnim = ValueAnimator.ofFloat(100f, 0f)
        lineAnim.interpolator = LinearInterpolator()
        lineAnim.addUpdateListener {
            paint.pathEffect = DashPathEffect(dashes, lineAnim.animatedValue as Float)
            invalidate()
        }

        lineAnim.duration = 1500
        lineAnim.start()
    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
        mCallBack.onStar()
        canvas!!.drawPath(path, paint)
    }
}
