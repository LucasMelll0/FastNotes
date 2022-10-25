package com.example.fastnotes.extensions

import android.view.View
import android.widget.ImageView
import coil.load

fun ImageView.tryLoadImage(image: String = "") {
    visibility = if (image.trim() != "") {
        load(image)
        View.VISIBLE
    } else {
        View.GONE
    }
}