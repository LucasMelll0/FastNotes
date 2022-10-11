package com.example.fastnotes.model

import android.os.Parcelable
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.firebase.database.FirebaseDatabase
import kotlinx.parcelize.Parcelize
import java.util.*

@Parcelize
@Entity
data class Note(
    @PrimaryKey
    var id: String = UUID.randomUUID().toString(),
    var user: String = "",
    var userId: String = "",
    var title: String = "",
    var description: String = "",
    var image: String = "",
    @ColumnInfo(defaultValue = "0")
    var public: Boolean = false,
    @ColumnInfo(defaultValue = "0")
    var synchronized: Boolean = false,
    @ColumnInfo(defaultValue = "0")
    var disabled: Boolean = false
): Parcelable{

}