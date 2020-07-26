package com.example.android.bonte_android.onboarding

import android.animation.*
import android.annotation.SuppressLint
import android.app.ActivityManager
import android.content.*
import android.graphics.*
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.IBinder
import android.util.DisplayMetrics
import android.util.Log
import android.view.*
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.view.animation.LinearInterpolator
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.animation.doOnEnd
import androidx.core.animation.doOnPause
import androidx.core.animation.doOnRepeat
import androidx.core.animation.doOnStart
import androidx.core.view.GestureDetectorCompat
import androidx.core.view.ViewCompat
import androidx.databinding.DataBindingUtil
import com.example.android.bonte_android.*
import com.example.android.bonte_android.R
import com.example.android.bonte_android.databinding.ActivityOnboardingBinding
import com.example.android.bonte_android.sky.BackgroundSongService
import com.example.android.bonte_android.sky.Screenshot
import com.example.android.bonte_android.sky.SkyActivity
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import kotlinx.android.synthetic.main.activity_onboarding.*
import java.lang.Exception
import java.util.*
import kotlin.random.Random


class OnboardingActivity : AppCompatActivity() {

    private lateinit var binding: ActivityOnboardingBinding
    private lateinit var startArrow: ImageView
    private lateinit var startText: TextView
    private lateinit var welcomeText1: TextView
    private lateinit var welcomeText2: TextView
    private lateinit var title1: TextView
    private lateinit var description1: TextView
    private lateinit var actionText: TextView
    private lateinit var turnedOffStarButton: View
    private lateinit var starOutter2: View
    private lateinit var ballIndicator: ImageView
    private lateinit var database: DatabaseReference
    private lateinit var language: String
    private lateinit var view: View
    private lateinit var params: ViewGroup.LayoutParams
    private var backgroundSongService: BackgroundSongService? = null
    private var connection: ServiceConnection? = null
    private var fadedFirstTexts = false
    private var startingLoginActivity = false
    private var firstTime = true
    private var timesClicked = 0

    //Check firebase login

    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var authStateListener: FirebaseAuth.AuthStateListener
    private lateinit var gso: GoogleSignInOptions

    //

    private lateinit var particles: List<ImageView>
    private lateinit var  size: List<Double>
    private lateinit var  displayMetrics: DisplayMetrics
    private lateinit var  xRight: List<Int>
    private lateinit var  yRight:  List<Int>
    private lateinit var  xLeft:  List<Int>
    private lateinit var  yLeft:  List<Int>
    private lateinit var  xTop:  List<Int>
    private lateinit var  yTop:  List<Int>
    private lateinit var  xBottom:  List<Int>
    private lateinit var  yBottom:  List<Int>
    private lateinit var  paths: Array<Path>
    private lateinit var starRotation: ObjectAnimator
    private var time = 0L

    override fun onCreate(savedInstanceState: Bundle?) {
        setTheme(R.style.AppTheme)
        super.onCreate(savedInstanceState)
        gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build()
        firebaseAuth = FirebaseAuth.getInstance()
        checkLogin()
    }

    override fun onStart() {
        super.onStart()
        firebaseAuth.addAuthStateListener(authStateListener)
    }

    override fun onPause() {
        firebaseAuth.removeAuthStateListener(authStateListener)
        super.onPause()
    }

    override fun onStop() {
        if (!startingLoginActivity) {
            Log.d("aabd", "ué")
            backgroundSongService?.run {
                if (isPlaying()) {
                    pauseMusic()
                }
            }
        }
        super.onStop()
    }

    override fun onDestroy() {
        firebaseAuth.removeAuthStateListener(authStateListener)
        super.onDestroy()
    }

    override fun onResume() {
        super.onResume()
        backgroundSongService?.resumeMusic()
    }

    override fun onWindowFocusChanged(hasFocus: Boolean) {
        super.onWindowFocusChanged(hasFocus)
        if (!fadedFirstTexts) {
            //Need to make a better usage of lateinit (with nullable property)
            //fadeInAnimation()
            fadedFirstTexts = true 
        }
    }

    private fun backgroundSong() {
        connection = object : ServiceConnection {
            override fun onServiceConnected(className: ComponentName?, service: IBinder?) {
                val binder = service as BackgroundSongService.LocalBinder
                backgroundSongService = binder.getService()
                backgroundSongService?.startSong()
            }

            override fun onServiceDisconnected(p0: ComponentName?) {
                Log.e("service status:", "onServiceDisconnected")
            }
        }

        Intent(this, BackgroundSongService::class.java).also { intent ->
            bindService(intent, connection as ServiceConnection, Context.BIND_AUTO_CREATE)
        }

        if ((!intent.hasExtra("EXTRA_SONG_SERVICE_ON"))) {
            Log.d("uékcarai"," kk")
            startService(Intent(this, BackgroundSongService::class.java))
        }
    }

    private fun loadOnboarding() {
        //database = FirebaseDatabase.getInstance().reference
        //database.keepSynced(true)
        firebaseAuth.addAuthStateListener(authStateListener)

        binding = DataBindingUtil.setContentView(
            this,
            R.layout.activity_onboarding
        )

        changeStatusBarColor()
        backgroundSong()
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
        view = binding.onboarding
        params = view.layoutParams
        rotateStar()
        addParticlesExplosion()
        addSkyParticles()
        setTexts()
        fadeInAnimation()
        backgroundSong()
    }

    private fun checkLogin() {
        authStateListener =  FirebaseAuth.AuthStateListener {
            var firebaseUser = firebaseAuth.currentUser
            if (firebaseUser != null) {
                if (intent.hasExtra("EXTRA_SONG_SERVICE_ON")) {
                    intent.removeExtra("EXTRA_SONG_SERVICE_ON")
                }
                val intent = Intent(baseContext, SkyActivity::class.java)
                intent.putExtra("EXTRA_SONG_SERVICE", "DISABLED")
                intent.putExtra("EXTRA_STARTING_SKY_ACTIVITY", "1")
                connection?.let { unbindService(it) }
                startActivity(intent)
                window.exitTransition = null
                overridePendingTransition(0, 0);
                finish()
            } else {
                if (firstTime) {
                    loadOnboarding()
                    firstTime = false
                }
            }
        }
    }

    private fun rotateStar() {
        var pvhR = PropertyValuesHolder.ofFloat(View.ROTATION, 0f, 360f)
        starRotation = ObjectAnimator.ofPropertyValuesHolder(turnedOffStarButton, pvhR).apply {
            duration = 15000
            repeatCount = ValueAnimator.INFINITE
            repeatMode = ValueAnimator.RESTART
            interpolator = LinearInterpolator()
            start()
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    private fun fadeInAnimation() {
        val shake = AnimationUtils.loadAnimation(this, R.anim.shake_star_onboarding)

        val fadeIn1 = ObjectAnimator.ofFloat(welcomeText1, "alpha", 0f, 1.0f).apply {
            duration = 1250
            doOnStart {
                welcomeText1.visibility = View.VISIBLE
            }
        }

        val fadeIn2 = ObjectAnimator.ofFloat(welcomeText2, "alpha", 0f, 1.0f).apply {
            duration = 1250
            doOnStart {
                welcomeText2.visibility = View.VISIBLE
            }
        }

        val fadeIn3 = ObjectAnimator.ofFloat(startText, "alpha", 0f, 1.0f).apply {
            duration = 1250
            doOnStart {
                startText.visibility = View.VISIBLE
            }
        }

        val fadeIn4 = ObjectAnimator.ofFloat(startArrow, "alpha", 0f, 1.0f).apply {
            duration = 1250
            doOnStart {
                startArrow.visibility = View.VISIBLE
            }
            doOnEnd {
                turnedOffStarButton.setOnClickListener {
                    if (timesClicked == 0) {
                        var initialValue = 0f
                        var rotateOutter = ObjectAnimator.ofFloat(starOutter2, "rotation", initialValue, initialValue + 24f)
                        starRotation.doOnPause {
                            initialValue = starRotation.animatedValue as Float
                            Log.d("valorr", (starRotation.animatedValue as Float).toString())

                            rotateOutter = ObjectAnimator.ofFloat(starOutter2, "rotation", initialValue, initialValue + 26f).apply {
                                duration = 500
                                doOnStart {
                                    starOutter2.visibility = View.VISIBLE
                                }
                            }
                        }
                        starRotation.pause()

                        val fadeOutWelcomeText1 = ObjectAnimator.ofFloat(welcomeText1, "alpha", 1f, 0f)
                        fadeOutWelcomeText1.duration = 500

                        val fadeOutWelcomeText2 = ObjectAnimator.ofFloat(welcomeText2, "alpha", 1f, 0f)
                        fadeOutWelcomeText2.duration = 500

                        val fadeOutStartText = ObjectAnimator.ofFloat(startText, "alpha", 1f, 0f)
                        fadeOutWelcomeText2.duration = 500

                        val fadeOutStartArrow = ObjectAnimator.ofFloat(startArrow, "alpha", 1f, 0f)
                        fadeOutWelcomeText2.duration = 500


                        val fadeInTitle1= ObjectAnimator.ofFloat(title1, "alpha", 0f, 1.0f).apply {
                            duration = 1000
                            doOnStart {
                                binding.beamLightView.run()
                                binding.starLineView.run()
                            }

                        }
                        val fadeInDesc1 =
                            ObjectAnimator.ofFloat(description1, "alpha", 0f, 1.0f).apply {
                                duration = 1000
                            }

                        val fadeInActionText =
                            ObjectAnimator.ofFloat(actionText, "alpha", 0.0f, 1.0f).apply {
                                duration = 1000
                                startDelay = 2000
                            }

                        val fadeInBallIndicator =
                            ObjectAnimator.ofFloat(ballIndicator, "alpha", 0.0f, 1.0f).apply {
                                duration = 1000
                                startDelay = 2100
                                doOnEnd {
                                    var longTouchHappened = false
                                    turnedOffStarButton.setOnTouchListener { view, motionEvent ->
                                        when (motionEvent.actionMasked) {
                                            MotionEvent.ACTION_DOWN -> {
                                                time = motionEvent.downTime
                                                Log.d("time", time.toString())
                                                binding.beamLightView.undo()
                                                true
                                            }
                                            MotionEvent.ACTION_UP -> {
                                                val totalTime = motionEvent.eventTime - time
                                                Log.d("totaltime", totalTime.toString())
                                                if (totalTime >= 400) {
                                                    if (!longTouchHappened) {
                                                        longTouchHappened = true
                                                        onboarding.removeView(binding.beamLightView)
                                                    var xInner = PropertyValuesHolder.ofFloat(
                                                        View.SCALE_X,
                                                        1f,
                                                        2.5f
                                                    )
                                                    var yInner = PropertyValuesHolder.ofFloat(
                                                        View.SCALE_Y,
                                                        1f,
                                                        2.5f
                                                    )
                                                    var xOutter = PropertyValuesHolder.ofFloat(
                                                        View.SCALE_X,
                                                        1f,
                                                        2.3f
                                                    )
                                                    var yOutter = PropertyValuesHolder.ofFloat(
                                                        View.SCALE_Y,
                                                        1f,
                                                        2.3f
                                                    )
                                                    var xOutterReturn =
                                                        PropertyValuesHolder.ofFloat(
                                                            View.SCALE_X,
                                                            2.3f,
                                                            0f
                                                        )
                                                    var yOutterReturn =
                                                        PropertyValuesHolder.ofFloat(
                                                            View.SCALE_Y,
                                                            2.3f,
                                                            0f
                                                        )
                                                    var xMid = PropertyValuesHolder.ofFloat(
                                                        View.SCALE_X,
                                                        1f,
                                                        2.5f
                                                    )
                                                    var yMid = PropertyValuesHolder.ofFloat(
                                                        View.SCALE_Y,
                                                        1f,
                                                        2.5f
                                                    )
                                                    var xMid2 = PropertyValuesHolder.ofFloat(
                                                        View.SCALE_X,
                                                        0f,
                                                        3.5f
                                                    )
                                                    var yMid2 = PropertyValuesHolder.ofFloat(
                                                        View.SCALE_Y,
                                                        0f,
                                                        3.5f
                                                    )
                                                    var xBright = PropertyValuesHolder.ofFloat(
                                                        View.SCALE_X,
                                                        0f,
                                                        5f
                                                    )
                                                    var yBright = PropertyValuesHolder.ofFloat(
                                                        View.SCALE_Y,
                                                        0f,
                                                        5f
                                                    )
                                                    var xInnerReturn = PropertyValuesHolder.ofFloat(
                                                        View.SCALE_X,
                                                        2.5f,
                                                        1.5f
                                                    )
                                                    var yInnerReturn = PropertyValuesHolder.ofFloat(
                                                        View.SCALE_Y,
                                                        2.5f,
                                                        1.5f
                                                    )
                                                    var xMidReturn = PropertyValuesHolder.ofFloat(
                                                        View.SCALE_X,
                                                        2.5f,
                                                        1.5f
                                                    )
                                                    var yMidReturn = PropertyValuesHolder.ofFloat(
                                                        View.SCALE_Y,
                                                        2.5f,
                                                        1.5f
                                                    )
                                                    var xMid2Return = PropertyValuesHolder.ofFloat(
                                                        View.SCALE_X,
                                                        3.5f,
                                                        2.2f
                                                    )
                                                    var yMid2Return = PropertyValuesHolder.ofFloat(
                                                        View.SCALE_Y,
                                                        3.5f,
                                                        2.2f
                                                    )
                                                    var xBrightReturn =
                                                        PropertyValuesHolder.ofFloat(
                                                            View.SCALE_X,
                                                            5f,
                                                            4f
                                                        )
                                                    var yBrightReturn =
                                                        PropertyValuesHolder.ofFloat(
                                                            View.SCALE_Y,
                                                            5f,
                                                            4f
                                                        )
                                                    val time = 500L
                                                    val scaleInner =
                                                        ObjectAnimator.ofPropertyValuesHolder(
                                                            binding.starInner,
                                                            xInner,
                                                            yInner
                                                        ).apply {
                                                            duration = 1000
                                                            startDelay = time
                                                            doOnStart {
                                                                starParticlesExplosion()
                                                            }
                                                        }
                                                    val scaleMid =
                                                        ObjectAnimator.ofPropertyValuesHolder(
                                                            binding.starMid,
                                                            xMid,
                                                            yMid
                                                        ).apply {
                                                            duration = 1000
                                                            startDelay = time
                                                        }
                                                    val scaleMid2 =
                                                        ObjectAnimator.ofPropertyValuesHolder(
                                                            binding.starMid2,
                                                            xMid2,
                                                            yMid2
                                                        ).apply {
                                                            duration = 1500
                                                            doOnStart {
                                                                binding.starMid2.alpha = 0.6f
                                                            }
                                                        }
                                                    val scaleOutter =
                                                        ObjectAnimator.ofPropertyValuesHolder(
                                                            binding.starOutter1,
                                                            xOutter,
                                                            yOutter
                                                        ).apply {
                                                            duration = 1000
                                                            startDelay = time
                                                        }
                                                    val scaleOutter2 =
                                                        ObjectAnimator.ofPropertyValuesHolder(
                                                            binding.starOutter2,
                                                            xOutter,
                                                            yOutter
                                                        ).apply {
                                                            duration = 1000
                                                            startDelay = time
                                                        }

                                                    val scaleBright =
                                                        ObjectAnimator.ofPropertyValuesHolder(
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
                                                                    starBright.visibility =
                                                                        View.VISIBLE
                                                                }
                                                            }
                                                        }

                                                    val scaleDownOutter =
                                                        ObjectAnimator.ofPropertyValuesHolder(
                                                            binding.starOutter1,
                                                            xOutterReturn,
                                                            yOutterReturn
                                                        ).apply {
                                                            duration = 300
                                                            doOnEnd {
                                                                binding.starOutter1.visibility =
                                                                    View.GONE
                                                                binding.starOutter2.visibility =
                                                                    View.GONE
                                                            }

                                                        }
                                                    val scaleDownOutter2 =
                                                        ObjectAnimator.ofPropertyValuesHolder(
                                                            binding.starOutter2,
                                                            xOutterReturn,
                                                            yOutterReturn
                                                        ).apply {
                                                            duration = 300
                                                        }
                                                    val scaleDownInner =
                                                        ObjectAnimator.ofPropertyValuesHolder(
                                                            binding.starInner,
                                                            xInnerReturn,
                                                            yInnerReturn
                                                        ).apply {
                                                            duration = 1000
                                                            startDelay = 500
                                                        }
                                                    val scaleDownMid =
                                                        ObjectAnimator.ofPropertyValuesHolder(
                                                            binding.starMid,
                                                            xMidReturn,
                                                            yMidReturn
                                                        ).apply {
                                                            duration = 1000
                                                            startDelay = 500
                                                        }
                                                    val scaleDownMid2 =
                                                        ObjectAnimator.ofPropertyValuesHolder(
                                                            binding.starMid2,
                                                            xMid2Return,
                                                            yMid2Return
                                                        ).apply {
                                                            duration = 1000
                                                            startDelay = 500
                                                        }
                                                    val scaleDownBright =
                                                        ObjectAnimator.ofPropertyValuesHolder(
                                                            binding.starBright,
                                                            xBrightReturn,
                                                            yBrightReturn
                                                        ).apply {
                                                            duration = 1000
                                                            startDelay = 500
                                                        }

                                                        val fadeOutTitle =
                                                            ObjectAnimator.ofFloat(
                                                                title1,
                                                                "alpha",
                                                                1.0f,
                                                                0f
                                                            ).apply {
                                                                duration = 500
                                                            }
                                                        val fadeOutDescription =
                                                            ObjectAnimator.ofFloat(
                                                                description1,
                                                                "alpha",
                                                                1.0f,
                                                                0f
                                                            ).apply {
                                                                duration = 500
                                                            }
                                                        val fadeOutStarAction =
                                                            ObjectAnimator.ofFloat(
                                                                actionText,
                                                                "alpha",
                                                                1.0f,
                                                                0f
                                                            ).apply {
                                                                duration = 500
                                                            }
                                                        val fadeOutStarCircle =
                                                            ObjectAnimator.ofFloat(
                                                                ballIndicator,
                                                                "alpha",
                                                                1.0f,
                                                                0f
                                                            ).apply {
                                                                duration = 500
                                                            }

                                                    val fadeStar0 = ObjectAnimator.ofFloat(
                                                        binding.star0,
                                                        "alpha",
                                                        0.3f,
                                                        1.0f
                                                    ).apply {
                                                        duration = 1500
                                                        repeatMode = ValueAnimator.REVERSE
                                                        repeatCount = 1
                                                        doOnRepeat {
                                                            pause()
                                                            Timer().schedule(object : TimerTask() {
                                                                override fun run() {
                                                                    runOnUiThread { resume() }
                                                                }
                                                            }, 1000)
                                                        }
                                                    }
                                                    val fadeStar1 = ObjectAnimator.ofFloat(
                                                        binding.star1,
                                                        "alpha",
                                                        0.3f,
                                                        1.0f
                                                    ).apply {
                                                        duration = 1500
                                                        repeatMode = ValueAnimator.REVERSE
                                                        repeatCount = 1
                                                        doOnRepeat {
                                                            pause()
                                                            Timer().schedule(object : TimerTask() {
                                                                override fun run() {
                                                                    runOnUiThread { resume() }
                                                                }
                                                            }, 1000)
                                                        }
                                                    }
                                                    val fadeStar2 = ObjectAnimator.ofFloat(
                                                        binding.star2,
                                                        "alpha",
                                                        0.3f,
                                                        1.0f
                                                    ).apply {
                                                        duration = 1500
                                                        repeatMode = ValueAnimator.REVERSE
                                                        repeatCount = 1
                                                        doOnRepeat {
                                                            pause()
                                                            Timer().schedule(object : TimerTask() {
                                                                override fun run() {
                                                                    runOnUiThread { resume() }
                                                                }
                                                            }, 1000)
                                                        }
                                                    }
                                                    val fadeStar3 = ObjectAnimator.ofFloat(
                                                        binding.star3,
                                                        "alpha",
                                                        0.3f,
                                                        1.0f
                                                    ).apply {
                                                        duration = 1500
                                                        repeatMode = ValueAnimator.REVERSE
                                                        repeatCount = 1
                                                        doOnRepeat {
                                                            pause()
                                                            Timer().schedule(object : TimerTask() {
                                                                override fun run() {
                                                                    runOnUiThread { resume() }
                                                                }
                                                            }, 1000)
                                                        }
                                                    }
                                                    val fadeStar4 = ObjectAnimator.ofFloat(
                                                        binding.star4,
                                                        "alpha",
                                                        0.3f,
                                                        1.0f
                                                    ).apply {
                                                        duration = 1500
                                                        repeatMode = ValueAnimator.REVERSE
                                                        repeatCount = 1
                                                        doOnRepeat {
                                                            pause()
                                                            Timer().schedule(object : TimerTask() {
                                                                override fun run() {
                                                                    runOnUiThread { resume() }
                                                                }
                                                            }, 1000)
                                                        }
                                                    }
                                                    val fadeStar5 = ObjectAnimator.ofFloat(
                                                        binding.star5,
                                                        "alpha",
                                                        0.3f,
                                                        1.0f
                                                    ).apply {
                                                        duration = 1500
                                                        repeatMode = ValueAnimator.REVERSE
                                                        repeatCount = 1
                                                        doOnRepeat {
                                                            pause()
                                                            Timer().schedule(object : TimerTask() {
                                                                override fun run() {
                                                                    runOnUiThread { resume() }
                                                                }
                                                            }, 1000)
                                                        }
                                                    }
                                                    val fadeStar6 = ObjectAnimator.ofFloat(
                                                        binding.star6,
                                                        "alpha",
                                                        0.3f,
                                                        1.0f
                                                    ).apply {
                                                        duration = 1500
                                                        repeatMode = ValueAnimator.REVERSE
                                                        repeatCount = 1
                                                        doOnRepeat {
                                                            pause()
                                                            Timer().schedule(object : TimerTask() {
                                                                override fun run() {
                                                                    runOnUiThread {
                                                                        resume()
                                                                        val fadeInLitStarTile =
                                                                            ObjectAnimator.ofFloat(
                                                                                litStarTitle,
                                                                                "alpha",
                                                                                0f,
                                                                                1f
                                                                            ).apply {
                                                                                duration = 1000
                                                                            }
                                                                        val fadeInLitStarDescription =
                                                                            ObjectAnimator.ofFloat(
                                                                                litStarDescription,
                                                                                "alpha",
                                                                                0f,
                                                                                1f
                                                                            ).apply {
                                                                                duration = 1000
                                                                            }
                                                                        val fadeInButton =
                                                                            ObjectAnimator.ofFloat(
                                                                                buttonSky,
                                                                                "alpha",
                                                                                0f,
                                                                                0.23f
                                                                            ).apply {
                                                                                duration = 1000
                                                                                doOnEnd {
                                                                                    buttonSky.isClickable =
                                                                                        true
                                                                                    buttonSky.setOnTouchListener { view, motionEvent ->
                                                                                        when (motionEvent.action) {
                                                                                            MotionEvent.ACTION_DOWN -> {
                                                                                                val button = view as ImageView
                                                                                                button.drawable.setColorFilter(0x77000000, PorterDuff.Mode.SRC_ATOP)
                                                                                                button.invalidate()
                                                                                            }
                                                                                            MotionEvent.ACTION_UP -> {
                                                                                                val button = view as ImageView
                                                                                                button.drawable.clearColorFilter()
                                                                                                button.invalidate()
                                                                                                val intent =
                                                                                                    Intent(
                                                                                                        baseContext,
                                                                                                        LoginActivity::class.java
                                                                                                    )
                                                                                                intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION)
                                                                                                startingLoginActivity = true
                                                                                                connection?.let { it1 ->
                                                                                                    unbindService(it1)
                                                                                                }
                                                                                                startActivity(intent)
                                                                                                overridePendingTransition(0,0)
                                                                                                finish()
                                                                                            }
                                                                                            MotionEvent.ACTION_CANCEL -> {
                                                                                                val button = view as ImageView
                                                                                                button.drawable.clearColorFilter()
                                                                                                button.invalidate()
                                                                                            }
                                                                                        }
                                                                                        false
                                                                                    }
                                                                                }
                                                                            }
                                                                        val fadeInButtonText =
                                                                            ObjectAnimator.ofFloat(
                                                                                buttonSkyText,
                                                                                "alpha",
                                                                                0f,
                                                                                1f
                                                                            ).apply {
                                                                                duration = 1000
                                                                            }

                                                                        AnimatorSet().apply {
                                                                            playTogether(
                                                                                fadeInLitStarTile,
                                                                                fadeInLitStarDescription,
                                                                                fadeInButton,
                                                                                fadeInButtonText
                                                                            )
                                                                            play(fadeInLitStarTile).after(
                                                                                scaleDownBright
                                                                            )
                                                                            start()
                                                                        }
                                                                    }
                                                                }
                                                            }, 1000)
                                                        }
                                                    }
                                                    AnimatorSet().apply {
                                                        playTogether(
                                                            scaleMid2,
                                                            scaleInner,
                                                            scaleMid,
                                                            scaleOutter,
                                                            scaleOutter2,
                                                            scaleBright,
                                                            fadeStar0,
                                                            fadeStar1,
                                                            fadeStar2,
                                                            fadeStar3,
                                                            fadeStar4,
                                                            fadeStar5,
                                                            fadeStar6,
                                                            fadeOutTitle,
                                                            fadeOutDescription,
                                                            fadeOutStarAction,
                                                            fadeOutStarCircle
                                                        )
                                                        playTogether(
                                                            scaleDownOutter,
                                                            scaleDownOutter2
                                                        )
                                                        play(scaleDownOutter).after(scaleBright)

                                                        play(scaleDownMid).after(scaleDownOutter)
                                                        play(scaleDownMid2).after(scaleDownOutter)
                                                        play(scaleDownBright).after(scaleDownOutter)
                                                        play(scaleDownInner).after(scaleDownOutter)

                                                        start()
                                                        binding.starLineView.undo()
                                                        binding.starInner.startAnimation(shake)
                                                        binding.starMid.startAnimation(shake)
                                                        binding.starOutter1.startAnimation(shake)
                                                        binding.starOutter2.startAnimation(shake)
                                                        binding.starMid2.startAnimation(shake)

                                                        /*runOnUiThread {
                                                            Handler().postDelayed({
                                                                binding.beamLightView.visibility = View.GONE
                                                                binding.starLineView.visibility = View.GONE
                                                            }, 500)
                                                        }*/
                                                    }
                                                }
                                                } else {
                                                    binding.beamLightView.getLineAnimUndo()?.pause()
                                                    //binding.beamLightView.getfadeAnimUndo()?.pause()
                                                    binding.beamLightView.redo()
                                                }

                                                true
                                            }
                                            else -> super.onTouchEvent(motionEvent)
                                        }
                                    }
                                }
                            }

                        val metrics =  DisplayMetrics()
                        windowManager.defaultDisplay.getMetrics(metrics);
                        val location = IntArray(2)
                        starOutterInvisible.getLocationOnScreen(location)
                        //actionText.y = metrics.heightPixels/2.8f - actionText.height
                        actionText.y = starOutterInvisible.y - dpToPx(50) - actionText.height
                        Log.d("actiontextheight", actionText.height.toString())
                        ballIndicator.y = actionText.y - dpToPxF(15f)

                        AnimatorSet().apply {
                            playTogether(
                                rotateOutter,
                                fadeOutWelcomeText1,
                                fadeOutWelcomeText2,
                                fadeOutStartText,
                                fadeOutStartArrow
                            )

                            playTogether(fadeInTitle1, fadeInDesc1, fadeInActionText, fadeInBallIndicator)
                            play(fadeInTitle1).after(rotateOutter)
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

    private fun addSkyParticles() {
        val skyParticles = List(50) { ImageView(this) }
        val size = List(50) { Random.nextDouble(1.0, 5.0) }
        val x = List(50) { Random.nextInt(0, resources.displayMetrics.widthPixels )}
        val y = List(50) { Random.nextInt(0, resources.displayMetrics.heightPixels)}
        for (i in skyParticles.indices) {
            skyParticles[i].setImageResource(R.drawable.star_circle)
            skyParticles[i].layoutParams = LinearLayout.LayoutParams(dpToPxD(size[i]), dpToPxD(size[i]))
            skyParticles[i].x = x[i].toFloat()
            skyParticles[i].y = y[i].toFloat()
            binding.onboarding.addView(skyParticles[i])
        }
    }

    private fun addParticlesExplosion() {
        particles = List(100) { ImageView(this) }
        size = List(100) { Random.nextDouble(1.0, 5.0) }
        displayMetrics = resources.displayMetrics
        xRight = List(25) { Random.nextInt((displayMetrics.widthPixels/2 + (displayMetrics.widthPixels * 0.2)).toInt(), (displayMetrics.widthPixels/2 + (displayMetrics.widthPixels * 0.4)).toInt()) }
        yRight = List(25) { ((displayMetrics.heightPixels/2 - (displayMetrics.heightPixels * 0.1)).toInt() until (displayMetrics.heightPixels/2 + (displayMetrics.heightPixels * 0.25)).toInt() ).random() }
        xLeft = List(25) { Random.nextInt((displayMetrics.widthPixels/2 - (displayMetrics.widthPixels* 0.4)).toInt(), (displayMetrics.widthPixels/2- (displayMetrics.widthPixels * 0.15)).toInt()) }
        yLeft = List(25) { Random.nextInt((displayMetrics.heightPixels/2 - (displayMetrics.heightPixels*0.2)).toInt(), (displayMetrics.heightPixels/2 + (displayMetrics.heightPixels * 0.3)).toInt()) }
        xTop = List(25) { Random.nextInt((displayMetrics.widthPixels/2 - (displayMetrics.widthPixels * 0.4)).toInt(), (displayMetrics.widthPixels/2 + (displayMetrics.widthPixels * 0.4)).toInt()) }
        yTop = List(25) { Random.nextInt((displayMetrics.heightPixels/2 - (displayMetrics.heightPixels * 0.4)).toInt(), (displayMetrics.heightPixels/2 - (displayMetrics.heightPixels * 0.05)).toInt()) }
        xBottom = List(25) { Random.nextInt((displayMetrics.widthPixels/2 - (displayMetrics.widthPixels * 0.4)).toInt(), (displayMetrics.widthPixels/2 + (displayMetrics.widthPixels * 0.4)).toInt()) }
        yBottom = List(25) { Random.nextInt((displayMetrics.heightPixels/2 + (displayMetrics.heightPixels * 0.07)).toInt(), (displayMetrics.heightPixels/2 + (displayMetrics.heightPixels * 0.24)).toInt()) }
        paths = Array(100) { Path() }

        for (i in particles.indices) {
            particles[i].setImageResource(R.drawable.star_circle)
            particles[i].layoutParams = LinearLayout.LayoutParams(dpToPxD(size[i]), dpToPxD(size[i]))
            particles[i].x = displayMetrics.widthPixels / 2f
            particles[i].y = displayMetrics.heightPixels / 2f
            onboarding.addView(particles[i])
        }

    }

    private fun starParticlesExplosion() {
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

    private fun setParticles() {
        var displayMetrics = resources.displayMetrics
        val particle = List(100) { ImageView(this) }
        val size = List(100) { Random.nextDouble(1.0, 5.0) }
        val x = List(100) { Random.nextInt(0, displayMetrics.widthPixels)}
        val y = List(100) { Random.nextInt(0, displayMetrics.heightPixels)}
        for (i in particle.indices) {
            particle[i].setImageResource(R.drawable.star_circle)
            particle[i].layoutParams = LinearLayout.LayoutParams(dpToPxD(size[i]), dpToPxD(size[i]))
            particle[i].x = x[i].toFloat()
            particle[i].y = y[i].toFloat()
            binding.onboarding.addView(particle[i])
        }

    }

    private fun setTexts() {
        language = if (Language().language == "pt") {
            "pt"
        } else {
            "en"
        }
        val config = resources.configuration
        val locale = Locale(language)

        if (Build.VERSION.SDK_INT >= 24) {
            config.setLocale(locale)
        } else {
            config.locale = locale
        }

        if (Build.VERSION.SDK_INT >= 25) {
            resources.updateConfiguration(config, resources.displayMetrics)
        } else {
            createConfigurationContext(config)
        }

        welcomeText1.text = resources.getString(R.string.onboarding_title1)
        welcomeText2.text = resources.getString(R.string.onboarding_description1)
        startText.text = resources.getString(R.string.onboarding_start)
        actionText.text = resources.getString(R.string.onboarding_star_action)
        title1.text = resources.getString(R.string.onboarding_title2)
        description1.text = resources.getString(R.string.onboarding_description2)
        binding.litStarTitle.text = resources.getString(R.string.onboarding_title3)
        binding.litStarDescription.text = resources.getString(R.string.onboarding_description3)
        binding.buttonSkyText.text = resources.getString(R.string.onboarding_go_to_sky)
        Log.d("buttonheight", binding.buttonSkyText.height.toString())

        //This was when the app used to get the main texts from firebase
/*        database.child(language).child("onboarding").child("onboarding1").addListenerForSingleValueEvent(
            object : ValueEventListener {
                override fun onCancelled(databaseError: DatabaseError) {
                    Log.w(ContentValues.TAG, "getUser:onCancelled", databaseError.toException())
                }

                override fun onDataChange(dataSnapshot: DataSnapshot) {

                    try {
                        welcomeText1.text = dataSnapshot.child("welcomeTitle").value as String
                        welcomeText2.text = dataSnapshot.child("welcomeDescription").value as String
                        startText.text = dataSnapshot.child("startText").value as String
                        actionText.text = dataSnapshot.child("starAction").value as String
                        title1.text = dataSnapshot.child("title").value as String
                        description1.text = dataSnapshot.child("description").value as String
                        litStarTitle.text = dataSnapshot.child("title2").value as String
                        litStarDescription.text = dataSnapshot.child("description2").value as String
                        buttonSkyText.text = dataSnapshot.child("button").value as String
                        fadeInAnimation()

                    } catch(e: Exception) {
                        Log.d("oputs", "merda")
                    }


                }
            }
        )*/
    }
}