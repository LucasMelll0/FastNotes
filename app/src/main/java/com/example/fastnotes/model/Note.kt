package com.example.fastnotes.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.*

@Entity
data class Note(
    @PrimaryKey
    val id: String = UUID.randomUUID().toString(),
    val user: User,
    val title: String,
    val description: String?,
    val image: String?,
    @ColumnInfo(defaultValue = "0")
    val synchronized: Boolean = false,
    @ColumnInfo(defaultValue = "0")
    val disabled: Boolean = false
)