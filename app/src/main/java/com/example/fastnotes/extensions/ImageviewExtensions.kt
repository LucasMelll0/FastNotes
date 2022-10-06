package com.example.fastnotes.extensions

import android.view.View
import android.widget.ImageView
import coil.load

fun ImageView.tryLoadImage(image: String = "") {
    if (image != "") {
        load(image)
    } else {
        run { visibility = View.GONE }
    }
}