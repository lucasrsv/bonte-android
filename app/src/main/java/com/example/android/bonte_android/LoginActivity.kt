package com.example.android.bonte_android

import android.content.Intent
import android.os.Bundle
import android.os.PersistableBundle
import android.util.Log
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.example.android.bonte_android.User.User
import com.example.android.bonte_android.User.UserConstellations
import com.example.android.bonte_android.User.UserStars
import com.example.android.bonte_android.databinding.ActivityLoginBinding
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
import kotlin.random.Random

class LoginActivity : AppCompatActivity() {
    private lateinit var binding: ActivityLoginBinding
    private lateinit var signInButton: SignInButton
    private lateinit var database: DatabaseReference
    private lateinit var googleSignInClient: GoogleSignInClient
    private lateinit var auth: FirebaseAuth
    private val RC_SIGN_IN = 9001

    override fun onCreate(savedInstanceState: Bundle?) {
        window.enterTransition = null
        setTheme(R.style.AppTheme)
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(
            this,
            R.layout.activity_login
        )
        signInButton = binding.signInButton
        signInButton.setSize(SignInButton.SIZE_WIDE)
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build()

        googleSignInClient = GoogleSignIn.getClient(this, gso)
        auth = FirebaseAuth.getInstance()
        database = FirebaseDatabase.getInstance().reference

        signInButton.setOnClickListener {
            signIn()
        }

        changeStatusBarColor()
        addSkyParticles()
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
        val user = User(firebaseUser!!.uid, firebaseUser.email, userConstellations)
        database.child("users").child(firebaseUser!!.uid).setValue(user)
    }

    private fun addSkyParticles() {
        val particles = List(100) { ImageView(this) }
        val size = List(100) { Random.nextDouble(1.0, 5.0) }
        val x = List(100) { Random.nextInt(0, resources.displayMetrics.widthPixels )}
        val y = List(100) { Random.nextInt(0, resources.displayMetrics.heightPixels)}
        for (i in particles.indices) {
            particles[i].setImageResource(R.drawable.star_circle)
            particles[i].layoutParams = LinearLayout.LayoutParams(dpToPxD(size[i]), dpToPxD(size[i]))
            particles[i].x = x[i].toFloat()
            particles[i].y = y[i].toFloat()
            binding.loginLayout.addView(particles[i])
        }
    }
}