package com.example.android.bonte_android.onboarding

import android.content.SharedPreferences
import android.graphics.PointF
import android.graphics.Rect
import android.graphics.RectF
import android.os.Bundle
import android.util.Log
import android.view.MotionEvent
import android.view.ScaleGestureDetector
import android.view.VelocityTracker
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GestureDetectorCompat
import androidx.core.view.ViewCompat
import androidx.databinding.DataBindingUtil
import com.example.android.bonte_android.R
import com.example.android.bonte_android.changeStatusBarColor
import com.example.android.bonte_android.databinding.ActivityOnboardingBinding


class OnboardingActivity : AppCompatActivity() {


    private lateinit var sharedPreferences: SharedPreferences
    private var firstTime: Boolean = true
    private lateinit var mScaleGestureDetector:ScaleGestureDetector
    private lateinit var mGestureDetector: GestureDetectorCompat


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

        changeStatusBarColor()

    }
}