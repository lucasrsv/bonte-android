package com.example.android.bonte_android.sky

import android.graphics.Path
import android.graphics.Point
import android.view.View
import android.widget.ImageView

data class Star(val id: Int, var done: Boolean, var intermediate: Boolean, val action: String, var timesCompleted: Long, var position: Point, var starViews: MutableList<ImageView>, var paths: MutableList<Path>) {

    private var neighbor = mutableListOf<Star>()


    fun setNeighbor(star: Star) {
        neighbor.add(star)
    }

    fun getNeighbor(): MutableList<Star> {
        return neighbor
    }

}
