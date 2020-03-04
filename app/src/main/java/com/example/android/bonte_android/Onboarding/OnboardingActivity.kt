package com.example.android.bonte_android.Onboarding

import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.databinding.DataBindingUtil
import com.example.android.bonte_android.R
import com.example.android.bonte_android.databinding.ActivityOnboardingBinding

class OnboardingActivity : AppCompatActivity() {


    private lateinit var sharedPreferences: SharedPreferences
    private var firstTime: Boolean = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        /*sharedPreferences = getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)
        firstTime = sharedPreferences.getBoolean("FirstTime", true)
        if (!firstTime) {} else {
            setContentView(R.layout.activity_onboarding)
            var editor: SharedPreferences.Editor = sharedPreferences.edit()
            editor.putBoolean("FirstTIme", false)
            editor.commit()
        }*/

        val binding = DataBindingUtil.setContentView<ActivityOnboardingBinding>(this,
            R.layout.activity_onboarding
        )
        // zoomAnimation()

    }
/*
    private fun zoomAnimation() {
        zoomAnimation = AnimationUtils.loadAnimation(this,R.anim.zoom_undonestar)
        turnedOffStar.startAnimation(zoomAnimation)
    }*/
}