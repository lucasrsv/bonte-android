package com.example.android.bonte_android.onboarding

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.content.ContentValues
import android.os.Bundle
import android.util.Log
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import android.view.animation.LinearInterpolator
import android.view.animation.RotateAnimation
import android.widget.ImageView
import android.widget.TextView
import androidx.core.animation.doOnEnd
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.navigation.Navigation
import com.example.android.bonte_android.Language
import com.example.android.bonte_android.R
import com.example.android.bonte_android.databinding.FragmentOnboarding2Binding
import com.google.firebase.database.*
import kotlin.math.roundToInt


class OnboardingFragment2 : Fragment() {
    private lateinit var binding: FragmentOnboarding2Binding
    private lateinit var title1: TextView
    private lateinit var title2: TextView
    private lateinit var ballIndicator: ImageView
    private lateinit var actionText: TextView
    private lateinit var starButton: View
    private lateinit var starOutter2: View
    private var database: DatabaseReference = FirebaseDatabase.getInstance().reference
    private lateinit var language: String

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_onboarding2, container, false)
        title1 = binding.text1
        title2 = binding.text2
        actionText = binding.firstAction
        ballIndicator = binding.ballIndicator
        starButton = binding.starOutter
        starOutter2 = binding.starOutter2

        rotateAnimation()
        setTexts()

        return binding.root
    }

    private fun rotateAnimation() {
        val rotateAnim = RotateAnimation(0f, -26f, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f)
        rotateAnim.duration = 1000
        rotateAnim.interpolator = LinearInterpolator()
        rotateAnim.fillAfter = true
        starOutter2.startAnimation(rotateAnim)

    }

    private fun fadeInAnimation() {

        val fadeIn1 = ObjectAnimator.ofFloat(title1, "alpha", 0.35f, 1.0f).apply {
            duration = 1500

        }

        val fadeIn2 = ObjectAnimator.ofFloat(title2, "alpha", 0.35f, 1.0f).apply {
            duration = 1500
            doOnEnd {
                ballIndicator.visibility = View.VISIBLE
                actionText.visibility = View.VISIBLE
            }
        }

        val fadeIn3 = ObjectAnimator.ofFloat(actionText, "alpha", 0.0f, 1.0f).apply {
            duration = 1000
        }

        val fadeIn4 = ObjectAnimator.ofFloat(ballIndicator, "alpha", 0.0f, 1.0f).apply {
            duration = 1000
            doOnEnd {
                starButton.setOnClickListener(
                    Navigation.createNavigateOnClickListener(R.id.action_onboardingFragment2_to_skyActivity)
                )
            }
        }

        AnimatorSet().apply {
            playTogether(fadeIn1, fadeIn2)
            playTogether(fadeIn3, fadeIn4)
            play(fadeIn3).after(fadeIn2)
            start()
        }

    }

    private fun setTexts() {
        language = if (Language().language == "pt") {
            "pt"
        } else {
            "en"
        }
        database.child(language).child("onboarding").child("onboarding2").addListenerForSingleValueEvent(
            object : ValueEventListener {
                override fun onCancelled(databaseError: DatabaseError) {
                    Log.w(ContentValues.TAG, "getUser:onCancelled", databaseError.toException())
                }

                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    actionText.text = dataSnapshot.child("starAction").value as String
                    title1.text = dataSnapshot.child("title").value as String
                    title2.text = dataSnapshot.child("description").value as String
                    fadeInAnimation()

                }
            }
        )
    }



    fun dpToPx(dp: Int): Int {
        return TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_DIP,
            dp.toFloat(),
            resources.displayMetrics
        ).roundToInt()
    }

    fun spToPx(sp: Int): Int {
        return TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_SP,
            sp.toFloat(),
            resources.displayMetrics
        ).roundToInt()
    }
}
