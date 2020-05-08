package com.example.android.bonte_android.onboarding

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.annotation.SuppressLint
import android.content.ContentValues.TAG
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import android.widget.ImageView
import android.widget.TextView
import androidx.core.animation.doOnEnd
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.navigation.Navigation
import com.example.android.bonte_android.Language
import com.example.android.bonte_android.R
import com.example.android.bonte_android.databinding.FragmentOnboarding1Binding
import com.example.android.bonte_android.R.layout
import com.google.firebase.database.*

class OnboardingFragment1 : Fragment() {
    private lateinit var binding: FragmentOnboarding1Binding
    private lateinit var fadeInAnim: Animation
    private lateinit var startArrow: ImageView
    private lateinit var startText: TextView
    private lateinit var welcomeText1: TextView
    private lateinit var welcomeText2: TextView
    private lateinit var turnedOffStarButton: View
    private var database: DatabaseReference = FirebaseDatabase.getInstance().reference
    private lateinit var language: String

    @SuppressLint("ClickableViewAccessibility")
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        binding = DataBindingUtil.inflate(inflater, layout.fragment_onboarding1, container, false)
        welcomeText1 = binding.welcomeText
        welcomeText2 = binding.welcomeText2
        startArrow = binding.arrowUp
        startText = binding.startText
        turnedOffStarButton = binding.starOutter

        setTexts()
        return binding.root
    }

    private fun fadeInAnimation() {

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
                turnedOffStarButton.setOnClickListener (
                    Navigation.createNavigateOnClickListener(R.id.action_onboardingFragment1_to_onboardingFragment2)
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
        database.child(language).child("onboarding").child("onboarding1").addListenerForSingleValueEvent(
            object : ValueEventListener {
                override fun onCancelled(databaseError: DatabaseError) {
                    Log.w(TAG, "getUser:onCancelled", databaseError.toException())
                }

                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    welcomeText1.text = dataSnapshot.child("welcomeTitle").value as String
                    welcomeText2.text = dataSnapshot.child("welcomeDescription").value as String
                    startText.text = dataSnapshot.child("startText").value as String
                    fadeInAnimation()

                }
            }
        )
    }


}