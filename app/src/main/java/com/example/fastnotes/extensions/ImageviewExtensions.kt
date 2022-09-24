package com.example.fastnotes.extensions

import android.view.View
import android.widget.ImageView
import coil.load

fun ImageView.tryLoadImage(image: String? = null) {
    image?.let {
        load(image)
    } ?: run { visibility = View.GONE }
}