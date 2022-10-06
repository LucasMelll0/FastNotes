package com.example.fastnotes.repositories

import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.fastnotes.model.Note
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class NoteRepository(
    private val fragment: Fragment,
) {

    val database = FirebaseDatabase.getInstance().reference
    private val currentUser = FirebaseAuth.getInstance().currentUser
    private val userNotes = mutableListOf<Note>()

    fun saveNote(note: Note) {
        currentUser?.let {
            database
                .child("note")
                .child(it.uid)
                .child(note.id)
                .setValue(note)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        fragment.findNavController().popBackStack()
                        Snackbar.make(
                            fragment.requireView(),
                            "Note saved with successful!!",
                            Snackbar.LENGTH_SHORT
                        ).show()
                    } else {
                        Snackbar.make(
                            fragment.requireView(),
                            "Error on save note!!",
                            Snackbar.LENGTH_SHORT
                        ).show()
                    }
                }.addOnFailureListener {
                    Snackbar.make(
                        fragment.requireView(),
                        "Error on save note!!",
                        Snackbar.LENGTH_SHORT
                    ).show()
                }
        }
    }





}