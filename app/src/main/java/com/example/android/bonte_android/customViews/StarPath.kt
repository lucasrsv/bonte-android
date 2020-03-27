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
    private lateinit var paths: MutableList<Pair<Path, Pair<Int, Int>>>

    init {
        paint = Paint().apply {
            isAntiAlias = true
            color = Color.WHITE
            style = Paint.Style.STROKE
            strokeCap = Paint.Cap.ROUND
            strokeWidth = 3f
            alpha = 30

        }
    }

    fun setConstellations(const: Array<Constellation>) {
        constellations = const
    }


    @SuppressLint("DrawAllocation")
    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)

        for (i in 0..4) {
            val position1 = intArrayOf(0, 0)
            val position2 = intArrayOf(0, 0)
            val sizes = floatArrayOf(0F, 0F)
            for (j in 0 until constellations[i].size) {
                for (k in 0 until constellations[i].stars[j].getNeighbor().size) {
                    constellations[i].stars[j].starImageView.getLocationOnScreen(position1)
                    constellations[i].stars[j].getNeighbor()[k].starImageView.getLocationOnScreen(position2)
                    val path = Path().apply {
                        moveTo(constellations[i].stars[j].starImageView.x + dpToPx(12), constellations[i].stars[j].starImageView.y + dpToPx(12))
                        lineTo(constellations[i].stars[j].getNeighbor()[k].starImageView.x + dpToPx(12), constellations[i].stars[j].getNeighbor()[k].starImageView.y + dpToPx(12))
                        sizes[0] = dpToPx(4).toFloat()
                        sizes[1] = dpToPx(4).toFloat()
                    }
                    constellations[i].paths[j] = path
                    paint.pathEffect = DashPathEffect(sizes, dpToPx(20).toFloat())
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