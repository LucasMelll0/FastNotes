package com.example.fastnotes.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy.REPLACE
import androidx.room.Query
import com.example.fastnotes.model.Note
import kotlinx.coroutines.flow.Flow


@Dao
interface NoteDao {


    @Insert(onConflict = REPLACE)
    suspend fun save(note: Note)

    @Insert(onConflict = REPLACE)
    suspend fun save(notes: List<Note>)

    @Query("SELECT * FROM Note WHERE disabled = 0")
    fun getAll(): Flow<List<Note>>

    @Query("SELECT * FROM Note WHERE id = :noteId AND disabled = 0")
    fun getById(noteId: String): Flow<Note>

    @Query("DELETE FROM Note WHERE id = :id")
    suspend fun remove(id: String)

    @Query("UPDATE Note SET disabled = 1 WHERE id = :id")
    suspend fun disable(id: String)

    @Query("SELECT * FROM Note WHERE synchronized = 0")
    fun getNotSynchronized(): Flow<List<Note>>

    @Query("SELECT * FROM Note WHERE disabled = 1")
    fun getAllDisabled(): Flow<List<Note>>

    @Query("SELECT * FROM Note WHERE `key` = :key")
    suspend fun getAllByKey(key: String): List<Note>
}