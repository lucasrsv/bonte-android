package com.example.android.bonte_android.Onboarding

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.animation.ValueAnimator
import android.content.Context
import android.graphics.Canvas
import android.os.Bundle
import android.renderscript.Sampler
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.LinearInterpolator
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import com.example.android.bonte_android.R
import com.example.android.bonte_android.databinding.FragmentOnboarding2Binding


class OnboardingFragment2 : Fragment() {
    private lateinit var binding: FragmentOnboarding2Binding
    private lateinit var title1: TextView
    private lateinit var title2: TextView
    private lateinit var star: ImageView

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        binding = DataBindingUtil.inflate(inflater,
            R.layout.fragment_onboarding2, container, false)
        title1 = binding.text1
        title2 = binding.text2
        star = binding.starOffButton
        fadeInAnimation()

        return binding.root
    }

    private fun fadeInAnimation() {

        val fadeIn1 = ObjectAnimator.ofFloat(title1, "alpha", 0.35f, 1.0f).apply {
            duration = 2000

        }
        val fadeIn2 = ObjectAnimator.ofFloat(title2, "alpha", 0.35f, 1.0f).apply {
            duration = 2000
        }

        AnimatorSet().apply {
            playTogether(fadeIn1, fadeIn2)
            start()
        }

    }

}