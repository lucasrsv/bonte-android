package com.example.android.bonte_android.sky

import android.app.Activity
import android.graphics.Path
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import com.example.android.bonte_android.R

data class Constellation(val id: Int, val size: Int, val name: String, val description: String, val stars: List<Star>, val numPaths: Int, val paths: MutableList<Path>)  {

    fun setImageParams(params: ViewGroup.LayoutParams) {
        for (i in stars.indices) {
            if (!stars[i].status) stars[i].starImageView.setImageResource(R.drawable.star_off)
            else stars[i].starImageView.setImageResource((R.drawable.star_on))
            stars[i].starImageView.layoutParams = params
            stars[i].starImageView.x = stars[i].position.x.toFloat()
            Log.d("aa", stars[i].starImageView.x.toString())
            Log.d("bb", stars[i].position.x.toString())
            stars[i].starImageView.y = stars[i].position.y.toFloat()

        }
    }

    fun setStarsNeighbors() {
        when (id) {
            0 -> {
                stars[0].setNeighbor(stars[1])
                stars[0].setNeighbor(stars[2])
                stars[1].setNeighbor(stars[2])
                stars[2].setNeighbor(stars[3])
                stars[2].setNeighbor(stars[4])
                stars[3].setNeighbor(stars[4])

            }
            1 -> {
                stars[0].setNeighbor(stars[1])
                stars[1].setNeighbor(stars[2])
                stars[2].setNeighbor(stars[3])
                stars[2].setNeighbor(stars[4])

            }
            2 -> {
                stars[0].setNeighbor(stars[1])
                stars[1].setNeighbor(stars[2])
                stars[2].setNeighbor(stars[3])
                stars[2].setNeighbor(stars[4])
                stars[2].setNeighbor(stars[6])
                stars[4].setNeighbor(stars[5])
                stars[6].setNeighbor(stars[7])

            }
            3 -> {
                stars[0].setNeighbor(stars[1])
                stars[0].setNeighbor(stars[3])
                stars[1].setNeighbor(stars[2])
                stars[2].setNeighbor(stars[3])
            }
            4 -> {
                stars[0].setNeighbor(stars[1])
                stars[1].setNeighbor(stars[2])
                stars[1].setNeighbor(stars[3])
            }

        }
    }


}