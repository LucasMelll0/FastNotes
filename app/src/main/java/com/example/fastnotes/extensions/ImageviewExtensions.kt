package com.example.fastnotes.extensions

import android.view.View
import android.widget.ImageView
import coil.load

fun ImageView.tryLoadImage(image: String = "") {
    if (image.trim() != "") {
        load(image)
    } else {
        visibility = View.GONE 
    }
}