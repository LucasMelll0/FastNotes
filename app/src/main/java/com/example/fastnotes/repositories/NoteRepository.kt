package com.example.fastnotes.repositories

import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.fastnotes.model.Note
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase

class NoteRepository(
    private val activity: AppCompatActivity
){

    private val database = FirebaseDatabase.getInstance().reference
    private val currentUser = FirebaseAuth.getInstance().currentUser

    fun saveNote(note: Note){
        currentUser?.let {
            database
                .child("note")
                .child(it.uid)
                .child(note.id)
                .setValue(note)
                .addOnCompleteListener { task ->
                    if(task.isSuccessful){
                        activity.onBackPressed()
                        Toast.makeText(
                            activity,
                            "Note saved with successful!!",
                            Toast.LENGTH_SHORT
                        ).show()
                    }else{
                        Toast.makeText(
                            activity,
                            "Error on save note!!",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }.addOnFailureListener {
                    Toast.makeText(
                        activity,
                        "Error on save note!!",
                        Toast.LENGTH_SHORT
                    ).show()
                }
        }
    }
}