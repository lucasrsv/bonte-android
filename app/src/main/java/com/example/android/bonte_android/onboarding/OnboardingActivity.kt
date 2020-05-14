package com.example.android.bonte_android.onboarding

import android.animation.*
import android.content.ContentValues
import android.content.SharedPreferences
import android.graphics.Path
import android.graphics.PointF
import android.graphics.Rect
import android.graphics.RectF
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.MotionEvent
import android.view.ScaleGestureDetector
import android.view.VelocityTracker
import android.view.View
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.animation.doOnEnd
import androidx.core.animation.doOnStart
import androidx.core.view.GestureDetectorCompat
import androidx.core.view.ViewCompat
import androidx.databinding.DataBindingUtil
import com.example.android.bonte_android.Language
import com.example.android.bonte_android.R
import com.example.android.bonte_android.changeStatusBarColor
import com.example.android.bonte_android.databinding.ActivityOnboardingBinding
import com.example.android.bonte_android.dpToPxD
import com.google.firebase.database.*
import kotlinx.android.synthetic.main.activity_onboarding.*
import kotlinx.android.synthetic.main.fragment_onboarding1.*
import kotlinx.android.synthetic.main.fragment_onboarding1.onboardingLayout
import kotlinx.android.synthetic.main.fragment_onboarding1.starBright
import kotlin.random.Random


class OnboardingActivity : AppCompatActivity() {

    private lateinit var binding: ActivityOnboardingBinding
    private lateinit var sharedPreferences: SharedPreferences
    private var firstTime: Boolean = true
    private lateinit var mScaleGestureDetector:ScaleGestureDetector
    private lateinit var mGestureDetector: GestureDetectorCompat
    private lateinit var fadeInAnim: Animation
    private lateinit var startArrow: ImageView
    private lateinit var startText: TextView
    private lateinit var welcomeText1: TextView
    private lateinit var welcomeText2: TextView
    private lateinit var title1: TextView
    private lateinit var description1: TextView
    private lateinit var actionText: TextView
    private lateinit var title2: TextView
    private lateinit var description2: TextView
    private lateinit var turnedOffStarButton: View
    private lateinit var starOutter2: View
    private lateinit var ballIndicator: ImageView
    private lateinit var database: DatabaseReference
    private lateinit var language: String
    private var timesClicked = 0
    private lateinit var particles: List<ImageView>
    private lateinit var size: List<Double>
    private lateinit var xRight: List<Int>
    private lateinit var yRight: List<Int>
    private lateinit var xLeft: List<Int>
    private lateinit var yLeft: List<Int>
    private lateinit var xTop: List<Int>
    private lateinit var yTop: List<Int>
    private lateinit var xBottom: List<Int>
    private lateinit var yBottom: List<Int>
    private lateinit var paths: Array<Path>


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        FirebaseDatabase.getInstance().setPersistenceEnabled(true)
        database= FirebaseDatabase.getInstance().reference
        binding = DataBindingUtil.setContentView(
            this,
            R.layout.activity_onboarding
        )
        welcomeText1 = binding.welcomeText
        welcomeText2 = binding.welcomeText2
        startArrow = binding.arrowUp
        startText = binding.startText
        turnedOffStarButton = binding.starOutter1
        starOutter2 = binding.starOutter2
        title1 = binding.title1
        description1 = binding.description1
        actionText = binding.firstAction
        ballIndicator = binding.ballIndicator
        setTexts()
        /*sharedPreferences = getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)
        firstTime = sharedPreferences.getBoolean("FirstTime", true)
        if (!firstTime) {} else {
            setContentView(R.layout.activity_onboarding)
            var editor: SharedPreferences.Editor = sharedPreferences.edit()
            editor.putBoolean("FirstTIme", false)
            editor.commit()
        }*/


        changeStatusBarColor()

    }
    private fun fadeInAnimation() {

        val shake = AnimationUtils.loadAnimation(this, R.anim.shake_star_onboarding)

        val fadeIn1 = ObjectAnimator.ofFloat(welcomeText1, "alpha", 0f, 1.0f).apply {
            duration = 1250

        }

        val fadeIn2 = ObjectAnimator.ofFloat(welcomeText2, "alpha", 0f, 1.0f).apply {
            duration = 1250
            addListener(object : AnimatorListenerAdapter() {
                override fun onAnimationEnd(animation: Animator) {
                    startArrow.visibility = View.VISIBLE
                    startText.visibility = View.VISIBLE
                }
            })
        }

        val fadeIn3 = ObjectAnimator.ofFloat(startText, "alpha", 0f, 1.0f).apply {
            duration = 1000
        }

        val fadeIn4 = ObjectAnimator.ofFloat(startArrow, "alpha", 0f, 1.0f).apply {
            duration = 1000
            doOnEnd {
                turnedOffStarButton.setOnClickListener {
                    if (timesClicked == 0) {
                        val rotateOutter = ObjectAnimator.ofFloat(starOutter2, "rotation", 0f, 26f)
                        rotateOutter.duration = 500

                        val fadeOutWelcomeText1 =
                            ObjectAnimator.ofFloat(welcomeText1, "alpha", 1f, 0f)
                        fadeOutWelcomeText1.duration = 500

                        val fadeOutWelcomeText2 =
                            ObjectAnimator.ofFloat(welcomeText2, "alpha", 1f, 0f)
                        fadeOutWelcomeText2.duration = 500

                        val fadeOutStartText = ObjectAnimator.ofFloat(startText, "alpha", 1f, 0f)
                        fadeOutWelcomeText2.duration = 500

                        val fadeOutStartArrow = ObjectAnimator.ofFloat(startArrow, "alpha", 1f, 0f)
                        fadeOutWelcomeText2.duration = 500


                        val fadeIn1 = ObjectAnimator.ofFloat(title1, "alpha", 0f, 1.0f).apply {
                            duration = 1000
                            doOnStart {
                                binding.beamLightView.run()
                                binding.starLineView.run()
                            }

                        }
                        val fadeIn2 =
                            ObjectAnimator.ofFloat(description1, "alpha", 0f, 1.0f).apply {
                                duration = 1000
                            }

                        val fadeIn3 =
                            ObjectAnimator.ofFloat(actionText, "alpha", 0.0f, 1.0f).apply {
                                duration = 1000
                                startDelay = 2000
                            }

                        val fadeIn4 =
                            ObjectAnimator.ofFloat(ballIndicator, "alpha", 0.0f, 1.0f).apply {
                                duration = 1000
                                startDelay = 2000
                                doOnEnd {
                                    addParticles()
                                    turnedOffStarButton.setOnLongClickListener {
                                        var xInner = PropertyValuesHolder.ofFloat(View.SCALE_X, 1f, 2.5f)
                                        var yInner = PropertyValuesHolder.ofFloat(View.SCALE_Y, 1f, 2.5f)
                                        var xOutter = PropertyValuesHolder.ofFloat(View.SCALE_X, 1f, 2.3f)
                                        var yOutter = PropertyValuesHolder.ofFloat(View.SCALE_Y, 1f, 2.3f)
                                        var xOutterReturn = PropertyValuesHolder.ofFloat(View.SCALE_X, 2.3f, 0f)
                                        var yOutterReturn = PropertyValuesHolder.ofFloat(View.SCALE_Y, 2.3f, 0f)
                                        var xMid = PropertyValuesHolder.ofFloat(View.SCALE_X, 1f, 2.5f)
                                        var yMid = PropertyValuesHolder.ofFloat(View.SCALE_Y, 1f, 2.5f)
                                        var xMid2 = PropertyValuesHolder.ofFloat(View.SCALE_X, 0f, 3.5f)
                                        var yMid2 = PropertyValuesHolder.ofFloat(View.SCALE_Y, 0f, 3.5f)
                                        var xBright = PropertyValuesHolder.ofFloat(View.SCALE_X, 0f, 5f)
                                        var yBright = PropertyValuesHolder.ofFloat(View.SCALE_Y, 0f, 5f)
                                        var xInnerReturn = PropertyValuesHolder.ofFloat(View.SCALE_X, 2.5f, 1.5f)
                                        var yInnerReturn = PropertyValuesHolder.ofFloat(View.SCALE_Y, 2.5f, 1.5f)
                                        var xMidReturn = PropertyValuesHolder.ofFloat(View.SCALE_X, 2.5f, 1.5f)
                                        var yMidReturn = PropertyValuesHolder.ofFloat(View.SCALE_Y, 2.5f, 1.5f)
                                        var xMid2Return = PropertyValuesHolder.ofFloat(View.SCALE_X, 3.5f, 2.2f)
                                        var yMid2Return = PropertyValuesHolder.ofFloat(View.SCALE_Y, 3.5f, 2.2f)
                                        var xBrightReturn = PropertyValuesHolder.ofFloat(View.SCALE_X, 5f, 4f)
                                        var yBrightReturn = PropertyValuesHolder.ofFloat(View.SCALE_Y, 5f, 4f)
                                        val time = 500L
                                        val scaleInner = ObjectAnimator.ofPropertyValuesHolder(binding.starInner, xInner, yInner).apply {
                                            duration = 1000
                                            startDelay = time
                                            doOnStart {
                                                particlesExplosion()
                                            }
                                        }
                                        val scaleMid = ObjectAnimator.ofPropertyValuesHolder(binding.starMid, xMid, yMid).apply {
                                            duration = 1000
                                            startDelay = time
                                        }
                                        val scaleMid2 = ObjectAnimator.ofPropertyValuesHolder(
                                            binding.starMid2,
                                            xMid2,
                                            yMid2
                                        ).apply {
                                            duration = 1500
                                            doOnStart {
                                                binding.starMid2.alpha = 0.3f
                                            }
                                        }
                                        val scaleOutter = ObjectAnimator.ofPropertyValuesHolder(
                                            binding.starOutter1,
                                            xOutter,
                                            yOutter
                                        ).apply {
                                            duration = 1000
                                            startDelay = time
                                        }
                                        val scaleOutter2 = ObjectAnimator.ofPropertyValuesHolder(
                                            binding.starOutter2,
                                            xOutter,
                                            yOutter
                                        ).apply {
                                            duration = 1000
                                            startDelay = time
                                        }

                                        val scaleBright = ObjectAnimator.ofPropertyValuesHolder(
                                            binding.starBright,
                                            xBright,
                                            yBright
                                        ).apply {
                                            duration = 1000
                                            startDelay = time
                                            var turnedVisible = false
                                            addUpdateListener { animation ->
                                                if (animation.animatedValue as Float > 0f && !turnedVisible) {
                                                    turnedVisible = true
                                                    starBright.visibility = View.VISIBLE
                                                }
                                            }
                                        }

                                        val scaleDownOutter = ObjectAnimator.ofPropertyValuesHolder(binding.starOutter1, xOutterReturn, yOutterReturn).apply {
                                            duration = 300
                                            doOnEnd {
                                                binding.starOutter1.visibility = View.GONE
                                                binding.starOutter2.visibility = View.GONE
                                            }

                                        }
                                        val scaleDownOutter2 = ObjectAnimator.ofPropertyValuesHolder(binding.starOutter2, xOutterReturn, yOutterReturn).apply {
                                            duration = 300
                                        }
                                        val scaleDownInner = ObjectAnimator.ofPropertyValuesHolder(
                                            binding.starInner,
                                            xInnerReturn,
                                            yInnerReturn
                                        ).apply {
                                            duration = 1000
                                            startDelay = 500
                                        }
                                        val scaleDownMid = ObjectAnimator.ofPropertyValuesHolder(
                                            binding.starMid,
                                            xMidReturn,
                                            yMidReturn
                                        ).apply {
                                            duration = 1000
                                            startDelay = 500
                                        }
                                        val scaleDownMid2 = ObjectAnimator.ofPropertyValuesHolder(
                                            binding.starMid2,
                                            xMid2Return,
                                            yMid2Return
                                        ).apply {
                                            duration = 1000
                                            startDelay = 500
                                        }
                                        val scaleDownBright = ObjectAnimator.ofPropertyValuesHolder(
                                            binding.starBright,
                                            xBrightReturn,
                                            yBrightReturn
                                        ).apply {
                                            duration = 1000
                                            startDelay = 500
                                        }
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
                                            binding.beamLightView.undo()
                                            binding.starLineView.undo()
                                            binding.starInner.startAnimation(shake)
                                            binding.starMid.startAnimation(shake)
                                            binding.starOutter1.startAnimation(shake)
                                            binding.starOutter2.startAnimation(shake)
                                            binding.starMid2.startAnimation(shake)

                                            runOnUiThread {
                                                Handler().postDelayed({
                                                    binding.beamLightView.visibility = View.GONE
                                                    binding.starLineView.visibility = View.GONE
                                                }, 500)
                                            }
                                        }
                                        return@setOnLongClickListener true
                                    }
                                }
                            }


                        AnimatorSet().apply {
                            playTogether(
                                rotateOutter,
                                fadeOutWelcomeText1,
                                fadeOutWelcomeText2,
                                fadeOutStartText,
                                fadeOutStartArrow
                            )
                            playTogether(fadeIn1, fadeIn2, fadeIn3, fadeIn4)
                            play(fadeIn2).after(rotateOutter)
                            start()
                        }
                        timesClicked = 1
                    }
                }
            }
        }

        AnimatorSet().apply {
            playTogether(fadeIn1, fadeIn2, fadeIn3, fadeIn4)
            start()
        }

    }

    private fun addParticles() {

        particles = List(100) { ImageView(this) }
        size = List(100) { Random.nextDouble(2.0, 5.0) }
        xRight = List(25) { Random.nextInt(( onboarding.width/2 + (onboarding.width * 0.2)).toInt(), (onboarding.width/2 + (onboarding.width * 0.4)).toInt()) }
        yRight = List(25) { ((onboarding.height/2 - (onboarding.height * 0.1)).toInt() until (onboarding.height/2 + (onboarding.height/2 * 0.25)).toInt() ).random() }
        xLeft = List(25) { Random.nextInt((onboarding.width/2 - (onboarding.width* 0.4)).toInt(), (onboarding.width/2- (onboarding.width * 0.15)).toInt()) }
        yLeft = List(25) { Random.nextInt((onboarding.height/2 - (onboarding.height*0.2)).toInt(), (onboarding.height/2+ (onboarding.height * 0.3)).toInt()) }
        xTop = List(25) { Random.nextInt((onboarding.width/2 - (onboarding.width * 0.4)).toInt(), (onboarding.width/2 + (onboarding.width * 0.4)).toInt()) }
        yTop = List(25) { Random.nextInt((onboarding.height/2 - (onboarding.height * 0.3)).toInt(), (onboarding.height/2 - (onboarding.height * 0.05)).toInt()) }
        xBottom = List(25) { Random.nextInt((onboarding.width/2 - (onboarding.width * 0.4)).toInt(), (onboarding.width/2 + (onboarding.width * 0.4)).toInt()) }
        yBottom = List(25) { Random.nextInt((onboarding.height/2 + (onboarding.height * 0.07)).toInt(), (onboarding.height/2 + (onboarding.height * 0.24)).toInt()) }
        paths = Array(100) { Path() }
        for (i in particles.indices) {
            particles[i].setImageResource(R.drawable.star_circle)
            particles[i].layoutParams = LinearLayout.LayoutParams(dpToPxD(size[i]), dpToPxD(size[i]))
            particles[i].x = onboarding.width / 2f
            particles[i].y = onboarding.height / 2f
            binding.onboarding.addView(particles[i])
        }
    }

    private fun particlesExplosion() {
        for (i in particles.indices) {
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
                        start()
                    }
                }
            }
        }
        for (i in particles.indices) {
            ObjectAnimator.ofFloat(particles[i], View.X, View.Y, paths[i]).apply {
                duration = Random.nextLong(500, 1000)
                startDelay = 1600
                doOnEnd {
                    binding.onboarding.removeView(particles[i])
                }
                start()
            }
        }
    }


    private fun setTexts() {
        language = if (Language().language == "pt") {
            "pt"
        } else {
            "en"
        }
        database.child(language).child("onboarding").child("onboarding1").addListenerForSingleValueEvent(
            object : ValueEventListener {
                override fun onCancelled(databaseError: DatabaseError) {
                    Log.w(ContentValues.TAG, "getUser:onCancelled", databaseError.toException())
                }

                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    welcomeText1.text = dataSnapshot.child("welcomeTitle").value as String
                    welcomeText2.text = dataSnapshot.child("welcomeDescription").value as String
                    startText.text = dataSnapshot.child("startText").value as String
                    actionText.text = dataSnapshot.child("starAction").value as String
                    title1.text = dataSnapshot.child("title").value as String
                    description1.text = dataSnapshot.child("description").value as String
                    fadeInAnimation()

                }
            }
        )
    }
}