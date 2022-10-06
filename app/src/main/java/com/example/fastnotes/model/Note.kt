package com.example.fastnotes.model

import android.os.Parcelable
import com.google.firebase.database.FirebaseDatabase
import kotlinx.parcelize.Parcelize
import java.util.*

@Parcelize
data class Note(
    var id: String = UUID.randomUUID().toString(),
    var user: String ="",
    var title: String = "",
    var description: String = "",
    var image: String = ""
): Parcelable{

}