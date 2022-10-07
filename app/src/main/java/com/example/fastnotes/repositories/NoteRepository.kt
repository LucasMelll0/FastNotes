package com.example.fastnotes.repositories

import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.fastnotes.R
import com.example.fastnotes.database.NOTE_PATH
import com.example.fastnotes.model.Note
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase


class NoteRepository(
    private val fragment: Fragment,
) {

    val database = FirebaseDatabase.getInstance().reference
    private val currentUser = FirebaseAuth.getInstance().currentUser


    fun saveNote(note: Note) {
        currentUser?.let {
            database
                .child(NOTE_PATH)
                .child(it.uid)
                .child(note.id)
                .setValue(note)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        fragment.findNavController().popBackStack()
                        Snackbar.make(
                            fragment.requireView(),
                            fragment.getString(R.string.succesfull_not_save),
                            Snackbar.LENGTH_SHORT
                        ).show()
                    } else {
                        Snackbar.make(
                            fragment.requireView(),
                            fragment.getString(R.string.error_save_note),
                            Snackbar.LENGTH_SHORT
                        ).show()
                    }
                }.addOnFailureListener {
                    Snackbar.make(
                        fragment.requireView(),
                        fragment.getString(R.string.error_save_note),
                        Snackbar.LENGTH_SHORT
                    ).show()
                }
        }
    }

    fun removeNote(id: String) {
        currentUser?.let { user ->
            database
                .child(NOTE_PATH)
                .child(user.uid)
                .child(id)
                .removeValue()
                .addOnCompleteListener { task ->
                    if (task.isSuccessful){
                        fragment.findNavController().popBackStack()
                        Snackbar.make(
                            fragment.requireView(),
                            fragment.getString(R.string.note_delet_success),
                            Snackbar.LENGTH_SHORT
                        ).show()
                    }else{
                        Snackbar.make(
                            fragment.requireView(),
                            fragment.getString(R.string.error_on_delete_note),
                            Snackbar.LENGTH_LONG
                        ).show()
                    }

                }
                .addOnFailureListener {
                    Snackbar.make(
                        fragment.requireView(),
                        fragment.getString(R.string.error_on_delete_note),
                        Snackbar.LENGTH_LONG
                    ).show()
                }
        }
    }
}