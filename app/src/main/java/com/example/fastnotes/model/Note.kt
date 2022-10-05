package com.example.fastnotes.model

import java.util.*

data class Note(
    val id: String = UUID.randomUUID().toString(),
    val user: String,
    val title: String,
    val description: String?,
    val image: String? = ""
)