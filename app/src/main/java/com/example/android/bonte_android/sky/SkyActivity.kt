package com.example.android.bonte_android.sky

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.animation.PropertyValuesHolder
import android.annotation.SuppressLint
import android.content.ContentValues
import android.graphics.Point
import android.os.Bundle
import android.util.Log
import android.view.MotionEvent
import android.view.ScaleGestureDetector
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.animation.doOnEnd
import androidx.core.animation.doOnStart
import androidx.databinding.DataBindingUtil
import com.example.android.bonte_android.*
import com.example.android.bonte_android.R
import com.example.android.bonte_android.customViews.SkyStarLineView
import com.example.android.bonte_android.customViews.StarPathView
import com.example.android.bonte_android.databinding.ActivitySkyBinding
import com.google.firebase.database.*
import kotlinx.android.synthetic.main.activity_sky.*
import kotlin.random.Random

class SkyActivity : AppCompatActivity() {
    private lateinit var view: View
    private lateinit var params: ViewGroup.LayoutParams
    private lateinit var binding: ActivitySkyBinding
    private lateinit var constellations: Array<Constellation>
    private lateinit var const1Positions: Array<Point>
    private lateinit var const2Positions: Array<Point>
    private lateinit var const3Positions: Array<Point>
    private lateinit var const4Positions: Array<Point>
    private lateinit var const5Positions: Array<Point>
    private lateinit var starParams: LinearLayout.LayoutParams
    private lateinit var pathCustomView: StarPathView
    private lateinit var starClicked: Pair<Int, Int>
    private lateinit var scaleListener: ScaleGestureDetector.SimpleOnScaleGestureListener
    private lateinit var scaleDetector: ScaleGestureDetector
    private lateinit var starLineView:  SkyStarLineView
    private lateinit var starRectangle: ImageView
    private lateinit var actionText: TextView
    private lateinit var actionDot: ImageView
    private lateinit var language: String
    private var database: DatabaseReference = FirebaseDatabase.getInstance().reference
    private var numClicks = 0
    private var isStarClicked = false

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
        actionText = binding.actionText
        actionDot = ImageView(this)
        actionDot.setImageResource(R.drawable.star_circle)
        actionDot.layoutParams = LinearLayout.LayoutParams(dpToPx(3), dpToPx(3))
        actionDot.visibility = View.INVISIBLE
        sky.addView(actionDot)
        pathCustomView = StarPathView(this)

        scaleListener = object : ScaleGestureDetector.SimpleOnScaleGestureListener() {
            override fun onScale(detector: ScaleGestureDetector?): Boolean {

                numClicks = 0
                val pvhX: PropertyValuesHolder
                val pvhY: PropertyValuesHolder
                val pvhXBright: PropertyValuesHolder
                val pvhYBright: PropertyValuesHolder
                val pvhXLine =  PropertyValuesHolder.ofFloat(View.SCALE_X, 1f, 0f)
                val pvhYLine =  PropertyValuesHolder.ofFloat(View.SCALE_Y, 1f, 0f)
                actionText.visibility = View.INVISIBLE
                actionText.text = ""
                actionDot.visibility = View.INVISIBLE
                sky.removeView(starLineView)
                sky.removeView(starRectangle)
                skyZoomLayout.engine.setScrollEnabled(true)
                skyZoomLayout.engine.setFlingEnabled(true)

                if (!constellations[starClicked.first].stars[starClicked.second].done) {
                    pvhX = PropertyValuesHolder.ofFloat(View.SCALE_X, 2f, 1f)
                    pvhY = PropertyValuesHolder.ofFloat(View.SCALE_Y, 2f, 1f)
                    pvhXBright = PropertyValuesHolder.ofFloat(View.SCALE_X, 3f, 1.2f)
                    pvhYBright = PropertyValuesHolder.ofFloat(View.SCALE_Y, 3f, 1.2f)

                } else if (constellations[starClicked.first].stars[starClicked.second].done && constellations[starClicked.first].stars[starClicked.second].intermediate) {
                    pvhX = PropertyValuesHolder.ofFloat(View.SCALE_X, 3f, 1.0f)
                    pvhY = PropertyValuesHolder.ofFloat(View.SCALE_Y, 3f, 1.0f)
                    pvhXBright = PropertyValuesHolder.ofFloat(View.SCALE_X, 4f, 1.2f)
                    pvhYBright = PropertyValuesHolder.ofFloat(View.SCALE_Y, 4f, 1.2f)
                } else {
                    pvhX = PropertyValuesHolder.ofFloat(View.SCALE_X, 3f, 1.2f)
                    pvhY = PropertyValuesHolder.ofFloat(View.SCALE_Y, 3f, 1.2f)
                    pvhXBright = PropertyValuesHolder.ofFloat(View.SCALE_X, 4f, 1.4f)
                    pvhYBright = PropertyValuesHolder.ofFloat(View.SCALE_Y, 4f, 1.4f)
                }

                    val scaleButton  = ObjectAnimator.ofPropertyValuesHolder(
                        constellations[starClicked.first].stars[starClicked.second].starViews[7],
                        pvhX,
                        pvhY
                    ).apply {
                        duration = 500
                    }
                    val scaleInner = ObjectAnimator.ofPropertyValuesHolder(
                        constellations[starClicked.first].stars[starClicked.second].starViews[0],
                        pvhX,
                        pvhY
                    ).apply {
                        duration = 500
                    }
                    val scaleMid = ObjectAnimator.ofPropertyValuesHolder(
                        constellations[starClicked.first].stars[starClicked.second].starViews[1],
                        pvhX,
                        pvhY
                    ).apply {
                        duration = 500
                    }
                    val scaleMid2 = ObjectAnimator.ofPropertyValuesHolder(
                        constellations[starClicked.first].stars[starClicked.second].starViews[3],
                        pvhX,
                        pvhY
                    ).apply {
                        duration = 500
                    }
                    val scaleOutter1 = ObjectAnimator.ofPropertyValuesHolder(
                        constellations[starClicked.first].stars[starClicked.second].starViews[2],
                        pvhX,
                        pvhY
                    ).apply {
                        duration = 500
                    }
                    val scaleOutter2 = ObjectAnimator.ofPropertyValuesHolder(
                        constellations[starClicked.first].stars[starClicked.second].starViews[4],
                        pvhX,
                        pvhY
                    ).apply {
                        duration = 500
                    }
                    val scaleOutter3 = ObjectAnimator.ofPropertyValuesHolder(
                        constellations[starClicked.first].stars[starClicked.second].starViews[6],
                        pvhX,
                        pvhY
                    ).apply {
                        duration = 500
                    }
                    val scaleBright = ObjectAnimator.ofPropertyValuesHolder(
                        constellations[starClicked.first].stars[starClicked.second].starViews[5],
                        pvhXBright,
                        pvhYBright
                    ).apply {
                        duration = 500
                    }
                    val scaleLine = ObjectAnimator.ofPropertyValuesHolder(starLineView, pvhXLine, pvhYLine).apply {
                        duration = 500
                    }

                    val pvhR = PropertyValuesHolder.ofFloat(View.ROTATION, -11f, 0f)
                    val rotateAnim = ObjectAnimator.ofPropertyValuesHolder(
                        constellations[starClicked.first].stars[starClicked.second].starViews[2],
                        pvhR
                    ).apply {
                        duration = 500
                    }

                    if (!constellations[starClicked.first].stars[starClicked.second].intermediate && !constellations[starClicked.first].stars[starClicked.second].done) {
                        AnimatorSet().apply {
                            playTogether(
                                scaleButton,
                                scaleInner,
                                scaleMid,
                                scaleOutter1,
                                scaleOutter2,
                                scaleBright,
                                //scaleLine
                                rotateAnim
                            )
                            start()
                        }
                    } else if (!constellations[starClicked.first].stars[starClicked.second].intermediate && constellations[starClicked.first].stars[starClicked.second].done
                        || constellations[starClicked.first].stars[starClicked.second].intermediate && !constellations[starClicked.first].stars[starClicked.second].done
                        || constellations[starClicked.first].stars[starClicked.second].intermediate && constellations[starClicked.first].stars[starClicked.second].done) {
                        constellations[starClicked.first].stars[starClicked.second].starViews[2].isClickable = true
                        AnimatorSet().apply {
                            playTogether(
                                scaleButton,
                                scaleInner,
                                scaleMid,
                                scaleOutter1,
                                scaleOutter2,
                                scaleMid2,
                                scaleBright,
                                scaleOutter3
                            )
                            start()
                        }
                    }

                for (i in constellations.indices) {
                    for (j in constellations[i].stars.indices) {
                        if (i == starClicked.first && j == starClicked.second) {

                        } else {

                            val alphaInner: ObjectAnimator = ObjectAnimator.ofFloat(
                                constellations[i].stars[j].starViews[0],
                                "alpha",
                                0.3f,
                                1.0f
                            ).apply {
                                duration = 500
                            }
                            val alphaMid: ObjectAnimator = ObjectAnimator.ofFloat(
                                constellations[i].stars[j].starViews[1],
                                "alpha",
                                0.09f,
                                0.3f
                            ).apply {
                                duration = 500
                            }
                            val alphaOutter1: ObjectAnimator = ObjectAnimator.ofFloat(
                                constellations[i].stars[j].starViews[2],
                                "alpha",
                                0.3f,
                                1.0f
                            ).apply {
                                duration = 500
                            }
                            val alphaOutter2: ObjectAnimator = ObjectAnimator.ofFloat(
                                constellations[i].stars[j].starViews[4],
                                "alpha",
                                0.3f,
                                1.0f
                            ).apply {
                                duration = 500
                            }
                            AnimatorSet().apply {
                                playTogether(alphaInner, alphaMid, alphaOutter1, alphaOutter2)
                                start()
                            }
                        }
                    }
                }

                isStarClicked = false
                sky.addView(pathCustomView)
                return true
            }
        }
        scaleDetector = ScaleGestureDetector(this, scaleListener)

        skyZoomLayout.setOnTouchListener { _, event ->
            Log.d("star", isStarClicked.toString())
            if (isStarClicked) {
                scaleDetector.onTouchEvent(event)

            }

            false
        }

        binding.skyZoomLayout.setBackgroundResource(R.drawable.gradient)

        changeStatusBarColor()
        setConstellations()
        setStars()
        setPaths()
        touchListener()
        longPressListener()
        setParticles()
    }

    @SuppressLint("ClickableViewAccessibility")
    private fun touchListener() {
        for (i in constellations.indices) {
            for (j in constellations[i].stars.indices) {
                constellations[i].stars[j].starViews[7].setOnTouchListener { _, event ->
                    Log.d("starTouched", isStarClicked.toString())
                    if (!isStarClicked) {
                        when (event.actionMasked) {
                            MotionEvent.ACTION_DOWN -> {
                                true
                            }
                            MotionEvent.ACTION_UP -> {
                                numClicks++
                                starClicked = Pair(i, j)
                                isStarClicked = true
                                skyZoomLayout.setAnimationDuration(2000)
                                skyZoomLayout.engine.moveTo(
                                    5f,
                                    (constellations[i].stars[j].starViews[2].x) * -1 + view.layoutParams.width*0.0865.toFloat(),
                                    (constellations[i].stars[j].starViews[2].y) * -1 + view.layoutParams.height*0.090.toFloat(),
                                    true
                                )
                                skyZoomLayout.setAnimationDuration(280)
                                skyZoomLayout.engine.setScrollEnabled(false)
                                skyZoomLayout.engine.setFlingEnabled(false)

                                var pvhX = PropertyValuesHolder.ofFloat(View.SCALE_X, 1f, 2f)
                                var pvhY = PropertyValuesHolder.ofFloat(View.SCALE_Y, 1f, 2f)
                                var pvhBrightX = PropertyValuesHolder.ofFloat(View.SCALE_X, 1.2f, 3.0f)
                                var pvhBrightY = PropertyValuesHolder.ofFloat(View.SCALE_Y, 1.2f, 3.0f)

                                if (constellations[i].stars[j].done && !constellations[i].stars[j].intermediate) {
                                    pvhX = PropertyValuesHolder.ofFloat(View.SCALE_X, 1.2f, 3.0f)
                                    pvhY = PropertyValuesHolder.ofFloat(View.SCALE_Y, 1.2f, 3.0f)
                                    pvhBrightX = PropertyValuesHolder.ofFloat(View.SCALE_X, 1.4f, 4.0f)
                                    pvhBrightY= PropertyValuesHolder.ofFloat(View.SCALE_Y, 1.4f, 4.0f)

                                } else if (constellations[i].stars[j].done && constellations[i].stars[j].intermediate) {
                                    pvhX = PropertyValuesHolder.ofFloat(View.SCALE_X, 1.0f, 3.0f)
                                    pvhY = PropertyValuesHolder.ofFloat(View.SCALE_Y, 1.0f, 3.0f)
                                    pvhBrightX = PropertyValuesHolder.ofFloat(View.SCALE_X, 1.2f, 4.0f)
                                    pvhBrightY= PropertyValuesHolder.ofFloat(View.SCALE_Y, 1.2f, 4.0f)
                                }

                                val fadeText = ObjectAnimator.ofFloat(actionText, "alpha", 0f, 1f).apply {
                                    duration = 1000
                                    startDelay = 2000
                                    doOnStart {
                                        actionText.visibility = View.VISIBLE
                                    }
                                }
                                val fadeDot = ObjectAnimator.ofFloat(actionDot, "alpha", 0f, 1f).apply {
                                    duration = 1000
                                    startDelay = 2000
                                    doOnStart {
                                        actionDot.visibility = View.VISIBLE
                                    }
                                }

                                val scaleButton = ObjectAnimator.ofPropertyValuesHolder(constellations[i].stars[j].starViews[7], pvhX, pvhY).apply {
                                    duration = 1000
                                }

                                val scaleInner = ObjectAnimator.ofPropertyValuesHolder(constellations[i].stars[j].starViews[0], pvhX, pvhY).apply {
                                    duration = 1000
                                }

                                val scaleMid = ObjectAnimator.ofPropertyValuesHolder(constellations[i].stars[j].starViews[1], pvhX, pvhY).apply {
                                    duration = 1000
                                }

                                val scaleMid2 = ObjectAnimator.ofPropertyValuesHolder(constellations[i].stars[j].starViews[3], pvhX, pvhY).apply {
                                    duration = 1000
                                }
                                val scaleOutter1 = ObjectAnimator.ofPropertyValuesHolder(constellations[i].stars[j].starViews[2], pvhX, pvhY).apply {
                                    duration = 1000
                                }
                                val scaleOutter2 = ObjectAnimator.ofPropertyValuesHolder(constellations[i].stars[j].starViews[4], pvhX, pvhY).apply {
                                    duration = 1000
                                }
                                val scaleOutter3 = ObjectAnimator.ofPropertyValuesHolder(constellations[i].stars[j].starViews[6], pvhX, pvhY).apply {
                                    duration = 1000
                                }
                                val scaleBright = ObjectAnimator.ofPropertyValuesHolder(constellations[i].stars[j].starViews[5], pvhBrightX, pvhBrightY).apply {
                                    duration = 1000
                                }
                                val pvhR = PropertyValuesHolder.ofFloat(View.ROTATION, 0f, -11f)

                                val rotateAnim = ObjectAnimator.ofPropertyValuesHolder(constellations[i].stars[j].starViews[2], pvhR). apply {
                                    duration = 1000
                                }

                                actionText.x = constellations[i].stars[j].position.x - dpToPx(26).toFloat()
                                actionText.y = constellations[i].stars[j].position.y - dpToPx(70).toFloat()
                                actionDot.x = constellations[i].stars[j].position.x.toFloat() + dpToPxF(11.3f)
                                actionDot.y = constellations[i].stars[j].position.y.toFloat() - dpToPx(75)
                                val constellation = if (i == 0) {
                                    "volans"
                                } else if (i == 1) {
                                    "cancer"
                                } else if (i == 2){
                                    "aquila"
                                } else if (i == 3) {
                                    "equuleus"
                                } else {
                                    "sagitta"
                                }
                                val star = "star" + (j+1).toString()
                                setTexts(constellation, star)

                                if (!constellations[i].stars[j].done && !constellations[i].stars[j].intermediate) {
                                    starLineView.setValues(constellations[i].stars[j].position.x, constellations[i].stars[j].position.y)
                                    sky.addView(starLineView)
                                    AnimatorSet().apply {
                                        playTogether(scaleButton, scaleInner, scaleMid, scaleOutter1, scaleOutter2, rotateAnim, scaleOutter3, fadeText, fadeDot)
                                        start()
                                    }
                                } else if (constellations[i].stars[j].done && !constellations[i].stars[j].intermediate) {
                                    AnimatorSet().apply {
                                        playTogether(scaleButton, scaleInner, scaleMid, scaleOutter3, scaleMid2, scaleBright, fadeText, fadeDot)
                                        start()
                                    }
                                } else if (!constellations[i].stars[j].done && constellations[i].stars[j].intermediate) {
                                    starLineView.setValues(constellations[i].stars[j].position.x, constellations[i].stars[j].position.y)
                                    sky.addView(starLineView)
                                    AnimatorSet().apply {
                                        playTogether(scaleButton, scaleInner, scaleMid, scaleOutter1, scaleOutter2, fadeText, fadeDot)
                                        start()
                                    }
                                } else if (constellations[i].stars[j].done && constellations[i].stars[j].intermediate) {
                                    AnimatorSet().apply {
                                        playTogether(scaleButton, scaleInner, scaleMid, scaleMid2, scaleOutter3, scaleBright, fadeText, fadeDot)
                                        start()
                                    }
                                }
                                binding.sky.removeView(pathCustomView)

                                for (a in constellations.indices) {
                                    for (b in constellations[a].stars.indices) {
                                        if (a == i && b == j) {

                                        } else {
                                            val alphaInner: ObjectAnimator = ObjectAnimator.ofFloat(constellations[a].stars[b].starViews[0], "alpha", 1.0f, 0.3f).apply {
                                                duration = 1000
                                            }
                                            val alphaMid: ObjectAnimator = ObjectAnimator.ofFloat(constellations[a].stars[b].starViews[1], "alpha", 0.3f, 0.09f).apply {
                                                duration = 1000
                                            }
                                            val alphaOutter1: ObjectAnimator = ObjectAnimator.ofFloat(constellations[a].stars[b].starViews[2], "alpha", 1.0f, 0.3f).apply {
                                                duration = 1000
                                            }
                                            val alphaOutter2: ObjectAnimator = ObjectAnimator.ofFloat(constellations[a].stars[b].starViews[4], "alpha", 1.0f, 0.3f).apply {
                                                duration = 1000
                                            }
                                            AnimatorSet().apply {
                                                playTogether(alphaInner, alphaMid, alphaOutter1, alphaOutter2)
                                                start()
                                            }
                                        }
                                    }
                                }
                                true
                            }
                            else -> super.onTouchEvent(event)
                        }
                    } else {
                        //Caso seja o 2o toque
                        when (event.actionMasked) {
                            MotionEvent.ACTION_UP -> {
                                Log.d("entrouaqui", "aa")
                                if (starClicked.first == i && starClicked.second == j && numClicks == 1 && !constellations[i].stars[j].intermediate && !constellations[i].stars[j].done) {
                                    numClicks++
                                    constellations[i].stars[j].starViews[4].visibility = View.VISIBLE

                                    val pvhR = PropertyValuesHolder.ofFloat(View.ROTATION, -11f, -35f)
                                    val rotateAnim = ObjectAnimator.ofPropertyValuesHolder(constellations[i].stars[j].starViews[4], pvhR).apply {
                                        duration = 1000
                                    }
                                    AnimatorSet().apply {
                                        play(rotateAnim)
                                        start()
                                    }
                                    constellations[i].stars[j].intermediate = true

                                } else if (starClicked.first == i && starClicked.second == j && numClicks == 1 &&
                                    !constellations[i].stars[j].intermediate && constellations[i].stars[j].done) {
                                    constellations[i].stars[j].intermediate = true
                                    Log.d("numclick", numClicks.toString())
                                    numClicks++
                                    val x = PropertyValuesHolder.ofFloat(View.SCALE_X, 0f, 3.1f)
                                    val y = PropertyValuesHolder.ofFloat(View.SCALE_Y, 0f, 3.1f)
                                    val scaleOutterIntermediary = ObjectAnimator.ofPropertyValuesHolder(constellations[i].stars[j].starViews[6], x, y).apply {
                                        duration = 1000
                                        var isVisible = false
                                        addUpdateListener { animation ->
                                            if (animation.animatedValue as Float > 0f && !isVisible) {
                                                isVisible = true
                                                constellations[i].stars[j].starViews[6].visibility = View.VISIBLE
                                            }
                                        }
                                    }
                                    scaleOutterIntermediary.start()
                                }
                            }
                        }
                        super.onTouchEvent(event)
                    }
                }

            }
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    private fun longPressListener() {
        for (i in constellations.indices) {
            for (j in constellations[i].stars.indices) {
                constellations[i].stars[j].starViews[7].setOnLongClickListener {
                    if (isStarClicked) {
                        Log.d("click", "cliick")
                        Log.d("status", constellations[i].stars[j].intermediate.toString())
                        Log.d("done", constellations[i].stars[j].done.toString())
                        if (constellations[i].stars[j].intermediate) {
                            numClicks = 5 //Pra que o Motion Event UP seja detectado na função do onTouch!

                            var xMid2 = PropertyValuesHolder.ofFloat(View.SCALE_X, 0f, 4f)
                            var yMid2 = PropertyValuesHolder.ofFloat(View.SCALE_Y, 0f, 4f)
                            var xGeneral = PropertyValuesHolder.ofFloat(View.SCALE_X, 2f, 4f)
                            var yGeneral = PropertyValuesHolder.ofFloat(View.SCALE_Y, 2f, 4f)
                            var xOutter = PropertyValuesHolder.ofFloat(View.SCALE_X, 2f, 4.1f)
                            var yOutter = PropertyValuesHolder.ofFloat(View.SCALE_Y, 2f, 4.1f)
                            var xBright = PropertyValuesHolder.ofFloat(View.SCALE_X, 0f, 6f)
                            var yBright = PropertyValuesHolder.ofFloat(View.SCALE_Y, 0f, 6f)
                            var delay = 0L
                            var time = 2000L


                            if (!constellations[i].stars[j].done) {
                                delay = 750L
                            } else if (constellations[i].stars[j].intermediate && !constellations[i].stars[j].done) {
                                delay = 1250L
                            }

                            if (constellations[i].stars[j].done) {
                                xMid2 = PropertyValuesHolder.ofFloat(View.SCALE_X, 3f, 4f)
                                yMid2 = PropertyValuesHolder.ofFloat(View.SCALE_Y, 3f, 4f)
                                xGeneral = PropertyValuesHolder.ofFloat(View.SCALE_X, 3f, 4f)
                                yGeneral = PropertyValuesHolder.ofFloat(View.SCALE_Y, 3f, 4f)
                                xOutter = PropertyValuesHolder.ofFloat(View.SCALE_X, 3.1f, 4.1f)
                                yOutter = PropertyValuesHolder.ofFloat(View.SCALE_Y, 3.1f, 4.1f)
                                xBright = PropertyValuesHolder.ofFloat(View.SCALE_X, 4f, 6f)
                                yBright = PropertyValuesHolder.ofFloat(View.SCALE_Y, 4f, 6f)
                                time = 1000L
                            }

                            val scaleInner = ObjectAnimator.ofPropertyValuesHolder(
                                constellations[i].stars[j].starViews[0],
                                xGeneral,
                                yGeneral
                            ).apply {
                                duration = 1000
                                startDelay = delay
                            }
                            val scaleMid = ObjectAnimator.ofPropertyValuesHolder(
                                constellations[i].stars[j].starViews[1],
                                xGeneral,
                                yGeneral
                            ).apply {
                                duration = 1000
                                startDelay = delay
                            }
                            val scaleMid2 = ObjectAnimator.ofPropertyValuesHolder(
                                constellations[i].stars[j].starViews[3],
                                xMid2,
                                yMid2
                            ).apply {
                                duration = time
                                startDelay = 0
                            }
                            val scaleOutter = ObjectAnimator.ofPropertyValuesHolder(
                                constellations[i].stars[j].starViews[2],
                                xOutter,
                                yOutter
                            ).apply {
                                duration = 1100
                                startDelay = delay
                            }
                            val scaleOutter2 = ObjectAnimator.ofPropertyValuesHolder(
                                constellations[i].stars[j].starViews[4],
                                xOutter,
                                yOutter
                            ).apply {
                                duration = 1100
                                startDelay = delay
                            }
                            val scaleIntermediaryOutter = ObjectAnimator.ofPropertyValuesHolder(
                                constellations[i].stars[j].starViews[6],
                                xOutter,
                                yOutter
                            ).apply {
                                duration = 1000
                                startDelay = delay
                            }
                            val scaleBright = ObjectAnimator.ofPropertyValuesHolder(
                                constellations[i].stars[j].starViews[5],
                                xBright,
                                yBright
                            ).apply {
                                duration = 1000
                                startDelay = delay
                                var turnedVisible = false
                                addUpdateListener { animation ->
                                    if (animation.animatedValue as Float > 0f && !turnedVisible) {
                                        turnedVisible = true
                                        constellations[i].stars[j].starViews[5].visibility = View.VISIBLE
                                    }
                                }
                            }

                            //Retornar ao scale antigo

                            val xReturn = PropertyValuesHolder.ofFloat(View.SCALE_X, 4f, 3f)
                            val yReturn = PropertyValuesHolder.ofFloat(View.SCALE_Y, 4f, 3f)
                            val outterXReturn =
                                PropertyValuesHolder.ofFloat(View.SCALE_X, 4.1f, 1.5f)
                            val outterYReturn =
                                PropertyValuesHolder.ofFloat(View.SCALE_Y, 4.1f, 1.5f)
                            val xBrightReturn = PropertyValuesHolder.ofFloat(View.SCALE_X, 6f, 4f)
                            val yBrightReturn = PropertyValuesHolder.ofFloat(View.SCALE_Y, 6f, 4f)

                            val scaleDownOutter = ObjectAnimator.ofPropertyValuesHolder(
                                constellations[i].stars[j].starViews[2],
                                outterXReturn,
                                outterYReturn
                            ).apply {
                                duration = 250
                                doOnEnd {
                                    constellations[i].stars[j].starViews[2].visibility = View.GONE
                                    constellations[i].stars[j].starViews[4].visibility = View.GONE
                                }

                            }
                            val scaleDownOutter2 = ObjectAnimator.ofPropertyValuesHolder(
                                constellations[i].stars[j].starViews[4],
                                outterXReturn,
                                outterYReturn
                            ).apply {
                                duration = 250

                            }
                            val scaleDownInner = ObjectAnimator.ofPropertyValuesHolder(
                                constellations[i].stars[j].starViews[0],
                                xReturn,
                                yReturn
                            ).apply {
                                duration = 1000
                                startDelay = 500
                                doOnEnd {
                                    sky.removeView(starLineView)
                                    sky.addView(starRectangle)
                                    val fadeRectangle = ObjectAnimator.ofFloat(starRectangle, "alpha", 0f, 0.06f)
                                    fadeRectangle.duration = 500
                                    starRectangle.x = constellations[i].stars[j].position.x.toFloat() - view.resources.displayMetrics.widthPixels*0.09f
                                    starRectangle.y = constellations[i].stars[j].position.y.toFloat() + view.resources.displayMetrics.heightPixels*0.17f
                                    starRectangle.visibility = View.VISIBLE
                                    fadeRectangle.start()
                                }
                            }
                            val scaleDownMid = ObjectAnimator.ofPropertyValuesHolder(
                                constellations[i].stars[j].starViews[1],
                                xReturn,
                                yReturn
                            ).apply {
                                duration = 1000
                                startDelay = 500
                            }
                            val scaleDownMid2 = ObjectAnimator.ofPropertyValuesHolder(
                                constellations[i].stars[j].starViews[3],
                                xReturn,
                                yReturn
                            ).apply {
                                duration = 1000
                                startDelay = 500
                            }
                            val scaleDownBright = ObjectAnimator.ofPropertyValuesHolder(
                                constellations[i].stars[j].starViews[5],
                                xBrightReturn,
                                yBrightReturn
                            ).apply {
                                duration = 1000
                                startDelay = 500
                            }
                            val scaleDownIntermediaryOutter = ObjectAnimator.ofPropertyValuesHolder(
                                constellations[i].stars[j].starViews[6],
                                outterXReturn,
                                outterYReturn
                            ).apply {
                                duration = 250
                                doOnEnd {
                                    constellations[i].stars[j].starViews[6].visibility =
                                        View.INVISIBLE
                                }

                            }

                            val alphaMid2 = ObjectAnimator.ofFloat(
                                constellations[i].stars[j].starViews[3],
                                "alpha",
                                constellations[i].stars[j].starViews[3].alpha,
                                constellations[i].stars[j].starViews[3].alpha + 0.1f
                            ).apply {
                                duration = 1000

                            }

                            if (!constellations[i].stars[j].done) {
                                starLineView.undo()
                                constellations[i].stars[j].starViews[3].visibility = View.VISIBLE
                                AnimatorSet().apply {
                                    playTogether(
                                        scaleMid2,
                                        scaleInner,
                                        scaleMid,
                                        scaleOutter,
                                        scaleOutter2,
                                        scaleBright
                                    )
                                    playTogether(scaleDownOutter, scaleDownOutter2)
                                    play(scaleDownOutter).after(scaleBright)
                                    playTogether(
                                        scaleDownInner,
                                        scaleDownMid,
                                        scaleDownMid2,
                                        scaleDownBright
                                    )
                                    play(scaleDownInner).after(scaleDownOutter)
                                    start()
                                }

                            } else {
                                AnimatorSet().apply {
                                    if (constellations[i].stars[j].timesCompleted < 3) {
                                        playTogether(
                                            scaleInner,
                                            scaleMid,
                                            scaleMid2,
                                            scaleIntermediaryOutter,
                                            scaleBright,
                                            alphaMid2
                                        )
                                    } else {
                                        playTogether(
                                            scaleInner,
                                            scaleMid,
                                            scaleMid2,
                                            scaleIntermediaryOutter,
                                            scaleBright
                                        )
                                    }
                                       playTogether(
                                            scaleDownInner,
                                            scaleDownMid,
                                            scaleDownMid2,
                                            scaleDownIntermediaryOutter,
                                            scaleDownBright
                                        )
                                    playSequentially(scaleInner, scaleDownInner)
                                    start()
                                }
                            }

                            constellations[i].stars[j].done = true
                            constellations[i].stars[j].intermediate = false
                            if (constellations[i].stars[j].timesCompleted <= 2) {
                                constellations[i].stars[j].timesCompleted++
                            }
                            return@setOnLongClickListener true
                        }
                    }
                    return@setOnLongClickListener true
                }
            }
        }
    }

    private fun setConstellations() {
        const1Positions = arrayOf(
            Point((view.layoutParams.width*0.379).toInt(), (view.layoutParams.height*0.18).toInt()),
            Point((view.layoutParams.width*0.534).toInt(), (view.layoutParams.height*0.202).toInt()),
            Point((view.layoutParams.width*0.539).toInt(), (view.layoutParams.height*0.266).toInt()),
            Point((view.layoutParams.width*0.727).toInt(), (view.layoutParams.height*0.314).toInt()),
            Point((view.layoutParams.width*0.652).toInt(), (view.layoutParams.height*0.36).toInt())
        )

        const2Positions = arrayOf(
            Point((view.layoutParams.width*0.092).toInt(), (view.layoutParams.height*0.309).toInt()),
            Point((view.layoutParams.width*0.188).toInt(), (view.layoutParams.height*0.344).toInt()),
            Point((view.layoutParams.width*0.246).toInt(), (view.layoutParams.height*0.375).toInt()),
            Point((view.layoutParams.width*0.442).toInt(), (view.layoutParams.height*0.391).toInt()),
            Point((view.layoutParams.width*0.278).toInt(), (view.layoutParams.height*0.433).toInt())
        )

        const3Positions = arrayOf(
            Point((view.layoutParams.width*0.64).toInt(), (view.layoutParams.height*0.394).toInt()),
            Point((view.layoutParams.width*0.577).toInt(), (view.layoutParams.height*0.416).toInt()),
            Point((view.layoutParams.width*0.495).toInt(), (view.layoutParams.height*0.50).toInt()),
            Point((view.layoutParams.width*0.408).toInt(), (view.layoutParams.height*0.438).toInt()),
            Point((view.layoutParams.width*0.377).toInt(), (view.layoutParams.height*0.52).toInt()),
            Point((view.layoutParams.width*0.295).toInt(), (view.layoutParams.height*0.537).toInt()),
            Point((view.layoutParams.width*0.58).toInt(), (view.layoutParams.height*0.564).toInt()),
            Point((view.layoutParams.width*0.657).toInt(), (view.layoutParams.height*0.586).toInt())
        )

        const4Positions = arrayOf(
            Point((view.layoutParams.width*0.42).toInt(), (view.layoutParams.height*0.57).toInt()),
            Point((view.layoutParams.width*0.316).toInt(), (view.layoutParams.height*0.604).toInt()),
            Point((view.layoutParams.width*0.254).toInt(), (view.layoutParams.height*0.705).toInt()),
            Point((view.layoutParams.width*0.425).toInt(), (view.layoutParams.height*0.761).toInt())
        )

        const5Positions = arrayOf(
            Point((view.layoutParams.width*0.775).toInt(), (view.layoutParams.height*0.594).toInt()),
            Point((view.layoutParams.width*0.676).toInt(), (view.layoutParams.height*0.677).toInt()),
            Point((view.layoutParams.width*0.572).toInt(), (view.layoutParams.height*0.723).toInt()),
            Point((view.layoutParams.width*0.63).toInt(), (view.layoutParams.height*0.743).toInt())
        )

        constellations = arrayOf(
            //Cancer
            Constellation(
                0,
                5,
                "",
                "",
                List(5) { i ->
                    Star(
                        id = i,
                        done = false,
                        intermediate = false,
                        action = "",
                        timesCompleted = 0,
                        position = const1Positions[i],
                        starViews = mutableListOf(),
                        paths = mutableListOf()
                    )
                },
                6
            ),
            Constellation(
                1,
                5,
                "",
                "",
                List(5) { i ->
                    Star(
                        id = i,
                        done = false,
                        intermediate = false,
                        action = "",
                        timesCompleted = 0,
                        position = const2Positions[i],
                        starViews = mutableListOf(),
                        paths = mutableListOf()
                    )
                },
                4
            ),
            Constellation(
                2,
                8,
                "",
                "",
                List(8) { i ->
                    Star(
                        id = i,
                        done = false,
                        intermediate = false,
                        action = "",
                        timesCompleted = 0,
                        position = const3Positions[i],
                        starViews = mutableListOf(),
                        paths = mutableListOf()
                    )
                },
                7
            ),
            Constellation(
                3,
                4,
                "",
                "",
                List(4) { i ->
                    Star(
                        id = i,
                        done = false,
                        intermediate = false,
                        action = "",
                        timesCompleted = 0,
                        position = const4Positions[i],
                        starViews = mutableListOf(),
                        paths = mutableListOf()
                    )
                },
                4
            ),
            Constellation(
                4,
                4,
                "",
                "",
                List(4) { i ->
                    Star(
                        id = i,
                        done = false,
                        intermediate = false,
                        action = "",
                        timesCompleted = 0,
                        position = const5Positions[i],
                        starViews = mutableListOf(),
                        paths = mutableListOf()
                    )
                },
                3
            )
        )
    }

    private fun setStars() {
        starParams = LinearLayout.LayoutParams(dpToPx(25), dpToPx(25))
        for (i in constellations.indices) {
            for (j in constellations[i].stars.indices) {
                val starOffOutter = ImageView(this)
                starOffOutter.setImageResource(R.drawable.star_off_outter)
                starOffOutter.layoutParams = starParams
                starOffOutter.x = constellations[i].stars[j].position.x.toFloat()
                starOffOutter.y = constellations[i].stars[j].position.y.toFloat()
                starOffOutter.isClickable = true
                starOffOutter.rotation = 0f

                val starOffInner = ImageView(this)
                starOffInner.setImageResource(R.drawable.star_circle)
                starOffInner.layoutParams = LinearLayout.LayoutParams(dpToPx(7), dpToPx(7))
                starOffInner.x = constellations[i].stars[j].position.x.toFloat() + dpToPx(25)*0.361f
                starOffInner.y = constellations[i].stars[j].position.y.toFloat() + dpToPx(25)*0.36f

                val starOffMid = ImageView(this)
                starOffMid.setImageResource(R.drawable.star_circle)
                starOffMid.alpha = 0.3f
                starOffMid.layoutParams = ViewGroup.LayoutParams(dpToPx(13), dpToPx(13))
                starOffMid.x = constellations[i].stars[j].position.x.toFloat() + dpToPx(25)*0.240f
                starOffMid.y = constellations[i].stars[j].position.y.toFloat() + dpToPx(25)*0.247f

                val starOffMid2 = ImageView(this)
                starOffMid2.setImageResource(R.drawable.star_circle)
                starOffMid2.alpha = 0.4f
                starOffMid2.layoutParams = ViewGroup.LayoutParams(dpToPx(19), dpToPx(19))
                starOffMid2.x = constellations[i].stars[j].position.x.toFloat() + dpToPx(25)*0.12f
                starOffMid2.y = constellations[i].stars[j].position.y.toFloat() + dpToPx(25)*0.13f
                starOffMid2.visibility = View.INVISIBLE

                val starOffOutter2 = ImageView(this)
                starOffOutter2.setImageResource(R.drawable.star_off_outter)
                starOffOutter2.layoutParams = starParams
                starOffOutter2.x = constellations[i].stars[j].position.x.toFloat()
                starOffOutter2.y = constellations[i].stars[j].position.y.toFloat()
                starOffOutter2.rotation = -11f
                starOffOutter2.visibility = View.INVISIBLE

                val starOnInner = ImageView(this)
                starOnInner.setImageResource(R.drawable.star_on_inner)
                starOnInner.layoutParams = starParams
                starOnInner.x = constellations[i].stars[j].position.x.toFloat()
                starOnInner.y = constellations[i].stars[j].position.y.toFloat()

                val starOnMid = ImageView(this)
                starOnMid.setImageResource(R.drawable.star_circle)
                starOnMid.layoutParams = starParams
                starOnMid.x = constellations[i].stars[j].position.x.toFloat()
                starOnMid.x = constellations[i].stars[j].position.y.toFloat()

                val starOnOutter = ImageView(this)
                starOnOutter.setImageResource(R.drawable.star_on_outter)
                starOnOutter.layoutParams = starParams
                starOnOutter.x = constellations[i].stars[j].position.x.toFloat()
                starOnOutter.y = constellations[i].stars[j].position.y.toFloat()

                val starOnBright = ImageView(this)
                starOnBright.setImageResource(R.drawable.star_on_bright)
                starOnBright.layoutParams = starParams
                starOnBright.x = constellations[i].stars[j].position.x.toFloat()
                starOnBright.y = constellations[i].stars[j].position.y.toFloat()
                starOnBright.visibility = View.INVISIBLE

                val starIntermediaryOutter = ImageView(this)
                starIntermediaryOutter.setImageResource(R.drawable.star_inter_2x)
                starIntermediaryOutter.layoutParams = ViewGroup.LayoutParams(dpToPx(25), dpToPx(25))
                starIntermediaryOutter.x = constellations[i].stars[j].position.x.toFloat()
                starIntermediaryOutter.y = constellations[i].stars[j].position.y.toFloat()
                starIntermediaryOutter.visibility = View.INVISIBLE

                val starButton = ImageView(this)
                starButton.setImageResource(R.drawable.star_circle)
                starButton.layoutParams = ViewGroup.LayoutParams(dpToPx(26), dpToPx(26))
                starButton.x = constellations[i].stars[j].position.x.toFloat()
                starButton.y = constellations[i].stars[j].position.y.toFloat()
                starButton.isClickable = true
                starButton.alpha = 0f


                constellations[i].stars[j].starViews.add(starOffInner)
                constellations[i].stars[j].starViews.add(starOffMid)
                constellations[i].stars[j].starViews.add(starOffOutter)
                constellations[i].stars[j].starViews.add(starOffMid2)
                constellations[i].stars[j].starViews.add(starOffOutter2)
                constellations[i].stars[j].starViews.add(starOnBright)
                constellations[i].stars[j].starViews.add(starIntermediaryOutter)
                constellations[i].stars[j].starViews.add(starButton)

                binding.sky.addView(starOffInner)
                binding.sky.addView(starOffMid)
                binding.sky.addView(starOffOutter)
                binding.sky.addView(starOffMid2)
                binding.sky.addView(starOffOutter2)
                binding.sky.addView(starOnBright)
                binding.sky.addView(starIntermediaryOutter)
                binding.sky.addView(starButton)

            }
        }

        for (i in 0..4) {
            constellations[i].setStarsNeighbors()
        }
        starLineView = SkyStarLineView(this)
        starRectangle = ImageView(this)
        starRectangle.setImageResource(R.drawable.rectangle)
        starRectangle.alpha = 0.5f
        starRectangle.layoutParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT)
        starRectangle.visibility = View.INVISIBLE
    }

    private fun setPaths() {
        pathCustomView.setConstellations(constellations)
        binding.sky.addView(pathCustomView)
    }

    private fun setTexts(constellation: String, star: String) {
        language = if (Language().language == "pt") {
            "pt"
        } else {
            "en"
        }
        database.child(language).child("sky").child(constellation).addListenerForSingleValueEvent(
            object : ValueEventListener {
                override fun onCancelled(databaseError: DatabaseError) {
                    Log.w(ContentValues.TAG, "getUser:onCancelled", databaseError.toException())
                }

                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    actionText.text = dataSnapshot.child(star).value as String
                    if ((dataSnapshot.child(star).value as String).length <= 35) {
                        actionText.y = actionText.y + dpToPx(10)
                        actionDot.y = actionDot.y + dpToPx(10)
                    } else if ((dataSnapshot.child(star).value as String).length >= 60) {
                        actionText.y = actionText.y - dpToPx(12)
                        actionDot.y = actionDot.y - dpToPx(10)
                    }

                }
            }
        )
    }

    private fun setParticles() {
        val particle = List(100) { ImageView(this) }
        val size = List(100) { Random.nextDouble(1.0, 3.0) }
        val x = List(100) { Random.nextInt(0, params.width)}
        val y = List(100) { Random.nextInt(0, params.height)}
        for (i in particle.indices) {
            particle[i].setImageResource(R.drawable.star_circle)
            particle[i].layoutParams = LinearLayout.LayoutParams(dpToPxD(size[i]), dpToPxD(size[i]))
            particle[i].x = x[i].toFloat()
            particle[i].y = y[i].toFloat()
            sky.addView(particle[i])
        }

    }
}