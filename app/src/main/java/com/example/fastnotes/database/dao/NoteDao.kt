package com.example.fastnotes.database.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy.REPLACE
import androidx.room.Query
import com.example.fastnotes.model.Note
import kotlinx.coroutines.flow.Flow


@Dao
interface NoteDao {

    @Insert(onConflict = REPLACE)
    suspend fun save(note: Note)

    @Query("SELECT * FROM Note WHERE disabled = 0")
    fun getAll() : Flow<List<Note>>

    @Query("SELECT * FROM Note WHERE id = :id")
    fun get(id: String) : Flow<Note>

    @Delete
    suspend fun delete(note: Note)
}