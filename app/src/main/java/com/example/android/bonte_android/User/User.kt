package com.example.android.bonte_android.User

import com.example.android.bonte_android.sky.Star

data class User(
    val userId: String? = "",
    val userEmail: String? = "",
    val constellations: List<UserConstellations>
)
