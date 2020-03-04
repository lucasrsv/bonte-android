package com.example.android.bonte_android.CustomViews

import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.View



class StarLineView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {


/*    var inflater = LayoutInflater.from(getContext())
    var view = inflater.inflate(R.layout.fragment_onboarding2, null)
    var starView = view.findViewById<ImageView>(R.id.starOffButton)*/



    private val paint = Paint().apply {
        isAntiAlias = true
        color = Color.WHITE
        style = Paint.Style.STROKE
        strokeWidth = 10f
        CornerPathEffect(5f)
    }


    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)

        val path = Path().apply {
            moveTo(width/2.toFloat(), height/2.toFloat())
            lineTo(width/2.toFloat(), height/4.toFloat())
            //moveTo(starView.pivotX, starView.top.toFloat())
            //lineTo(starView.pivotX, height.toFloat())
        }

        val measure = PathMeasure(path, false)
        val length = measure.length
        val partialPath = Path()
        measure.getSegment(0.0f, length, partialPath, true)
        partialPath.rLineTo(0.0f, 0.0f)
        canvas!!.drawPath(partialPath, paint)
    }

    val propertyProgress: PropertyValuesHolder = PropertyValuesHolder.ofInt

}
