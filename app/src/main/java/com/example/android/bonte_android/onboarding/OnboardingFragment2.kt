package com.example.android.bonte_android.onboarding

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import com.example.android.bonte_android.R
import com.example.android.bonte_android.customViews.StarLineView
import com.example.android.bonte_android.databinding.FragmentOnboarding2Binding


class OnboardingFragment2 : Fragment(), StarLineView.StarView {
    private lateinit var binding: FragmentOnboarding2Binding
    private lateinit var title1: TextView
    private lateinit var title2: TextView
    private lateinit var firstAction: TextView
    private lateinit var ballIndicator: ImageView
    private lateinit var star: ImageView
    private lateinit var starLineView: StarLineView

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_onboarding2, container, false)
        title1 = binding.text1
        title2 = binding.text2
        firstAction = binding.firstAction
        ballIndicator = binding.ballIndicator
        star = binding.starOffButton


        fadeInAnimation()

        return binding.root
    }

    private fun fadeInAnimation() {

        val fadeIn1 = ObjectAnimator.ofFloat(title1, "alpha", 0.35f, 1.0f).apply {
            duration = 1500

        }
        val fadeIn2 = ObjectAnimator.ofFloat(title2, "alpha", 0.35f, 1.0f).apply {
            duration = 1500
            addListener(object : AnimatorListenerAdapter() {
                override fun onAnimationEnd(animation: Animator) {
                    ballIndicator.visibility = View.VISIBLE
                    firstAction.visibility = View.VISIBLE
                }
            })
        }

        val fadeIn3 = ObjectAnimator.ofFloat(firstAction, "alpha", 0.0f, 1.0f).apply {
            duration = 1000
        }
        val fadeIn4 = ObjectAnimator.ofFloat(ballIndicator, "alpha", 0.0f, 1.0f).apply {
            duration = 1000
        }


        AnimatorSet().apply {
            playTogether(fadeIn1, fadeIn2)
            playTogether(fadeIn3, fadeIn4)
            play(fadeIn3).after(fadeIn2)
            start()
        }

    }

    override fun onStar() {
        var star = binding.starOffButton
        starLineView.setCallBack(this)
    }

}
