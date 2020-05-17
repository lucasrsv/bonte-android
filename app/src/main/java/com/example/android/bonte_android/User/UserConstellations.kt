package com.example.android.bonte_android.User

import com.example.android.bonte_android.sky.Star

data class UserConstellations(
    val cId: Int = 0,
    val cSize: Int = 0,
    val cName: String = "",
    val cStars: List<UserStars>)