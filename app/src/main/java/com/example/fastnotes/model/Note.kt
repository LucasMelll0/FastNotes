package com.example.fastnotes.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.*

data class Note(
    val id: String = UUID.randomUUID().toString(),
    val user: String,
    val title: String,
    val description: String?,
    val image: String? = ""
)