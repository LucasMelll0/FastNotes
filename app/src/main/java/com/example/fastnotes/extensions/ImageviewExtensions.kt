package com.example.fastnotes.extensions

import android.view.View
import android.widget.ImageView
import coil.load

fun ImageView.tryLoadImage(image: String = "") {
    visibility = if (image.isNotEmpty()) {
        load(image)
        View.VISIBLE
    } else {
        View.GONE
    }
}