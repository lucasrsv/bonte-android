package com.example.android.bonte_android.customViews

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.util.Log
import android.util.TypedValue
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.example.android.bonte_android.sky.Constellation
import kotlin.math.roundToInt


class StarPath @JvmOverloads constructor(
    context:Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    private var paint = Paint()
    private val activity = context as AppCompatActivity
    private lateinit var constellations: Array<Constellation>

    init {
        paint = Paint().apply {
            isAntiAlias = true
            color = Color.WHITE
            style = Paint.Style.STROKE
            strokeCap = Paint.Cap.ROUND
            strokeWidth = 3f
            alpha = 15

        }
    }

    fun setConstellations(const: Array<Constellation>) {
        constellations = const
    }


    @SuppressLint("DrawAllocation")
    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)

        val clear = PorterDuffXfermode(PorterDuff.Mode.LIGHTEN)
        for (i in 0..4) {
            val position1 = intArrayOf(0, 0)
            val position2 = intArrayOf(0, 0)
            val sizes = floatArrayOf(0F, 0F)
            for (j in 0 until constellations[i].size) {
                for (k in 0 until constellations[i].stars[j].getNeighbor().size) {
                    constellations[i].stars[j].starImageView.getLocationOnScreen(position1)
                    constellations[i].stars[j].getNeighbor()[k].starImageView.getLocationOnScreen(position2)
                    val path = Path().apply {
/*                        if (position1[0] > position2[0] && position1[1] > position2[1]) {
                            moveTo(position1[0].toFloat() + dpToPx(9.0), position1[1].toFloat() - dpToPx(20.0))
                            lineTo(position2[0].toFloat() + dpToPx(24.0), position2[1].toFloat() - dpToPx(2.0))
                        } else if (position1[0] < position2[0] && position1[1] < position2 [1]) {
                            if ((position2[0] - position1[0] >= dpToPx(5.0)) && ((position2[1] - position1[1] > dpToPx(50.0)))) {
                                moveTo(position1[0].toFloat() + dpToPx(25.0), position1[1].toFloat() - dpToPx(2.0))
                                lineTo(position2[0].toFloat() + dpToPx(6.0), position2[1].toFloat() - dpToPx(24.0))
                            } else {
                                moveTo(position1[0].toFloat() + dpToPx(25.0), position1[1].toFloat() - dpToPx(4.0))
                                lineTo(position2[0].toFloat() + dpToPx(12.0), position2[1].toFloat() - dpToPx(24.0))
                            }
                            Log.v("tag", (dpToPx(50.0)).toString())
                             }*/
                        moveTo(position1[0].toFloat() + dpToPx(12), position1[1].toFloat() - dpToPx(10))
                        lineTo(position2[0].toFloat() + dpToPx(12), position2[1].toFloat() - dpToPx(10))
                        sizes[0] = dpToPx(4).toFloat()
                        sizes[1] = (dpToPx(4)).toFloat()
                    }
                    paint.pathEffect = DashPathEffect(sizes, dpToPx(20).toFloat())
                    paint.xfermode = clear
                    canvas!!.drawPath(path, paint)
                    constellations[i].paths.add(path)
                }
            }
        }
    }

    private fun dpToPx(dp: Int): Int {
        return TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_DIP,
            dp.toFloat(),
            resources.displayMetrics
        ).roundToInt()
    }
}