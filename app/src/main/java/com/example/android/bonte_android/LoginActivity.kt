package com.example.android.bonte_android

import android.animation.Animator
import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.annotation.SuppressLint
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.graphics.Path
import android.graphics.PorterDuff
import android.os.Build
import android.os.Bundle
import android.os.IBinder
import android.os.PersistableBundle
import android.util.Log
import android.view.MotionEvent
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.core.animation.doOnEnd
import androidx.core.animation.doOnStart
import androidx.databinding.DataBindingUtil
import com.example.android.bonte_android.User.*
import com.example.android.bonte_android.databinding.ActivityLoginBinding
import com.example.android.bonte_android.onboarding.OnboardingActivity
import com.example.android.bonte_android.sky.BackgroundSongService
import com.example.android.bonte_android.sky.SkyActivity
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.SignInButton
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import kotlinx.android.synthetic.main.activity_login.*
import kotlinx.android.synthetic.main.activity_onboarding.view.*
import kotlin.random.Random

class LoginActivity : AppCompatActivity() {
    private lateinit var binding: ActivityLoginBinding
    private lateinit var signInButton: ImageView
    private lateinit var database: DatabaseReference
    private lateinit var googleSignInClient: GoogleSignInClient
    private lateinit var auth: FirebaseAuth
    private lateinit var connection: ServiceConnection
    private var isBound = false
    private var backgroundSongService: BackgroundSongService? = null
    private var movedStar = false
    private var startingNewActivity = false
    private val RC_SIGN_IN = 9001

    @SuppressLint("ClickableViewAccessibility")
    override fun onCreate(savedInstanceState: Bundle?) {
        window.enterTransition = null
        setTheme(R.style.AppTheme)
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(
            this,
            R.layout.activity_login
        )
        signInButton = binding.signInButton
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build()

        googleSignInClient = GoogleSignIn.getClient(this, gso)
        auth = FirebaseAuth.getInstance()
        database = FirebaseDatabase.getInstance().reference
        signInButton.isClickable = true
        signInButton.setOnTouchListener { view, motionEvent ->
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
                    signIn()
                }
                MotionEvent.ACTION_CANCEL -> {
                    val button = view as ImageView
                    button.drawable.clearColorFilter()
                    button.invalidate()
                }
            }
            false
        }
        binding.returnToOnboardingButton.isClickable = true
        binding.returnToOnboardingButton.setOnTouchListener { view, motionEvent ->
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
                    val intent = Intent(baseContext, OnboardingActivity::class.java)
                    intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION)
                    intent.putExtra("EXTRA_SONG_SERVICE_ON", "1")
                    startingNewActivity = true
                    unbindService(connection)
                    isBound = false
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


        changeStatusBarColor()
        backgroundSong()
        addSkyParticles()

    }

    override fun onResume() {
        super.onResume()
        if (!isBound) {
            Intent(this, BackgroundSongService::class.java).also { intent ->
                bindService(intent, connection, Context.BIND_AUTO_CREATE)
            }
            isBound = true
            backgroundSongService?.resumeMusic()
        }
    }

    override fun onStop() {
        super.onStop()
        if (!startingNewActivity) {
            backgroundSongService?.run {
                if (isPlaying()) {
                    pauseMusic()
                }
            }
            if (isBound) {
                connection.let {
                    unbindService(it)
                    isBound = false
                }
            }
        }
    }

    override fun onWindowFocusChanged(hasFocus: Boolean) {
        super.onWindowFocusChanged(hasFocus)
        if (!movedStar) {
            moveStar()
            movedStar = true
        }
    }

    private fun backgroundSong() {
        connection = object : ServiceConnection {
            override fun onServiceConnected(className: ComponentName?, service: IBinder?) {
                val binder = service as BackgroundSongService.LocalBinder
                backgroundSongService = binder.getService()
            }

            override fun onServiceDisconnected(p0: ComponentName?) {
                Log.e("service status:", "onServiceDisconnected")
            }
        }
        Intent(this, BackgroundSongService::class.java).also { intent ->
            bindService(intent, connection as ServiceConnection, Context.BIND_AUTO_CREATE)
        }
    }

    private fun moveStar() {

        var distance = binding.signInButton.top.toFloat() - binding.loginText.bottom
        if (binding.starOutter.y > binding.bonteName.y) {
            distance += (binding.starOutter.bottom - binding.bonteName.top)
        } else {
            distance -= (binding.bonteName.top - binding.starOutter.bottom)
        }

        val pathInner = Path().apply {
            moveTo(binding.starInner.x, binding.starInner.y)
            lineTo(binding.starInner.x, binding.starInner.y - distance)
        }
        val pathMid = Path().apply {
            moveTo(binding.starMid.x, binding.starMid.y)
            lineTo(binding.starMid.x, binding.starMid.y - distance)
        }
        val pathOutter = Path().apply {
            moveTo(binding.starOutter.x, binding.starOutter.y)
            lineTo(binding.starOutter.x, binding.starOutter.y - distance)
        }
        val pathBright = Path().apply {
            moveTo(binding.starBright.x, binding.starBright.y)
            lineTo(binding.starBright.x, binding.starBright.y - distance)
        }

        val moveStarInner = ObjectAnimator.ofFloat(binding.starInner, View.X, View.Y, pathInner).apply {
            duration = 1500
        }
        val moveStarMid = ObjectAnimator.ofFloat(binding.starMid, View.X, View.Y, pathMid).apply {
            duration = 1500
        }
        val moveStarOutter = ObjectAnimator.ofFloat(binding.starOutter, View.X, View.Y, pathOutter).apply {
            duration = 1500
        }
        val moveStarBright = ObjectAnimator.ofFloat(binding.starBright, View.X, View.Y, pathBright).apply {
            duration = 1500
            doOnEnd {
                val fadeInTitle = ObjectAnimator.ofFloat(binding.bonteName, "alpha", 0f, 1f).apply {
                    duration = 1000
                    doOnStart {
                        binding.bonteName.visibility = View.VISIBLE
                    }
                }
                val fadeInBar = ObjectAnimator.ofFloat(binding.bar, "alpha", 0f, 0.3f).apply {
                    duration = 1000
                    doOnStart {
                        binding.bar.visibility = View.VISIBLE
                    }
                }
                val fadeInText = ObjectAnimator.ofFloat(binding.loginText, "alpha", 0f, 1f).apply {
                    duration = 1000
                    doOnStart {
                        binding.loginText.visibility = View.VISIBLE
                    }
                }
                val fadeInGoogleSymbol = ObjectAnimator.ofFloat(binding.googleSymbol, "alpha", 0f, 1f).apply {
                    duration = 1000
                    doOnStart {
                        binding.googleSymbol.visibility = View.VISIBLE
                    }
                }
                val fadeInLoginButton = ObjectAnimator.ofFloat(binding.signInButton, "alpha", 0f, 0.3f).apply {
                    duration = 1000
                    doOnStart {
                        binding.signInButton.visibility = View.VISIBLE
                    }
                }
                val fadeInLoginButtonText = ObjectAnimator.ofFloat(binding.loginButtonText, "alpha", 0f, 1f).apply {
                    duration = 1000
                    doOnStart {
                        binding.loginButtonText.visibility = View.VISIBLE
                    }
                }
                val fadeInReturnButton = ObjectAnimator.ofFloat(binding.returnToOnboardingButton, "alpha", 0f, 0.3f).apply {
                    duration = 1000
                    doOnStart {
                        binding.returnToOnboardingButton.visibility = View.VISIBLE
                    }
                }
                val fadeInReturnBUttonText = ObjectAnimator.ofFloat(binding.returnButtonText, "alpha", 0f, 1f).apply {
                    duration = 1000
                    doOnStart {
                        binding.returnButtonText.visibility = View.VISIBLE
                    }
                }

                AnimatorSet().apply {
                    playTogether(fadeInBar, fadeInLoginButton, fadeInLoginButtonText, fadeInReturnBUttonText, fadeInReturnButton, fadeInText, fadeInTitle, fadeInGoogleSymbol)
                    start()
                }
            }
        }
        AnimatorSet().apply {
            playTogether(moveStarInner, moveStarMid, moveStarOutter, moveStarBright)
            start()
        }
    }


    private fun signIn() {
        val signInIntent: Intent = googleSignInClient.signInIntent
        startActivityForResult(signInIntent, RC_SIGN_IN)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == RC_SIGN_IN) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            try {
                val account = task.getResult(ApiException::class.java)
                firebaseAuthWithGoogle(account!!)
            } catch (e: ApiException) {
                Log.w("Google Login", "signInResult: failed code = " + e.statusCode)
            }
        }
    }

    private fun firebaseAuthWithGoogle(acct: GoogleSignInAccount) {
        Log.d("Google Login", "firebaseAuthWithGoogle:" + acct.id!!)
        val credential = GoogleAuthProvider.getCredential(acct.idToken, null)
        auth.signInWithCredential(credential)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    Log.d("Google Login", "signInWithCredential:success")
                    val isNewUser = task.result!!.additionalUserInfo!!.isNewUser
                    if (isNewUser) {
                        writeNewUser()
                    }
                    val intent = Intent(baseContext, SkyActivity::class.java)
                    intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION)
                    intent.putExtra("EXTRA_SONG_SERVICE", "ENABLED")
                    intent.putExtra("EXTRA_STARTING_SKY_ACTIVITY_FROM_LOGIN", "1")
                    startingNewActivity = true
                    unbindService(connection)
                    startActivity(intent)
                    window.exitTransition = null
                    overridePendingTransition(0, 0);
                    finish()
                } else {
                    Log.w("Google Login", "signInWithCredential:failure", task.exception)
                }
            }
    }

    private fun writeNewUser() {
        val firebaseUser = FirebaseAuth.getInstance().currentUser
        val stars1 = List(5) { i ->
            UserStars(
                sId = i
            )
        }
        val constellation1 = UserConstellations(
            cId = 0,
            cName = "volans",
            cSize = 5,
            cStars = stars1
        )

        //for const 2:

        val stars2 = List(5) { i ->
            UserStars(
                sId = i
            )
        }
        val constellation2 = UserConstellations(
            cId = 1,
            cName = "cancer",
            cSize = 5,
            cStars = stars2
        )

        //for const 3:
        val stars3 = List(8) { i ->
            UserStars(
                sId = i
            )
        }
        val constellation3 = UserConstellations(
            cId = 2,
            cName = "aquila",
            cSize = 8,
            cStars = stars3
        )

        //for const 3:
        val stars4 = List(4) { i ->
            UserStars(
                sId = i
            )
        }
        val constellation4 = UserConstellations(
            cId = 3,
            cName = "equuleus",
            cSize = 4,
            cStars = stars4
        )

        //for const 3:
        val stars5 = List(4) { i ->
            UserStars(
                sId = i
            )
        }
        val constellation5 = UserConstellations(
            cId = 4,
            cName = "sagitta",
            cSize = 4,
            cStars = stars5
        )

        val userConstellations = listOf(constellation1, constellation2, constellation3, constellation4, constellation5)
        val userSettings = UserSettings(true)
        val userSkyStatus = UserSkyStatus(0)
        val user = User(firebaseUser!!.uid, firebaseUser.email, userConstellations, userSettings, userSkyStatus)
        database.child("users").child(firebaseUser.uid).setValue(user)
    }

    private fun addSkyParticles() {
        val particles = List(50) { ImageView(this) }
        val size = List(50) { Random.nextDouble(1.0, 5.0) }
        val x = List(50) { Random.nextInt(0, resources.displayMetrics.widthPixels )}
        val y = List(50) { Random.nextInt(0, resources.displayMetrics.heightPixels)}
        for (i in particles.indices) {
            particles[i].setImageResource(R.drawable.star_circle)
            particles[i].layoutParams = LinearLayout.LayoutParams(dpToPxD(size[i]), dpToPxD(size[i]))
            particles[i].x = x[i].toFloat()
            particles[i].y = y[i].toFloat()
            binding.loginLayout.addView(particles[i])
        }
    }
}