package com.example.fastnotes.database.firebase

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.util.Log
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.example.fastnotes.R
import com.example.fastnotes.database.NOTE_PATH
import com.example.fastnotes.database.dao.NoteDao
import com.example.fastnotes.model.Note
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import kotlinx.coroutines.launch
import okhttp3.internal.wait

class FirebaseDatabaseHelper(
    private val fragment: Fragment,
    private val dao: NoteDao
) {

    companion object {
        val dbFirebase = FirebaseDatabase.getInstance().reference
        var connected: Boolean = false

    }

    private fun checkConnection(): Boolean {
        val context = fragment.requireContext()
        val connectivityManager =
            context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

        val network = connectivityManager.activeNetwork ?: return false

        val activeNetwork = connectivityManager.getNetworkCapabilities(network) ?: return false

        return when {
            activeNetwork.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> {
                true
            }
            activeNetwork.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> {
                true
            }
            else -> {
                false
            }
        }
    }

    private val currentUser = FirebaseAuth.getInstance().currentUser

    fun save(note: Note) {
        connected = checkConnection()
        Log.i("Firebase", "checkConnection: connection = $connected")
        if (connected) {
            currentUser?.let { user ->
                val userIdRef = dbFirebase.child(NOTE_PATH).child(user.uid).push()
                val noteWithKey = note.copy(key = userIdRef.key!!)
                userIdRef
                    .child(noteWithKey.id)
                    .setValue(noteWithKey)
                    .addOnSuccessListener {
                        fragment.findNavController().popBackStack()
                        Snackbar.make(
                            fragment.requireView(),
                            fragment.getString(R.string.succesfull_not_save),
                            Snackbar.LENGTH_SHORT
                        ).show()
                        updateOnLocalDataBase(noteWithKey)

                    }.addOnFailureListener {
                        Snackbar.make(
                            fragment.requireView(),
                            fragment.getString(R.string.error_save_note),
                            Snackbar.LENGTH_SHORT
                        ).show()
                    }.addOnCanceledListener {
                        Snackbar.make(
                            fragment.requireView(),
                            fragment.getString(R.string.error_save_note),
                            Snackbar.LENGTH_SHORT
                        ).show()
                    }
            }
            return
        } else {
            Snackbar.make(
                fragment.requireView(),
                fragment.getString(R.string.message_offline_sync_notes),
                5000
            ).show()
            return
        }
    }

    fun save(notes: HashMap<String, Note>) {
        connected = checkConnection()
        Log.i("Firebase Save", "checkConnection: connection = $connected")
        if (connected) {
                currentUser?.let { user ->
                    val userIdRef = dbFirebase.child(NOTE_PATH).child(user.uid).push()
                    val pushKey = userIdRef.key!!
                    val notesMap = HashMap<String, Note>()
                    for ((key, value) in notes) {
                        val noteWithKey = value.copy(key = pushKey)
                        notesMap[key] = noteWithKey
                    }
                    val saveHashMap = HashMap<String, HashMap<String, Note>>()
                    saveHashMap[userIdRef.key!!] = notesMap
                    userIdRef
                        .setValue(notesMap)
                        .addOnCompleteListener {
                            if (it.isSuccessful){
                                Snackbar.make(
                                    fragment.requireView(),
                                    fragment.getString(R.string.message_sync_notes_successfull),
                                    Snackbar.LENGTH_SHORT
                                ).show()
                                updateOnLocalDataBase(notesMap)
                                return@addOnCompleteListener
                            }else{
                                Snackbar.make(
                                    fragment.requireView(),
                                    fragment.getString(R.string.message_offline_sync_notes),
                                    Snackbar.LENGTH_LONG
                                ).show()
                                return@addOnCompleteListener
                            }
                        }

                }
            return
        } else {
            Snackbar.make(
                fragment.requireView(),
                fragment.getString(R.string.message_offline_sync_notes),
                Snackbar.LENGTH_LONG
            ).show()
            return
        }

    }

    private fun updateOnLocalDataBase(
        note: Note
    ) {
        fragment.lifecycleScope.launch {
            val noteSync = note.copy(synchronized = true)
            dao.save(noteSync)
        }
    }

    private fun updateOnLocalDataBase(notesMap: HashMap<String, Note>) {
        val syncNotes = notesMap.map { note ->
            note.value.copy(synchronized = true)
        }
        fragment.lifecycleScope.launch {
            dao.save(syncNotes)
        }
    }

    fun remove(notes: HashMap<String, HashMap<String, Note?>>) {
        connected = checkConnection()
        Log.i("Firebase", "checkConnection: connection = $connected")
        if (connected) {
            currentUser?.let { user ->
                dbFirebase
                    .child(NOTE_PATH)
                    .child(user.uid)
                    .updateChildren(notes as Map<String, Any>)
                    .addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            fragment.findNavController().popBackStack()
                            Snackbar.make(
                                fragment.requireView(),
                                fragment.getString(R.string.note_delet_success),
                                Snackbar.LENGTH_SHORT
                            ).show()
                            fragment.lifecycleScope.launch {
                                for ((_, value) in notes) {
                                    for ((id, _) in value) {
                                        dao.remove(id)
                                    }
                                }
                            }
                        } else {
                            Snackbar.make(
                                fragment.requireView(),
                                fragment.getString(R.string.error_sync_notes),
                                Snackbar.LENGTH_LONG
                            ).show()
                        }

                    }
                    .addOnFailureListener {
                        Snackbar.make(
                            fragment.requireView(),
                            fragment.getString(R.string.error_sync_notes),
                            Snackbar.LENGTH_LONG
                        ).show()
                    }
            }
            return
        }else{
            Snackbar.make(
                fragment.requireView(),
                fragment.getString(R.string.message_offline_sync_notes),
                Snackbar.LENGTH_LONG
            ).show()
            return
        }
    }


    fun update(update: HashMap<String, HashMap<String, Note>>) {
        connected = checkConnection()
        Log.i("Firebase", "checkConnection: connection = $connected")
        if (connected) {
            currentUser?.let { user ->
                dbFirebase
                    .child(NOTE_PATH)
                    .child(user.uid)
                    .updateChildren(update as Map<String, Any>)
                    .addOnSuccessListener {
                        fragment.findNavController().popBackStack()
                        Snackbar.make(
                            fragment.requireView(),
                            fragment.getString(R.string.succesfull_not_save),
                            Snackbar.LENGTH_SHORT
                        ).show()
                        for ((_, value) in update) {
                            for ((_, note) in value) {
                                updateOnLocalDataBase(note)
                            }
                        }

                    }.addOnFailureListener {
                        Snackbar.make(
                            fragment.requireView(),
                            fragment.getString(R.string.error_save_note),
                            Snackbar.LENGTH_SHORT
                        ).show()
                    }.addOnCanceledListener {
                        Snackbar.make(
                            fragment.requireView(),
                            fragment.getString(R.string.error_save_note),
                            Snackbar.LENGTH_SHORT
                        ).show()
                    }
            }
            return
        } else {
            Snackbar.make(
                fragment.requireView(),
                fragment.getString(R.string.message_offline_sync_notes),
                5000
            ).show()
            return
        }
    }
}