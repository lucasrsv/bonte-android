package com.example.android.bonte_android.onboarding

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import android.widget.ImageView
import android.widget.TextView
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.navigation.Navigation
import com.example.android.bonte_android.R
import com.example.android.bonte_android.databinding.FragmentOnboarding1Binding
import com.example.android.bonte_android.R.layout

class OnboardingFragment1 : Fragment() {
    private lateinit var binding: FragmentOnboarding1Binding
    private lateinit var fadeInAnim: Animation
    private lateinit var startArrow: ImageView
    private lateinit var startText: TextView
    private lateinit var welcomeText1: TextView
    private lateinit var welcomeText2: TextView
    private lateinit var turnedOffStarButton: ImageView

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
        turnedOffStarButton = binding.starOffButton

        turnedOffStarButton.setOnClickListener (
            Navigation.createNavigateOnClickListener(R.id.action_onboardingFragment1_to_onboardingFragment2)
        )

        fadeInAnimation()

        return binding.root
    }

    private fun fadeInAnimation() {
/*        fadeInAnim = AnimationUtils.loadAnimation(activity, R.anim.fade_in)
        welcomeText1.startAnimation(fadeInAnim)
        welcomeText2.startAnimation(fadeInAnim)
        welcomeText1.animation = fadeInAnim
        startArrow.startAnimation(fadeInAnim)
        startText.startAnimation(fadeInAnim)*/


        val fadeIn1 = ObjectAnimator.ofFloat(welcomeText1, "alpha", 0.35f, 1.0f).apply {
            duration = 1500

        }

        val fadeIn2 = ObjectAnimator.ofFloat(welcomeText2, "alpha", 0.35f, 1.0f).apply {
            duration = 1500
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
        }

        AnimatorSet().apply {
            playTogether(fadeIn1, fadeIn2)
            playTogether(fadeIn3, fadeIn4)
            play(fadeIn3).after(fadeIn2)
            start()
        }

    }
}