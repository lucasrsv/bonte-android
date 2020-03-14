package com.example.android.bonte_android

import android.app.ActionBar
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.constraintlayout.widget.Constraints
import androidx.databinding.DataBindingUtil
import com.example.android.bonte_android.databinding.ActivitySkyBinding

class SkyActivity : AppCompatActivity() {
    private lateinit var view: View
    private lateinit var params: Constraints.LayoutParams
    private lateinit var binding: ActivitySkyBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = DataBindingUtil.setContentView<ActivitySkyBinding>(this,
            R.layout.activity_sky
        )
        view = binding.skyActivity
        view.setBackgroundResource(R.drawable.gradient)

    }
}
