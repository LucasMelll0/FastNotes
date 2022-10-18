package com.example.fastnotes.repositories

import androidx.fragment.app.Fragment
import com.example.fastnotes.database.dao.NoteDao
import com.example.fastnotes.database.firebase.FirebaseDatabaseHelper
import com.example.fastnotes.model.Note


class NoteRepository(
    fragment: Fragment,
    private val dao: NoteDao
) {

    val dbFirebase = FirebaseDatabaseHelper(
        fragment,
        dao
    )


    suspend fun save(note: Note) {
        dao.save(note)
        dbFirebase.save(note)
    }

    suspend fun update(note: Note) {
        dao.save(note)
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


    fun getUserNotes() = dao.getAll()


    suspend fun remove(note: Note) {
        dao.disable(note.id)
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