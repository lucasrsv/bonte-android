package com.example.android.bonte_android.sky

import android.annotation.SuppressLint
import android.graphics.Path
import android.graphics.Point
import android.os.Bundle
import android.util.Log
import android.view.Display
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.example.android.bonte_android.R
import com.example.android.bonte_android.changeStatusBarColor
import com.example.android.bonte_android.customViews.StarPath
import com.example.android.bonte_android.databinding.ActivitySkyBinding
import com.example.android.bonte_android.dpToPx
import kotlinx.android.synthetic.main.activity_sky.*
import pl.polidea.view.ZoomView

class SkyActivity : AppCompatActivity() {
    private lateinit var view: View
    private lateinit var params: ViewGroup.LayoutParams
    private lateinit var binding: ActivitySkyBinding
    private lateinit var display: Display
    private lateinit var size: Point
    private lateinit var zoomView: ZoomView
    private lateinit var constellations: Array<Constellation>
    private lateinit var const1Positions: Array<Point>
    private lateinit var const2Positions: Array<Point>
    private lateinit var const3Positions: Array<Point>
    private lateinit var const4Positions: Array<Point>
    private lateinit var const5Positions: Array<Point>
    private lateinit var paths: ArrayList<StarPath>
    private lateinit var starParams: ViewGroup.LayoutParams
    private lateinit var pathCustomView: StarPath

    @SuppressLint("ClickableViewAccessibility")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = DataBindingUtil.setContentView(
            this,
            R.layout.activity_sky
        )
        view = binding.sky
        params = view.layoutParams
        params.width = view.resources.displayMetrics.widthPixels * 2
        params.height = view.resources.displayMetrics.heightPixels * 2
        view.layoutParams = params
        pathCustomView = StarPath(this)
        binding.skyZoomLayout.setBackgroundResource(R.drawable.gradient)

        changeStatusBarColor()
        setStars()
        setPaths()


    }

    @SuppressLint("ClickableViewAccessibility")
    private fun setStars() {

        const1Positions = arrayOf(Point(dpToPx(349), dpToPx(258)), Point(dpToPx(491),dpToPx(302)), Point(dpToPx(495), dpToPx(429)), Point(dpToPx(669), dpToPx(524)), Point(dpToPx(600), dpToPx(618)))
        const2Positions = arrayOf(Point(dpToPx(84), dpToPx(516)), Point(dpToPx(173),dpToPx(584)), Point(dpToPx(227), dpToPx(647)), Point(dpToPx(408), dpToPx(678)), Point(dpToPx(256), dpToPx(762)))
        const3Positions = arrayOf(Point(dpToPx(589), dpToPx(682)), Point(dpToPx(531), dpToPx(729)), Point(dpToPx(456),dpToPx(896)), Point(dpToPx(376), dpToPx(771)), Point(dpToPx(347), dpToPx(936)),
            Point(dpToPx(271), dpToPx(969)), Point(dpToPx(533), dpToPx(1022)), Point(dpToPx(604), dpToPx(1067)))
        const4Positions = arrayOf(Point(dpToPx(387), dpToPx(1036)), Point(dpToPx(291), dpToPx(1102)), Point(dpToPx(233), dpToPx(1304)), Point(dpToPx(402), dpToPx(1416)))
        const5Positions = arrayOf(Point(dpToPx(713), dpToPx(1082)), Point(dpToPx(622), dpToPx(1249)), Point(dpToPx(527), dpToPx(1340)), Point(dpToPx(580), dpToPx(1380)))
        starParams = ViewGroup.LayoutParams(dpToPx(25), dpToPx(25))
        constellations  = arrayOf(
            Constellation(0, 5, "", "", List(5) { i -> Star(i, false, "", 0, const1Positions[i], ImageView(this))}, 6, MutableList(6) { Path() }),
            Constellation(1, 5, "", "", List(5) { i -> Star(i, false, "", 0, const2Positions[i], ImageView(this))}, 4, MutableList(4) { Path() }),
            Constellation(2, 8, "", "", List(8) { i -> Star(i, false, "", 0, const3Positions[i], ImageView(this))}, 7, MutableList(7) { Path() }),
            Constellation(3, 4, "", "", List(4) { i -> Star(i, false, "", 0, const4Positions[i], ImageView(this))}, 4, MutableList(4) { Path() }),
            Constellation(4, 4, "", "", List(4) { i -> Star(i, false, "", 0, const5Positions[i], ImageView(this))}, 3, MutableList(3) { Path() })
        )

        for (i in constellations.indices) {
            constellations[i].setImageParams(starParams)
            for (j in constellations[i].stars.indices) {
                binding.sky.addView(constellations[i].stars[j].starImageView)
                constellations[i].stars[j].starImageView.isClickable = true
                constellations[i].stars[j].starImageView.setOnTouchListener { v, event ->
                            when (event.actionMasked) {
                            MotionEvent.ACTION_DOWN -> {
                                Log.d("x", event.x.toString())
                                Log.d("star", constellations[i].stars[j].id.toString())
                                true
                            }
                            MotionEvent.ACTION_UP -> {
                                Log.d("DEBUG_TAG", "Action was UP")
                                binding.skyZoomLayout.engine.moveTo(4f, event.x, event.y, false)
                                pathCustomView
                                true
                            }
                            MotionEvent.ACTION_MOVE -> {
                                Log.d("DEBUG_TAG", "Action was MOVE")
                                true
                            }
                            MotionEvent.ACTION_CANCEL -> {
                                Log.d("DEBUG_TAG", "Action was CANCEL")
                                true
                            }
                            MotionEvent.ACTION_OUTSIDE -> {
                                Log.d(
                                    "DEBUG_TAG",
                                    "Movement occurred outside bounds of current screen element"
                                )
                                true
                            }
                            else -> super.onTouchEvent(event)
                        }
                }
            }
        }

        for (i in 0..4) {
            constellations[i].setStarsNeighbors()
        }

    }

    private fun setPaths() {
        pathCustomView.setConstellations(constellations)
        binding.sky.addView(pathCustomView)
    }

}