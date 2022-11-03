package com.example.fastnotes.extensions

import android.view.View
import android.widget.ImageView
import coil.load

fun ImageView.tryLoadImage(image: String = ""): Boolean {
     return if (image.isNotEmpty()) {
        load(image)
         visibility = View.VISIBLE
        true
    } else {
         visibility = View.GONE
         false
    }
}