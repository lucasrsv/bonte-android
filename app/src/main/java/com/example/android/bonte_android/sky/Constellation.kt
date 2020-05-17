package com.example.android.bonte_android.sky

data class Constellation(
    val id: Int,
    val size: Int,
    val name: String,
    val description: String,
    val stars: List<Star>,
    val numPaths: Int)  {

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