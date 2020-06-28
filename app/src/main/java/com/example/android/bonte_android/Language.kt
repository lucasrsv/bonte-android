package com.example.android.bonte_android

import java.util.*

open class Language {
    var locale: String = Locale.getDefault().language
    var language = if (locale == "pt") "pt" else "en"
}