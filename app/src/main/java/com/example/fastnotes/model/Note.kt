package com.example.fastnotes.model

import java.util.*

class Note(
    val id: String = UUID.randomUUID().toString(),
    val user: User,
    val title: String,
    val note: String?,
    val image: String?,
    synchronized: Boolean = false,
    disabled: Boolean = false
)