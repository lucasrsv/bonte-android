package com.example.android.bonte_android.sky

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.animation.PropertyValuesHolder
import android.animation.ValueAnimator
import android.annotation.SuppressLint
import android.content.ContentValues
import android.graphics.Color
import android.graphics.Path
import android.graphics.Point
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.MotionEvent
import android.view.ScaleGestureDetector
import android.view.View
import android.view.ViewGroup
import android.view.animation.AlphaAnimation
import android.view.animation.AnimationUtils
import android.view.animation.LinearInterpolator
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.animation.doOnEnd
import androidx.core.animation.doOnPause
import androidx.core.animation.doOnRepeat
import androidx.core.animation.doOnStart
import androidx.databinding.DataBindingUtil
import com.example.android.bonte_android.*
import com.example.android.bonte_android.R
import com.example.android.bonte_android.customViews.SkyStarLineView
import com.example.android.bonte_android.customViews.StarPathView
import com.example.android.bonte_android.databinding.ActivitySkyBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import kotlinx.android.synthetic.main.activity_sky.*
import java.util.*
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
    private lateinit var doStarAgainText: TextView
    private lateinit var takeYourTimeText: TextView
    private lateinit var actionDot: ImageView
    private lateinit var menuButton: ImageView
    private lateinit var language: String
    private var database: DatabaseReference = FirebaseDatabase.getInstance().reference
    private val firebaseUser = FirebaseAuth.getInstance().currentUser
    private var numClicks = 0
    private var time = 0L
    private var isStarClicked = false
    private var canDoStarAgain = false
    private var canZoomOut = true
    private var canZoomOut2 = true
    private var canRotate = true
    private var canAddPath = true
    private var boolForRotateShow = true
    private var rotateAnimations = Array(26) { ObjectAnimator() }

    override fun onCreate(savedInstanceState: Bundle?) {
        setTheme(R.style.AppTheme)
        super.onCreate(savedInstanceState)

        binding = DataBindingUtil.setContentView(
            this,
            R.layout.activity_sky
        )
        doStarAgainText = binding.starDoAgainText
        takeYourTimeText = binding.takeYourTimeText
        view = binding.sky

        params = view.layoutParams
        params.width = view.resources.displayMetrics.widthPixels * 2
        params.height = view.resources.displayMetrics.heightPixels * 2

        view.layoutParams = params

        actionDot = ImageView(this)
        actionDot.setImageResource(R.drawable.star_circle)
        actionDot.layoutParams = LinearLayout.LayoutParams(dpToPx(3), dpToPx(3))
        actionDot.visibility = View.INVISIBLE
        actionText = binding.actionText

        sky.addView(actionDot)
        pathCustomView = StarPathView(this)

        scaleListener = object : ScaleGestureDetector.SimpleOnScaleGestureListener() {
            override fun onScale(detector: ScaleGestureDetector?): Boolean {
                if (canZoomOut) {
                    canZoomOut = false
                    numClicks = 0
                    val pvhX: PropertyValuesHolder
                    val pvhY: PropertyValuesHolder
                    val pvhXBright: PropertyValuesHolder
                    val pvhYBright: PropertyValuesHolder
                    val pvhXLine = PropertyValuesHolder.ofFloat(View.SCALE_X, 1f, 0f)
                    val pvhYLine = PropertyValuesHolder.ofFloat(View.SCALE_Y, 1f, 0f)
                    actionText.visibility = View.INVISIBLE
                    actionText.text = ""
                    actionDot.visibility = View.INVISIBLE
                    sky.removeView(starLineView)
                    starRectangle.visibility = View.INVISIBLE
                    starDoAgainText.visibility = View.INVISIBLE
                    takeYourTimeText.visibility = View.INVISIBLE
                    skyZoomLayout.engine.setZoomEnabled(true)
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

                    val scaleButton = ObjectAnimator.ofPropertyValuesHolder(
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
                    val scaleLine =
                        ObjectAnimator.ofPropertyValuesHolder(starLineView, pvhXLine, pvhYLine)
                            .apply {
                                duration = 500
                            }
                    var index = 0
                    when (starClicked.first) {
                        0 -> {
                            index = starClicked.second
                        }
                        1 -> {
                            index = starClicked.second + 5
                        }
                        2 -> {
                            index = starClicked.second + 10
                        }
                        3 -> {
                            index = starClicked.second + 18
                        }
                        else -> {
                            index = starClicked.second + 22
                        }
                    }
                    val pvhR = PropertyValuesHolder.ofFloat(View.ROTATION, 349f, 349f+360f)
                    rotateAnimations[index] = ObjectAnimator.ofPropertyValuesHolder(
                        constellations[starClicked.first].stars[starClicked.second].starViews[2],
                        pvhR
                    ).apply {
                        duration = 15000
                        repeatCount = ValueAnimator.INFINITE
                        repeatMode = ValueAnimator.RESTART
                        interpolator = LinearInterpolator()
                    }

                    if (!constellations[starClicked.first].stars[starClicked.second].intermediate && !constellations[starClicked.first].stars[starClicked.second].done) {
                        AnimatorSet().apply {
                            playTogether(
                                scaleButton,
                                scaleInner,
                                scaleMid,
                                scaleOutter1,
                                scaleOutter2,
                                scaleBright

                                //scaleLine
                            )
                            rotateAnimations[index].start()
                            start()
                        }
                    } else if (!constellations[starClicked.first].stars[starClicked.second].intermediate && constellations[starClicked.first].stars[starClicked.second].done
                        || constellations[starClicked.first].stars[starClicked.second].intermediate && !constellations[starClicked.first].stars[starClicked.second].done
                        || constellations[starClicked.first].stars[starClicked.second].intermediate && constellations[starClicked.first].stars[starClicked.second].done
                    ) {
                        constellations[starClicked.first].stars[starClicked.second].starViews[2].isClickable =
                            true
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
                                val alphaMid2: ObjectAnimator = ObjectAnimator.ofFloat(
                                    constellations[i].stars[j].starViews[3],
                                    "alpha",
                                    0.12f,
                                    0.4f
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
                                val alphaIntermediaryOutter: ObjectAnimator = ObjectAnimator.ofFloat(
                                    constellations[i].stars[j].starViews[6],
                                    "alpha",
                                    0.3f,
                                    1.0f
                                ).apply {
                                    duration = 500
                                }
                                AnimatorSet().apply {
                                    playTogether(alphaInner, alphaMid, alphaMid2, alphaOutter1, alphaOutter2, alphaIntermediaryOutter)
                                    start()
                                }
                            }
                        }
                    }
                    if (canAddPath) {
                        moveStars(
                            starClicked.first, starClicked.second,
                            constellations[starClicked.first].stars[starClicked.second].starViews[0].x,
                            constellations[starClicked.first].stars[starClicked.second].starViews[0].y,
                            true
                        )
                    }
                    canRotate = false
                    if (canRotate) {
                        var index = 0
                        when (starClicked.first) {
                            0 -> {
                                index = starClicked.second
                            }
                            1 -> {
                                index = starClicked.second + 5
                            }
                            2 -> {
                                index = starClicked.second + 10
                            }
                            3 -> {
                                index = starClicked.second + 18
                            }
                            else -> {
                                index = starClicked.second + 22
                            }
                        }
                    }
                    return true
                }
                return false
            }
        }
        scaleDetector = ScaleGestureDetector(this, scaleListener)

        skyZoomLayout.setOnTouchListener { _, event ->
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
        getSkyStatus()
        touchListener()
        longPressListener()
        setParticles()
        addMenu()
        setTexts("0", "0", "takeYourTime")
        setTexts("0", "0", "doStarAgainText")

    }

    @SuppressLint("ClickableViewAccessibility")
    private fun touchListener() {
        for (i in constellations.indices) {
            for (j in constellations[i].stars.indices) {
                constellations[i].stars[j].starViews[7].setOnTouchListener { _, event ->
                    if (!isStarClicked) {
                        when (event.actionMasked) {
                            MotionEvent.ACTION_DOWN -> {
                                time = event.eventTime
                                Log.d("timeDown", time.toString())
                                true
                            }
                            MotionEvent.ACTION_UP -> {
                                val totalTime = event.eventTime - time
                                if (totalTime <= 300) {
                                    numClicks++
                                    starClicked = Pair(i, j)
                                    isStarClicked = true
                                    canZoomOut = false
                                    canZoomOut2 = false
                                    canRotate = true
                                    skyZoomLayout.setZoomEnabled(false)
                                    skyZoomLayout.engine.setScrollEnabled(false)
                                    skyZoomLayout.engine.setFlingEnabled(false)
                                    skyZoomLayout.setAnimationDuration(2000)
                                    skyZoomLayout.engine.moveTo(
                                        5f,
                                        (constellations[i].stars[j].starViews[2].x) * -1 + view.layoutParams.width*0.0865.toFloat(),
                                        (constellations[i].stars[j].starViews[2].y) * -1 + view.layoutParams.height*0.090.toFloat(),
                                        true
                                    )
                                    skyZoomLayout.setAnimationDuration(280)

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
                                        doOnEnd {
                                            canZoomOut = true
                                            canZoomOut2 = true
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
                                    var pvhR = PropertyValuesHolder.ofFloat(View.ROTATION, 0f, -11f)

                                    val rotateAnim = ObjectAnimator.ofPropertyValuesHolder(constellations[i].stars[j].starViews[2], pvhR). apply {
                                        duration = 1000
                                    }

                                    var rotateStar: ObjectAnimator
                                    var index = 0
                                    when (i) {
                                        0 -> {
                                            rotateStar = rotateAnimations[j]
                                            index = j
                                        }
                                        1 -> {
                                            rotateStar =rotateAnimations[j+5]
                                            index = j+5
                                        }
                                        2 -> {
                                            rotateStar = rotateAnimations[j+10]
                                            index = j+10
                                        }
                                        3 -> {
                                            rotateStar = rotateAnimations[j+18]
                                            index = j+18
                                        }
                                        else -> {
                                            rotateStar = rotateAnimations[j+22]
                                            index = j+22
                                        }
                                    }
                                    var currentRotation = 0f

                                    rotateStar.doOnPause {
                                        currentRotation = rotateStar.animatedValue as Float
                                        pvhR = PropertyValuesHolder.ofFloat(View.ROTATION, currentRotation, 349f)
                                        rotateStar = ObjectAnimator.ofPropertyValuesHolder(constellations[i].stars[j].starViews[2],pvhR)
                                        rotateStar.duration = 1000
                                        rotateAnimations[index] = rotateStar
                                        rotateStar.start()
                                    }

                                    rotateStar.pause()


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
                                        setTexts(constellation, star, "actionText")

                                    if (!constellations[i].stars[j].done && !constellations[i].stars[j].intermediate) {
                                        starLineView.setValues(constellations[i].stars[j].position.x, constellations[i].stars[j].position.y)
                                        sky.addView(starLineView)
                                        AnimatorSet().apply {
                                            playTogether(scaleButton, scaleInner, scaleMid, scaleOutter1, scaleOutter2, scaleOutter3, fadeText, fadeDot)
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
                                    val pathAlpha = ObjectAnimator.ofFloat(pathCustomView, "alpha", 1.0f, 0f)
                                    pathAlpha.duration = 500
                                    pathAlpha.doOnStart {
                                        moveStars(i, j, constellations[i].stars[j].starViews[0].x, constellations[i].stars[j].starViews[0].y, isReturning = false)
                                    }
                                    pathAlpha.doOnEnd {
                                        (pathCustomView.parent as ViewGroup).removeView(pathCustomView)
                                        canAddPath = true
                                    }
                                    pathAlpha.start()

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
                                                val alphaMid2: ObjectAnimator = ObjectAnimator.ofFloat(constellations[a].stars[b].starViews[3], "alpha", 0.4f, 0.12f).apply {
                                                    duration = 1000
                                                }
                                                val alphaOutter1: ObjectAnimator = ObjectAnimator.ofFloat(constellations[a].stars[b].starViews[2], "alpha", 1.0f, 0.3f).apply {
                                                    duration = 1000
                                                }
                                                val alphaOutter2: ObjectAnimator = ObjectAnimator.ofFloat(constellations[a].stars[b].starViews[4], "alpha", 1.0f, 0.3f).apply {
                                                    duration = 1000
                                                }
                                                val alphaIntermediaryOutter: ObjectAnimator = ObjectAnimator.ofFloat(constellations[a].stars[b].starViews[6], "alpha", 1.0f, 0.3f).apply {
                                                    duration = 1000
                                                }
                                                AnimatorSet().apply {
                                                    playTogether(alphaInner, alphaMid, alphaMid2, alphaOutter1, alphaOutter2, alphaIntermediaryOutter)
                                                    start()
                                                }
                                            }
                                        }
                                    }
                                    true
                                }
                                true
                            }
                            else -> super.onTouchEvent(event)
                        }
                    } else {
                        //Caso seja o 2o toque
                        when (event.actionMasked) {
                            MotionEvent.ACTION_DOWN -> {
                                time = event.eventTime
                            }

                            MotionEvent.ACTION_UP -> {
                                val totalTime = event.eventTime - time
                                if (totalTime <= 300) {
                                    if (starClicked.first == i && starClicked.second == j && numClicks == 1 && !constellations[i].stars[j].intermediate && !constellations[i].stars[j].done) {
                                        numClicks++
                                        constellations[i].stars[j].starViews[4].visibility =
                                            View.VISIBLE

                                        val constellation = if (i == 0) {
                                            "volans"
                                        } else if (i == 1) {
                                            "cancer"
                                        } else if (i == 2) {
                                            "aquila"
                                        } else if (i == 3) {
                                            "equuleus"
                                        } else {
                                            "sagitta"
                                        }
                                        val star = "star" + (j + 1).toString()

                                        starRectangle.x =
                                            constellations[i].stars[j].position.x - dpToPxF(36.5f)
                                        starRectangle.y =
                                            (-skyZoomLayout.engine.panY + view.height / skyZoomLayout.zoom) - starRectangle.height
                                        takeYourTimeText.x =
                                            constellations[i].stars[j].position.x - dpToPxF(26.3f)
                                        takeYourTimeText.y =
                                            (-skyZoomLayout.engine.panY + view.height / skyZoomLayout.zoom) - starRectangle.height / 1.1f

                                        val fadeInRectangle = ObjectAnimator.ofFloat(
                                            starRectangle,
                                            "alpha",
                                            0f,
                                            0.06f
                                        ).apply {
                                            duration = 500
                                            doOnStart {
                                                starRectangle.visibility = View.VISIBLE
                                            }
                                        }

                                        val fadeInTakeYourTimeText = ObjectAnimator.ofFloat(
                                            takeYourTimeText,
                                            "alpha",
                                            0f,
                                            1.0f
                                        ).apply {
                                            duration = 500
                                            doOnStart {
                                                takeYourTimeText.visibility = View.VISIBLE
                                            }
                                        }

                                        val pvhR =
                                            PropertyValuesHolder.ofFloat(View.ROTATION, -11f, -35f)
                                        val rotateAnim = ObjectAnimator.ofPropertyValuesHolder(
                                            constellations[i].stars[j].starViews[4],
                                            pvhR
                                        ).apply {
                                            duration = 1000
                                        }

                                        AnimatorSet().apply {
                                            playTogether(
                                                rotateAnim,
                                                fadeInRectangle,
                                                fadeInTakeYourTimeText
                                            )
                                            start()
                                        }
                                        updateSkyStatus(i, j, "intermediary", true)

                                    } else if (starClicked.first == i && starClicked.second == j &&
                                        !constellations[i].stars[j].intermediate && constellations[i].stars[j].done) {
                                        starRectangle.x =
                                            constellations[i].stars[j].position.x - dpToPxF(36.5f)
                                        starRectangle.y =
                                            (-skyZoomLayout.engine.panY + view.height / skyZoomLayout.zoom) - starRectangle.height
                                        takeYourTimeText.x =
                                            constellations[i].stars[j].position.x - dpToPxF(26.3f)
                                        takeYourTimeText.y =
                                            (-skyZoomLayout.engine.panY + view.height / skyZoomLayout.zoom) - starRectangle.height / 1.1f
                                        updateSkyStatus(i, j, "intermediary", true)
                                        numClicks++
                                        val x = PropertyValuesHolder.ofFloat(View.SCALE_X, 0f, 3.1f)
                                        val y = PropertyValuesHolder.ofFloat(View.SCALE_Y, 0f, 3.1f)
                                        val scaleOutterIntermediary =
                                            ObjectAnimator.ofPropertyValuesHolder(
                                                constellations[i].stars[j].starViews[6],
                                                x,
                                                y
                                            ).apply {
                                                duration = 1000
                                                var isVisible = false
                                                addUpdateListener { animation ->
                                                    if (animation.animatedValue as Float > 0f && !isVisible) {
                                                        isVisible = true
                                                        constellations[i].stars[j].starViews[6].visibility =
                                                            View.VISIBLE
                                                    }
                                                }
                                            }
                                        val takeYourTimeFade = ObjectAnimator.ofFloat(takeYourTimeText, "alpha", 0f, 1f).apply {
                                            duration = 500
                                            doOnStart {
                                                doStarAgainText.alpha = 0f
                                                doStarAgainText.visibility = View.INVISIBLE
                                                takeYourTimeText.visibility = View.VISIBLE
                                            }
                                            start()
                                        }
                                        val doStarAgain = ObjectAnimator.ofFloat(starDoAgainText, "alpha", 1f, 0f).apply {
                                            duration = 500
                                            doOnStart {
                                                takeYourTimeText.alpha = 0f
                                                takeYourTimeText.visibility = View.INVISIBLE
                                            }
                                            start()
                                        }

                                        AnimatorSet().apply {
                                            playTogether(scaleOutterIntermediary, doStarAgain)
                                            play(takeYourTimeFade).after(doStarAgain)
                                            start()
                                        }
                                    }
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
                        if (constellations[i].stars[j].intermediate) {
                            /*val vibrator = applicationContext.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                                vibrator.vibrate(VibrationEffect.createWaveform(longArrayOf(100L, 100L, 100L, 100L, 100L, 100L, 100L, 100L, 100L, 100L), intArrayOf(5, 10, 20, 30, 40, 50, 60, 70, 80, 90), -1))
                            }*/
                            shineStars(i, j)
                            //numClicks = 5 // O onTouch detecta o longpress normalmente. Então, para não entrar nas condições do longpress, aumentei o numClicks.
                            canDoStarAgain = true //Porém, aumentando o número de cliques
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
                                xMid2 = PropertyValuesHolder.ofFloat(View.SCALE_X, 3f, 5f)
                                yMid2 = PropertyValuesHolder.ofFloat(View.SCALE_Y, 3f, 5f)
                                xGeneral = PropertyValuesHolder.ofFloat(View.SCALE_X, 3f, 5f)
                                yGeneral = PropertyValuesHolder.ofFloat(View.SCALE_Y, 3f, 5f)
                                xOutter = PropertyValuesHolder.ofFloat(View.SCALE_X, 3.1f, 5.1f)
                                yOutter = PropertyValuesHolder.ofFloat(View.SCALE_Y, 3.1f, 5.1f)
                                xBright = PropertyValuesHolder.ofFloat(View.SCALE_X, 4f, 7f)
                                yBright = PropertyValuesHolder.ofFloat(View.SCALE_Y, 4f, 7f)
                                time = 1000L
                                delay = 500L
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
                                startDelay = if (constellations[i].stars[j].done) {
                                    500L
                                } else {
                                    0L
                                }
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
                            val fadeOutText = ObjectAnimator.ofFloat(actionText, "alpha", 1.0f, 0f)
                            fadeOutText.duration = 500
                            val fadeOutBall = ObjectAnimator.ofFloat(actionDot, "alpha", 1.0f, 0f)
                            fadeOutBall.duration = 500
                            val fadeInText = ObjectAnimator.ofFloat(actionText, "alpha", 0f, 1.0f)
                            fadeInText.duration = 500
                            val fadeInBall = ObjectAnimator.ofFloat(actionDot, "alpha", 0f, 1.0f)
                            fadeInBall.duration = 500
                            fadeInBall.doOnEnd {
                                canZoomOut = true
                                canZoomOut2 = true
                            }

                            if (takeYourTimeText.alpha == 1f) {
                                val takeYourTimeFade = ObjectAnimator.ofFloat(takeYourTimeText, "alpha", 1f, 0f).apply {
                                    duration = 500
                                    doOnStart {
                                        doStarAgainText.alpha = 0f
                                        doStarAgainText.visibility = View.INVISIBLE
                                    }
                                    start()
                                }

                                val rectangleFade = ObjectAnimator.ofFloat(starRectangle, "alpha", 0.06f, 0f).apply {
                                    duration = 500
                                    start()
                                }
                            }

                            val shake = AnimationUtils.loadAnimation(this, R.anim.shake_star)
                            var xReturn = PropertyValuesHolder.ofFloat(View.SCALE_X, 4f, 3f)
                            var yReturn = PropertyValuesHolder.ofFloat(View.SCALE_Y, 4f, 3f)
                            var outterXReturn = PropertyValuesHolder.ofFloat(View.SCALE_X, 4.1f, 0f)
                            var outterYReturn = PropertyValuesHolder.ofFloat(View.SCALE_Y, 4.1f, 0f)
                            var xBrightReturn = PropertyValuesHolder.ofFloat(View.SCALE_X, 6f, 4f)
                            var yBrightReturn = PropertyValuesHolder.ofFloat(View.SCALE_Y, 6f, 4f)

                            if(constellations[i].stars[j].done) {
                                xReturn = PropertyValuesHolder.ofFloat(View.SCALE_X, 5f, 3f)
                                yReturn = PropertyValuesHolder.ofFloat(View.SCALE_Y, 5f, 3f)
                                outterXReturn = PropertyValuesHolder.ofFloat(View.SCALE_X, 5.1f, 0f)
                                outterYReturn = PropertyValuesHolder.ofFloat(View.SCALE_Y, 5.1f, 0f)
                                xBrightReturn = PropertyValuesHolder.ofFloat(View.SCALE_X, 7f, 4f)
                                yBrightReturn = PropertyValuesHolder.ofFloat(View.SCALE_Y, 7f, 4f)
                            }

                            val fadeInStarDoAgainText = ObjectAnimator.ofFloat(starDoAgainText, "alpha", 0f, 1.0f).apply {
                                duration = 500
                                doOnStart {
                                    starDoAgainText.visibility = View.VISIBLE
                                    takeYourTimeText.alpha = 0f
                                }
                            }

                            val scaleDownOutter = ObjectAnimator.ofPropertyValuesHolder(constellations[i].stars[j].starViews[2], outterXReturn, outterYReturn).apply {
                                duration = 250
                                doOnEnd {
                                    constellations[i].stars[j].starViews[2].visibility = View.GONE
                                    constellations[i].stars[j].starViews[4].visibility = View.GONE
                                }

                            }
                            val scaleDownOutter2 = ObjectAnimator.ofPropertyValuesHolder(constellations[i].stars[j].starViews[4], outterXReturn, outterYReturn).apply {
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
                                    val fadeRectangle = ObjectAnimator.ofFloat(starRectangle, "alpha", 0f, 0.06f)
                                    fadeRectangle.duration = 500
                                    starRectangle.x = constellations[i].stars[j].position.x - dpToPxF(36.5f)
                                    starRectangle.y = (-skyZoomLayout.engine.panY + view.height/skyZoomLayout.zoom) - starRectangle.height
                                    doStarAgainText.x = constellations[i].stars[j].position.x - dpToPxF(26.3f)
                                    doStarAgainText.y = (-skyZoomLayout.engine.panY + view.height/skyZoomLayout.zoom) - starRectangle.height/1.1f
                                    starRectangle.visibility = View.VISIBLE
                                    fadeRectangle.start()
                                    fadeInStarDoAgainText.start()
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
                                startDelay = 100
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
                                canZoomOut = false
                                canZoomOut2 = false
                                starLineView.undo()
                                constellations[i].stars[j].starViews[3].visibility = View.VISIBLE
                                AnimatorSet().apply {
                                    playTogether(
                                        fadeOutBall,
                                        fadeOutText,
                                        scaleMid2,
                                        scaleInner,
                                        scaleMid,
                                        scaleOutter,
                                        scaleOutter2,
                                        scaleBright
                                    )
                                    playTogether(scaleDownOutter, scaleDownOutter2)
                                    play(scaleDownOutter).after(scaleBright)
                                    play(scaleDownMid).after(scaleDownOutter)
                                    play(scaleDownMid2).after(scaleDownOutter)
                                    play(scaleDownBright).after(scaleDownOutter)
                                    play(scaleDownInner).after(scaleDownOutter)
                                    playTogether(fadeInBall, fadeInText)
                                    play(fadeInBall).after(scaleDownInner)
                                    start()
                                }
                                constellations[i].stars[j].starViews[2].startAnimation(shake)
                                constellations[i].stars[j].starViews[4].startAnimation(shake)
                                constellations[i].stars[j].starViews[0].startAnimation(shake)
                                constellations[i].stars[j].starViews[1].startAnimation(shake)
                                constellations[i].stars[j].starViews[3].startAnimation(shake)

                            } else {
                                canZoomOut = false
                                canZoomOut2 = false
                                AnimatorSet().apply {
                                    if (constellations[i].stars[j].timesCompleted < 3) {
                                        playTogether(
                                            fadeOutBall,
                                            fadeOutText,
                                            scaleInner,
                                            scaleMid,
                                            scaleMid2,
                                            scaleIntermediaryOutter,
                                            scaleBright,
                                            alphaMid2
                                        )
                                        play(scaleDownIntermediaryOutter).after(scaleInner)
                                    } else {
                                        playTogether(
                                            fadeOutBall,
                                            fadeOutText,
                                            scaleInner,
                                            scaleMid,
                                            scaleMid2,
                                            scaleIntermediaryOutter,
                                            scaleBright
                                        )
                                        play(scaleDownIntermediaryOutter).after(scaleInner)
                                    }

                                    if (constellations[i].stars[j].timesCompleted == 0L) {
                                        play(scaleDownInner).after(scaleDownOutter)
                                        play(scaleDownMid).after(scaleDownOutter)
                                        play(scaleDownMid2).after(scaleDownOutter)
                                        play(scaleDownBright).after(scaleDownOutter)
                                    } else {
                                        play(scaleDownInner).after(scaleDownIntermediaryOutter)
                                        play(scaleDownMid).after(scaleDownIntermediaryOutter)
                                        play(scaleDownMid2).after(scaleDownIntermediaryOutter)
                                        play(scaleDownBright).after(scaleDownIntermediaryOutter)
                                    }

                                    playTogether(
                                        fadeInBall,
                                        fadeInText
                                    )
                                     playSequentially(scaleInner, fadeInBall)
                                    start()
                                }

                                constellations[i].stars[j].starViews[0].startAnimation(shake)
                                constellations[i].stars[j].starViews[1].startAnimation(shake)
                                constellations[i].stars[j].starViews[3].startAnimation(shake)
                                constellations[i].stars[j].starViews[6].startAnimation(shake)
                            }
                            particlesExplosion(i, j, constellations[i].stars[j].timesCompleted.toInt())
                            updateSkyStatus(i, j, "done", true)
                            Log.d("lala", constellations[i].stars[j].done.toString())
                            updateSkyStatus(i, j, "intermediary", false)
                            if (constellations[i].stars[j].timesCompleted <= 2) {
                                updateSkyStatus(i, j, "timesCompleted", false)
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
                        done = false ,
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
                starOffMid2.alpha = 0.5f
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
        sky.addView(starRectangle)
    }

    private fun setPaths() {
        pathCustomView.setConstellations(constellations)
        binding.sky.addView(pathCustomView)
    }

    private fun setTexts(constellation: String, star: String, whichText: String) {
        language = if (Language().language == "pt") {
            "pt"
        } else {
            "en"
        }
        when (whichText) {
            "actionText" -> {
                database.child(language).child("sky").child(constellation).addListenerForSingleValueEvent(
                    object : ValueEventListener {
                        override fun onCancelled(databaseError: DatabaseError) {
                            Log.w(
                                ContentValues.TAG,
                                "getUser:onCancelled",
                                databaseError.toException()
                            )
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
            "doStarAgainText" -> {
                database.child(language).child("interactions").addListenerForSingleValueEvent(
                    object : ValueEventListener {
                        override fun onCancelled(databaseError: DatabaseError) {
                            Log.w(
                                ContentValues.TAG,
                                "getUser:onCancelled",
                                databaseError.toException()
                            )
                        }

                        override fun onDataChange(dataSnapshot: DataSnapshot) {
                            doStarAgainText.text = dataSnapshot.child("doStarAgain").value as String
                        }
                    }
                )
            }
            "takeYourTime" -> {
                database.child(language).child("interactions").addListenerForSingleValueEvent(
                    object : ValueEventListener {
                        override fun onCancelled(databaseError: DatabaseError) {
                            Log.w(
                                ContentValues.TAG,
                                "getUser:onCancelled",
                                databaseError.toException()
                            )
                        }

                        override fun onDataChange(dataSnapshot: DataSnapshot) {
                            takeYourTimeText.text = dataSnapshot.child("takeYourTime").value as String
                        }
                    }
                )
            }
        }
    }

    private fun setParticles() {
        val particle = List(100) { ImageView(this) }
        val size = List(100) { Random.nextDouble(1.0, 5.0) }
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

    private fun moveStars(const: Int, star: Int, x: Float, y: Float, isReturning: Boolean) {
        var bool = false
        var bool2 = true
        canAddPath = false
        for (i in constellations.indices) {
            for (j in constellations[i].stars.indices) {
                if (const == i && star == j) {

                } else {
                    for (k in constellations[i].stars[j].starViews.indices) {
                        val starClicked = constellations[const].stars[star].starViews[k]
                        val star = constellations[i].stars[j].starViews[k]

                        if (i == 0 && j == 4 && !bool) {
                            bool = true
                        }
                        val path = Path().apply {
                            if (!isReturning) {
                                moveTo(star.x, star.y)
                                if (star.x >  starClicked.x && star.y > starClicked.y) {
                                    lineTo(star.x + dpToPxF(20f), star.y + dpToPxF(40f))
                                } else if (star.x > starClicked.x && star.y < starClicked.y) {
                                    lineTo(star.x + dpToPxF(20f), star.y - dpToPxF(40f))
                                } else if (star.x < starClicked.x && star.y < starClicked.y) {
                                    lineTo(star.x - dpToPxF(20f), star.y - dpToPxF(40f))
                                } else if (star.x < starClicked.x && star.y > starClicked.y){
                                    lineTo(star.x - dpToPxF(20f), star.y + dpToPxF(40f))
                                }
                            } else {
                                moveTo(star.x, star.y)
                                if (star.x > starClicked.x && star.y > starClicked.y) {
                                    lineTo(star.x - dpToPxF(20f), star.y - dpToPxF(40f))
                                } else if (star.x > starClicked.x && star.y < starClicked.y) {
                                    lineTo(star.x - dpToPxF(20f), star.y + dpToPxF(40f))
                                } else if (star.x < starClicked.x && star.y < starClicked.y) {
                                    lineTo(star.x + dpToPxF(20f), star.y + dpToPxF(40f))
                                } else {
                                    lineTo(star.x + dpToPxF(20f), star.y - dpToPxF(40f))
                                }
                            }

                        }
                        ObjectAnimator.ofFloat(star, View.X, View.Y, path).apply {
                            duration = 2000
                            if (isReturning) {
                                duration = 1000
                                doOnEnd {
                                    isStarClicked = false
                                    canZoomOut = true
                                    canZoomOut2 = true

                                    if (bool2) {
                                        setPaths()
                                        val pathAlpha = ObjectAnimator.ofFloat(pathCustomView, "alpha", 0f, 1.0f)
                                        pathAlpha.duration = 500
                                        pathAlpha.start()
                                        bool2 = false
                                    }
                                }
                            }
                            start()
                        }
                    }

                }
            }
        }
    }

    private fun particlesExplosion(constNum: Int, starNum: Int, starTimesDone: Int) {
        val star = constellations[constNum].stars[starNum].starViews[2]
        val particles = List(100) { ImageView(this) }
        val size = List(100) { Random.nextDouble(1.0, 3.0) }
        val xRight = List(25) { Random.nextInt((star.x + (star.width * 1.9)).toInt(), (star.x + (star.width * 5.0)).toInt()) }
        val yRight = List(25) { ((star.y - (star.height * 4)).toInt() until (star.y + (star.height * 5.0)).toInt() ).random() }
        val xLeft = List(25) { Random.nextInt((star.x - (star.width * 4.0)).toInt(), (star.x - (star.width * 1.0)).toInt()) }
        val yLeft = List(25) { Random.nextInt((star.y - (star.height * 4)).toInt(), (star.y + (star.height * 5)).toInt()) }
        val xTop = List(25) { Random.nextInt((star.x - (star.width * 1.7)).toInt(), (star.x + (star.width * 2.5)).toInt()) }
        val yTop = List(25) { Random.nextInt((star.y - (star.height * 4.0)).toInt(), (star.y - (star.height * 1.0)).toInt()) }
        val xBottom = List(25) { Random.nextInt((star.x - (star.width * 1.7)).toInt(), (star.x + (star.width * 2.5)).toInt()) }
        val yBottom = List(25) { Random.nextInt((star.y + (star.height * 1.7)).toInt(), (star.y + (star.height * 5.0)).toInt()) }
        var paths = Array(100) { Path() }

        for (i in particles.indices) {
            particles[i].setImageResource(R.drawable.star_circle)
            particles[i].layoutParams = LinearLayout.LayoutParams(dpToPxD(size[i]), dpToPxD(size[i]))
            particles[i].x = constellations[constNum].stars[starNum].starViews[0].x
            particles[i].y = constellations[constNum].stars[starNum].starViews[0].y
            sky.addView(particles[i])

            when {
                i <= 24 -> {
                    val path = Path().apply {
                        moveTo(particles[i].x, particles[i].y)
                        lineTo(xRight[i].toFloat(), yRight[i].toFloat())
                    }
                    paths[i] = Path().apply {
                        moveTo(xRight[i].toFloat(), yRight[i].toFloat())
                        lineTo(particles[i].x, particles[i].y)
                    }
                    ObjectAnimator.ofFloat(particles[i], View.X, View.Y, path).apply {
                        duration = Random.nextLong(500, 1000)
                        startDelay = 1000
                        start()
                    }
                }
                i in 25..49 -> {
                    val path = Path().apply {
                        moveTo(particles[i].x, particles[i].y)
                        lineTo(xLeft[i-25].toFloat(), yLeft[i-25].toFloat())
                    }
                    paths[i] = Path().apply {
                        moveTo(xLeft[i-25].toFloat(), yLeft[i-25].toFloat())
                        lineTo(particles[i].x, particles[i].y)
                    }
                    ObjectAnimator.ofFloat(particles[i], View.X, View.Y, path).apply {
                        duration = Random.nextLong(500, 1000)
                        startDelay = 1000
                        start()
                    }
                }
                i in 50..74 -> {
                    val path = Path().apply {
                        moveTo(particles[i-50].x, particles[i-50].y)
                        lineTo(xTop[i-50].toFloat(), yTop[i-50].toFloat())
                    }
                    paths[i] = Path().apply {
                        moveTo(xTop[i-50].toFloat(), yTop[i-50].toFloat())
                        lineTo(particles[i].x, particles[i].y)
                    }
                    ObjectAnimator.ofFloat(particles[i], View.X, View.Y, path).apply {
                        duration = Random.nextLong(500, 1000)
                        startDelay = 1000
                        start()
                    }
                }
                else -> {
                    val path = Path().apply {
                        moveTo(particles[i].x, particles[i].y)
                        lineTo(xBottom[i-75].toFloat(), yBottom[i -75].toFloat())
                    }
                    paths[i] = Path().apply {
                        moveTo(xBottom[i-75].toFloat(), yBottom[i-75].toFloat())
                        lineTo(particles[i].x, particles[i].y)
                    }
                    ObjectAnimator.ofFloat(particles[i], View.X, View.Y, path).apply {
                        duration = Random.nextLong(500, 1000)
                        startDelay = 1000
                        start()
                    }
                }
            }
        }
        for (i in particles.indices) {
            ObjectAnimator.ofFloat(particles[i], View.X, View.Y, paths[i]).apply {
                duration = Random.nextLong(500, 1000)
                startDelay = 2500
                doOnEnd {
                    sky.removeView(particles[i])
                }
                start()
            }
        }
    }

    private fun rotateStarts() {
        for (i in constellations.indices) {
            for (j in constellations[i].stars.indices) {
                val star = constellations[i].stars[j]
                    val pvhR = PropertyValuesHolder.ofFloat(View.ROTATION, 0f, 360f)
                    var k = if (i == 0) {
                        j
                    } else if(i == 1) {
                        j + 5
                    } else if (i == 2) {
                        j + 10
                    } else if (i == 3) {
                        j + 18
                    } else {
                        j + 22
                    }
                    rotateAnimations[k] = ObjectAnimator.ofPropertyValuesHolder(constellations[i].stars[j].starViews[2], pvhR). apply {
                        duration = 15000
                        repeatMode = ValueAnimator.RESTART
                        repeatCount = ValueAnimator.INFINITE
                        interpolator = LinearInterpolator()
                        if (!star.done && !star.intermediate) {
                            start()
                        }
                    }
            }
        }
    }
    private fun shineStars(constIndex: Int, starIndex: Int) {
        for (i in constellations.indices) {
            for (j in constellations[i].stars.indices) {
                val star = constellations[i].stars[j]
                if (i == constIndex && j == starIndex) {

                } else {
                    if (star.done ) {
                        for (k in star.starViews.indices) {
                            when (k) {
                                1 -> {
                                    ObjectAnimator.ofFloat(star.starViews[k], "alpha", 0.09f, 0.3f).apply {
                                        duration = 500
                                        repeatCount = 1
                                        repeatMode = ValueAnimator.REVERSE
                                        doOnRepeat {
                                            pause()
                                            Timer().schedule(object : TimerTask() {
                                                override fun run() {
                                                    runOnUiThread { resume() }
                                                }
                                            }, 2000)
                                        }
                                        start()

                                    }
                                }
                                3 -> {
                                    var extraAlpha = 0
                                    if (star.timesCompleted in 2..3) {
                                        extraAlpha = (star.timesCompleted/10).toInt()
                                    }
                                    ObjectAnimator.ofFloat(star.starViews[k], "alpha", 0.12f, 0.4f + extraAlpha).apply {
                                        duration = 500
                                        repeatCount = 1
                                        repeatMode = ValueAnimator.REVERSE
                                        doOnRepeat {
                                            pause()
                                            Timer().schedule(object : TimerTask() {
                                                override fun run() {
                                                    runOnUiThread { resume() }
                                                }
                                            }, 2000)
                                        }
                                        start()

                                    }
                                }
                                5 -> {

                                }
                                7 -> {

                                }
                                6 -> {
                                    if (star.starViews[6].visibility == View.VISIBLE) {
                                        ObjectAnimator.ofFloat(star.starViews[k], "alpha", 0.3f, 1f).apply {
                                            duration = 500
                                            repeatCount = 1
                                            repeatMode = ValueAnimator.REVERSE
                                            doOnRepeat {
                                                pause()
                                                Timer().schedule(object : TimerTask() {
                                                    override fun run() {
                                                        runOnUiThread { resume() }
                                                    }
                                                }, 2000)
                                            }
                                            start()
                                        }
                                    }
                                }
                                else -> {
                                    ObjectAnimator.ofFloat(star.starViews[k], "alpha", 0.3f, 1f).apply {
                                        duration = 500
                                        repeatCount = 1
                                        repeatMode = ValueAnimator.REVERSE
                                        doOnRepeat {
                                            pause()
                                            Timer().schedule(object : TimerTask() {
                                                override fun run() {
                                                    runOnUiThread { resume() }
                                                }
                                            }, 2000)
                                        }
                                        start()

                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
    private fun getSkyStatus() {
        database.child("users").child(firebaseUser!!.uid).child("constellations").addValueEventListener(
            object : ValueEventListener {
                override fun onCancelled(databaseError: DatabaseError) {
                    Log.w(ContentValues.TAG, "getUser:onCancelled", databaseError.toException())
                }

                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    Log.d("user", firebaseUser!!.uid)
                    constellations[0].stars[0].done = dataSnapshot.child(0.toString()).child("cstars").child(0.toString()).child("sdone").value.toString() == "true"
                    constellations[0].stars[1].done = dataSnapshot.child(0.toString()).child("cstars").child(1.toString()).child("sdone").value.toString() == "true"
                    constellations[0].stars[2].done = dataSnapshot.child(0.toString()).child("cstars").child(2.toString()).child("sdone").value.toString() == "true"
                    constellations[0].stars[3].done = dataSnapshot.child(0.toString()).child("cstars").child(3.toString()).child("sdone").value.toString() == "true"
                    constellations[0].stars[4].done = dataSnapshot.child(0.toString()).child("cstars").child(4.toString()).child("sdone").value.toString() == "true"
                    constellations[0].stars[0].intermediate = dataSnapshot.child(0.toString()).child("cstars").child(0.toString()).child("sintermediary").value.toString() == "true"
                    constellations[0].stars[1].intermediate = dataSnapshot.child(0.toString()).child("cstars").child(1.toString()).child("sintermediary").value.toString() == "true"
                    constellations[0].stars[2].intermediate = dataSnapshot.child(0.toString()).child("cstars").child(2.toString()).child("sintermediary").value.toString() == "true"
                    constellations[0].stars[3].intermediate = dataSnapshot.child(0.toString()).child("cstars").child(3.toString()).child("sintermediary").value.toString() == "true"
                    constellations[0].stars[4].intermediate = dataSnapshot.child(0.toString()).child("cstars").child(4.toString()).child("sintermediary").value.toString() == "true"
                    constellations[0].stars[0].timesCompleted = dataSnapshot.child(0.toString()).child("cstars").child(0.toString()).child("stimesCompleted").value as Long
                    constellations[0].stars[1].timesCompleted = dataSnapshot.child(0.toString()).child("cstars").child(1.toString()).child("stimesCompleted").value as Long
                    constellations[0].stars[2].timesCompleted = dataSnapshot.child(0.toString()).child("cstars").child(2.toString()).child("stimesCompleted").value as Long
                    constellations[0].stars[3].timesCompleted = dataSnapshot.child(0.toString()).child("cstars").child(3.toString()).child("stimesCompleted").value as Long
                    constellations[0].stars[4].timesCompleted = dataSnapshot.child(0.toString()).child("cstars").child(4.toString()).child("stimesCompleted").value as Long

                    constellations[1].stars[0].done = dataSnapshot.child(1.toString()).child("cstars").child(0.toString()).child("sdone").value.toString() == "true"
                    constellations[1].stars[1].done = dataSnapshot.child(1.toString()).child("cstars").child(1.toString()).child("sdone").value.toString() == "true"
                    constellations[1].stars[2].done = dataSnapshot.child(1.toString()).child("cstars").child(2.toString()).child("sdone").value.toString() == "true"
                    constellations[1].stars[3].done = dataSnapshot.child(1.toString()).child("cstars").child(3.toString()).child("sdone").value.toString() == "true"
                    constellations[1].stars[4].done = dataSnapshot.child(1.toString()).child("cstars").child(4.toString()).child("sdone").value.toString() == "true"
                    constellations[1].stars[0].intermediate = dataSnapshot.child(1.toString()).child("cstars").child(0.toString()).child("sintermediary").value.toString() == "true"
                    constellations[1].stars[1].intermediate = dataSnapshot.child(1.toString()).child("cstars").child(1.toString()).child("sintermediary").value.toString() == "true"
                    constellations[1].stars[2].intermediate = dataSnapshot.child(1.toString()).child("cstars").child(2.toString()).child("sintermediary").value.toString() == "true"
                    constellations[1].stars[3].intermediate = dataSnapshot.child(1.toString()).child("cstars").child(3.toString()).child("sintermediary").value.toString() == "true"
                    constellations[1].stars[4].intermediate = dataSnapshot.child(1.toString()).child("cstars").child(4.toString()).child("sintermediary").value.toString() == "true"
                    constellations[1].stars[0].timesCompleted = dataSnapshot.child(1.toString()).child("cstars").child(0.toString()).child("stimesCompleted").value as Long
                    constellations[1].stars[1].timesCompleted = dataSnapshot.child(1.toString()).child("cstars").child(1.toString()).child("stimesCompleted").value as Long
                    constellations[1].stars[2].timesCompleted = dataSnapshot.child(1.toString()).child("cstars").child(2.toString()).child("stimesCompleted").value as Long
                    constellations[1].stars[3].timesCompleted = dataSnapshot.child(1.toString()).child("cstars").child(3.toString()).child("stimesCompleted").value as Long
                    constellations[1].stars[4].timesCompleted = dataSnapshot.child(1.toString()).child("cstars").child(4.toString()).child("stimesCompleted").value as Long

                    constellations[2].stars[0].done = dataSnapshot.child(2.toString()).child("cstars").child(0.toString()).child("sdone").value.toString() == "true"
                    constellations[2].stars[1].done = dataSnapshot.child(2.toString()).child("cstars").child(1.toString()).child("sdone").value.toString() == "true"
                    constellations[2].stars[2].done = dataSnapshot.child(2.toString()).child("cstars").child(2.toString()).child("sdone").value.toString() == "true"
                    constellations[2].stars[3].done = dataSnapshot.child(2.toString()).child("cstars").child(3.toString()).child("sdone").value.toString() == "true"
                    constellations[2].stars[4].done = dataSnapshot.child(2.toString()).child("cstars").child(4.toString()).child("sdone").value.toString() == "true"
                    constellations[2].stars[5].done = dataSnapshot.child(2.toString()).child("cstars").child(5.toString()).child("sdone").value.toString() == "true"
                    constellations[2].stars[6].done = dataSnapshot.child(2.toString()).child("cstars").child(6.toString()).child("sdone").value.toString() == "true"
                    constellations[2].stars[7].done = dataSnapshot.child(2.toString()).child("cstars").child(7.toString()).child("sdone").value.toString() == "true"
                    constellations[2].stars[0].intermediate = dataSnapshot.child(2.toString()).child("cstars").child(0.toString()).child("sintermediary").value.toString() == "true"
                    constellations[2].stars[1].intermediate = dataSnapshot.child(2.toString()).child("cstars").child(1.toString()).child("sintermediary").value.toString() == "true"
                    constellations[2].stars[2].intermediate = dataSnapshot.child(2.toString()).child("cstars").child(2.toString()).child("sintermediary").value.toString() == "true"
                    constellations[2].stars[3].intermediate = dataSnapshot.child(2.toString()).child("cstars").child(3.toString()).child("sintermediary").value.toString() == "true"
                    constellations[2].stars[4].intermediate = dataSnapshot.child(2.toString()).child("cstars").child(4.toString()).child("sintermediary").value.toString() == "true"
                    constellations[2].stars[5].intermediate = dataSnapshot.child(2.toString()).child("cstars").child(5.toString()).child("sintermediary").value.toString() == "true"
                    constellations[2].stars[6].intermediate = dataSnapshot.child(2.toString()).child("cstars").child(6.toString()).child("sintermediary").value.toString() == "true"
                    constellations[2].stars[7].intermediate = dataSnapshot.child(2.toString()).child("cstars").child(7.toString()).child("sintermediary").value.toString() == "true"
                    constellations[2].stars[0].timesCompleted = dataSnapshot.child(2.toString()).child("cstars").child(0.toString()).child("stimesCompleted").value as Long
                    constellations[2].stars[1].timesCompleted = dataSnapshot.child(2.toString()).child("cstars").child(1.toString()).child("stimesCompleted").value as Long
                    constellations[2].stars[2].timesCompleted = dataSnapshot.child(2.toString()).child("cstars").child(2.toString()).child("stimesCompleted").value as Long
                    constellations[2].stars[3].timesCompleted = dataSnapshot.child(2.toString()).child("cstars").child(3.toString()).child("stimesCompleted").value as Long
                    constellations[2].stars[4].timesCompleted = dataSnapshot.child(2.toString()).child("cstars").child(4.toString()).child("stimesCompleted").value as Long
                    constellations[2].stars[5].timesCompleted = dataSnapshot.child(2.toString()).child("cstars").child(5.toString()).child("stimesCompleted").value as Long
                    constellations[2].stars[6].timesCompleted = dataSnapshot.child(2.toString()).child("cstars").child(6.toString()).child("stimesCompleted").value as Long
                    constellations[2].stars[7].timesCompleted = dataSnapshot.child(2.toString()).child("cstars").child(7.toString()).child("stimesCompleted").value as Long

                    constellations[3].stars[0].done = dataSnapshot.child("3").child("cstars").child(0.toString()).child("sdone").value.toString() == "true"
                    constellations[3].stars[1].done = dataSnapshot.child("3").child("cstars").child(1.toString()).child("sdone").value.toString() == "true"
                    constellations[3].stars[2].done = dataSnapshot.child("3").child("cstars").child(2.toString()).child("sdone").value.toString() == "true"
                    constellations[3].stars[3].done = dataSnapshot.child("3").child("cstars").child(3.toString()).child("sdone").value.toString() == "true"
                    constellations[3].stars[0].intermediate = dataSnapshot.child(3.toString()).child("cstars").child(0.toString()).child("sintermediary").value.toString() == "true"
                    constellations[3].stars[1].intermediate = dataSnapshot.child(3.toString()).child("cstars").child(1.toString()).child("sintermediary").value.toString() == "true"
                    constellations[3].stars[2].intermediate = dataSnapshot.child(3.toString()).child("cstars").child(2.toString()).child("sintermediary").value.toString() == "true"
                    constellations[3].stars[3].intermediate = dataSnapshot.child(3.toString()).child("cstars").child(3.toString()).child("sintermediary").value.toString() == "true"
                    constellations[3].stars[0].timesCompleted = dataSnapshot.child(3.toString()).child("cstars").child(0.toString()).child("stimesCompleted").value as Long
                    constellations[3].stars[1].timesCompleted = dataSnapshot.child(3.toString()).child("cstars").child(1.toString()).child("stimesCompleted").value as Long
                    constellations[3].stars[2].timesCompleted = dataSnapshot.child(3.toString()).child("cstars").child(2.toString()).child("stimesCompleted").value as Long
                    constellations[3].stars[3].timesCompleted = dataSnapshot.child(3.toString()).child("cstars").child(3.toString()).child("stimesCompleted").value as Long

                    constellations[4].stars[0].done = dataSnapshot.child("4").child("cstars").child(0.toString()).child("sdone").value.toString() == "true"
                    Log.d("eitah", constellations[4].stars[0].done.toString())
                    Log.d("eitah", constellations[4].stars[0].intermediate.toString())
                    constellations[4].stars[1].done = dataSnapshot.child("4").child("cstars").child(1.toString()).child("sdone").value.toString() == "true"
                    constellations[4].stars[2].done = dataSnapshot.child("4").child("cstars").child(2.toString()).child("sdone").value.toString() == "true"
                    constellations[4].stars[3].done = dataSnapshot.child("4").child("cstars").child(3.toString()).child("sdone").value.toString() == "true"
                    constellations[4].stars[0].intermediate = dataSnapshot.child(4.toString()).child("cstars").child(0.toString()).child("sintermediary").value.toString() == "true"
                    constellations[4].stars[1].intermediate = dataSnapshot.child(4.toString()).child("cstars").child(1.toString()).child("sintermediary").value.toString() == "true"
                    constellations[4].stars[2].intermediate = dataSnapshot.child(4.toString()).child("cstars").child(2.toString()).child("sintermediary").value.toString() == "true"
                    constellations[4].stars[3].intermediate = dataSnapshot.child(4.toString()).child("cstars").child(3.toString()).child("sintermediary").value.toString() == "true"
                    constellations[4].stars[0].timesCompleted = dataSnapshot.child(4.toString()).child("cstars").child(0.toString()).child("stimesCompleted").value as Long
                    constellations[4].stars[1].timesCompleted = dataSnapshot.child(4.toString()).child("cstars").child(1.toString()).child("stimesCompleted").value as Long
                    constellations[4].stars[2].timesCompleted = dataSnapshot.child(4.toString()).child("cstars").child(2.toString()).child("stimesCompleted").value as Long
                    constellations[4].stars[3].timesCompleted = dataSnapshot.child(4.toString()).child("cstars").child(3.toString()).child("stimesCompleted").value as Long

                    if (boolForRotateShow) {
                        rotateStarts()
                        showStars()
                        boolForRotateShow = false
                    }
                }
            }
        )
    }

    private fun updateSkyStatus(constellation: Int, star: Int, update: String, bool: Boolean) {
        when (update) {
            "done" -> {
                database.child("users").child(firebaseUser!!.uid).child("constellations").child(constellation.toString()).child("cstars").child(star.toString()).child("sdone").setValue(bool)
            }
            "intermediary" -> {
                database.child("users").child(firebaseUser!!.uid).child("constellations").child(constellation.toString()).child("cstars").child(star.toString()).child("sintermediary").setValue(bool)
            }
            "timesCompleted" -> {
                database.child("users").child(firebaseUser!!.uid).child("constellations").child(constellation.toString()).child("cstars").child(star.toString()).child("stimesCompleted").setValue(constellations[constellation].stars[star].timesCompleted+1)
            }
        }
    }

    private fun showStars() {
        for (i in constellations.indices) {
            for (j in constellations[i].stars.indices) {
                if (!constellations[i].stars[j].done && !constellations[i].stars[j].intermediate) {
                    constellations[i].stars[j].starViews[2].visibility = View.VISIBLE //Outter 1
                    constellations[i].stars[j].starViews[3].visibility = View.INVISIBLE //Mid 2
                    constellations[i].stars[j].starViews[4].visibility = View.INVISIBLE //Outter 2
                    constellations[i].stars[j].starViews[5].visibility = View.INVISIBLE //Bright
                    constellations[i].stars[j].starViews[6].visibility = View.INVISIBLE //Intermediary Outter

                } else if (!constellations[i].stars[j].done && constellations[i].stars[j].intermediate) {
                    constellations[i].stars[j].starViews[2].visibility = View.VISIBLE //Outter 1
                    constellations[i].stars[j].starViews[3].visibility = View.INVISIBLE //Mid 2
                    constellations[i].stars[j].starViews[4].visibility = View.VISIBLE //Outter 2
                    constellations[i].stars[j].starViews[4].rotation = -24f
                    constellations[i].stars[j].starViews[5].visibility = View.INVISIBLE //Bright
                    constellations[i].stars[j].starViews[6].visibility = View.INVISIBLE //Intermediary Outter

                } else if (constellations[i].stars[j].done && !constellations[i].stars[j].intermediate) {
                    Log.d("star23", i.toString() + j.toString())
                    constellations[i].stars[j].starViews[2].visibility = View.INVISIBLE //Outter 1
                    constellations[i].stars[j].starViews[3].visibility = View.VISIBLE //Mid 2
                    constellations[i].stars[j].starViews[3].alpha = 0.5f + ((constellations[i].stars[j].timesCompleted/10) - 0.1f)
                    constellations[i].stars[j].starViews[4].visibility = View.INVISIBLE //Outter 2
                    constellations[i].stars[j].starViews[5].visibility = View.VISIBLE //Bright
                    constellations[i].stars[j].starViews[6].visibility = View.INVISIBLE //Intermediary Outter

                    constellations[i].stars[j].starViews[0].scaleX = 1.2f
                    constellations[i].stars[j].starViews[0].scaleY = 1.2f
                    constellations[i].stars[j].starViews[1].scaleX = 1.2f
                    constellations[i].stars[j].starViews[1].scaleY = 1.2f
                    constellations[i].stars[j].starViews[3].scaleX = 1.2f
                    constellations[i].stars[j].starViews[3].scaleY = 1.2f

                } else {
                    constellations[i].stars[j].starViews[2].visibility = View.INVISIBLE //Outter 1
                    constellations[i].stars[j].starViews[3].visibility = View.VISIBLE //Mid 2
                    constellations[i].stars[j].starViews[3].alpha = 0.5f + ((constellations[i].stars[j].timesCompleted/10) - 0.1f)
                    constellations[i].stars[j].starViews[4].visibility = View.INVISIBLE //Outter 2
                    constellations[i].stars[j].starViews[5].visibility = View.VISIBLE //Bright
                    constellations[i].stars[j].starViews[6].visibility = View.VISIBLE //Intermediary Outter

                }
            }
        }
    }

    private fun addMenu() {
        menuButton = ImageView(this)
        menuButton.setImageResource(R.drawable.button_menu)
        menuButton.alpha = 0.5f
        menuButton.layoutParams = LinearLayout.LayoutParams(dpToPx(100), dpToPx(100))
        menuButton.x = view.layoutParams.width - dpToPxF(120f)
        menuButton.y = view.layoutParams.height - dpToPxF(130f)
        menuButton.isClickable = true
        sky.addView(menuButton)

        menuButton.setOnClickListener {
            Log.d("shot", "scren")
            val b = Screenshot().takescreenshotOfRootView(sky)
            Screenshot().storeScreenshot(b)
        }
    }
}
