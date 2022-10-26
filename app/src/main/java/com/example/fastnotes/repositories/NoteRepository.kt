package com.example.fastnotes.repositories

import androidx.fragment.app.Fragment
import com.example.fastnotes.R
import com.example.fastnotes.database.dao.NoteDao
import com.example.fastnotes.database.firebase.FirebaseDatabaseHelper
import com.example.fastnotes.model.Note
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.flow.Flow


class NoteRepository(
    private val fragment: Fragment,
    private val dao: NoteDao
) {

    private val dbFirebase = FirebaseDatabaseHelper(
        fragment,
        dao
    )

    private val userId = UserRepository(fragment).getUser()?.uid


    suspend fun save(note: Note) {
        dao.save(note)
        Snackbar.make(
            fragment.requireView(),
            fragment.getString(R.string.succesfull_not_save),
            Snackbar.LENGTH_SHORT
        ).show()
        dbFirebase.save(note)
    }

    suspend fun update(note: Note) {
        dao.save(note)
        Snackbar.make(
            fragment.requireView(),
            fragment.getString(R.string.succesfull_not_save),
            Snackbar.LENGTH_SHORT
        ).show()
        updateOnFirebase(note)
    }

    private fun updateOnFirebase(note: Note) {
        val updateMap = HashMap<String, HashMap<String, Note>>()
        val noteMap = HashMap<String, Note>()
        noteMap[note.id] = note
        updateMap[note.key] = noteMap
        dbFirebase.update(updateMap)
    }

    suspend fun trySyncNotes() {
        dao.getAllDisabled().let { notes ->
            if (notes.isNotEmpty()) {
                val notesForDelete = HashMap<String, HashMap<String, Note?>>()
                for (note in notes) {
                    notesForDelete[note.key] ?: run {
                        val notesWithSameKey = dao.getAllByKey(note.key)
                        val notesSameKeyMap = HashMap<String, Note?>()
                        for (noteSameKey in notesWithSameKey) {
                            when (noteSameKey.disabled) {
                                true -> {
                                    notesSameKeyMap[noteSameKey.id] = null
                                }
                                false -> {
                                    notesSameKeyMap[noteSameKey.id] = noteSameKey
                                }
                            }
                        }
                        notesForDelete[note.key] = notesSameKeyMap
                    }
                }
                dbFirebase.remove(notesForDelete)
            }
        }
        dao.getNotSynchronized().let { notes ->
            if (notes.isNotEmpty()) {
                val newNotesMap = HashMap<String, Note>()
                val notesForUpdateMap = HashMap<String, HashMap<String, Note>>()
                for (note in notes) {
                    if (note.key.isNotEmpty()) {
                        getNotSyncUpdates(notesForUpdateMap, note.key)
                    } else {
                        newNotesMap[note.id] = note
                    }
                }
                if (notesForUpdateMap.isNotEmpty()) {
                    dbFirebase.update(notesForUpdateMap)
                }
                if (newNotesMap.isNotEmpty()) {
                    dbFirebase.save(newNotesMap)
                }
            }

        }
    }

    private suspend fun getNotSyncUpdates(
        notesForUpdateMap: HashMap<String, HashMap<String, Note>>,
        key: String
    ) {
        val notesWithSameKey = dao.getAllByKey(key)
        val notesSameKeyMap = HashMap<String, Note>()
        for (noteSameKey in notesWithSameKey) {
            notesSameKeyMap[noteSameKey.id] = noteSameKey
        }
        notesForUpdateMap[key] = notesSameKeyMap

    }


    fun getUserNotes(): Flow<List<Note>>? {
        return userId?.let {
            dao.getAll(userId)
        }
    }


    suspend fun remove(note: Note) {
        dao.disable(note.id)
        Snackbar.make(
            fragment.requireView(),
            fragment.getString(R.string.note_delet_success),
            Snackbar.LENGTH_SHORT
        ).show()
        if (note.key.isNotEmpty()) {
            val notesWithSameKey = dao.getAllByKey(note.key)
            val noteForDeleteMap = HashMap<String, HashMap<String, Note?>>()
            val noteMap = HashMap<String, Note?>()
            for (noteSameKey in notesWithSameKey) {
                noteMap[noteSameKey.id] = noteSameKey
            }
            noteMap[note.id] = null
            noteForDeleteMap[note.key] = noteMap
            dbFirebase.remove(noteForDeleteMap)
        }
    }


}