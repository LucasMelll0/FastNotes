package com.example.fastnotes.repositories

import com.example.fastnotes.database.dao.NoteDao
import com.example.fastnotes.model.Note
import kotlinx.coroutines.flow.Flow

class NoteRepository(
    val dao: NoteDao
) {
    suspend fun getAll() : Flow<List<Note>> {
        return dao.getAll()
    }

    suspend fun save(note: Note){
        dao.save(note)
    }
}