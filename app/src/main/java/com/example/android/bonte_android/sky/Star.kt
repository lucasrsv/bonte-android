package com.example.android.bonte_android.sky

import android.graphics.Point
import android.widget.ImageView
import kotlin.properties.Delegates

data class Star(val id: Int, val status: Boolean, val action: String, var timesCompleted: Int, var position: Point, var starImageView: ImageView) {

    private var neighbor = mutableListOf<Star>()


    fun setNeighbor(star: Star) {
        neighbor.add(star)
    }

    fun getNeighbor(): MutableList<Star> {
        return neighbor
    }

}
