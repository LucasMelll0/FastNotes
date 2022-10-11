package com.example.fastnotes.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.fastnotes.database.dao.NoteDao
import com.example.fastnotes.model.Note

@Database(
    version = 1,
    entities = [Note::class],
    exportSchema = true
)


abstract class AppDataBase : RoomDatabase() {

    abstract fun noteDao(): NoteDao

    companion object {

        @Volatile
        private var db: AppDataBase? = null

        fun instance(context: Context): AppDataBase {
            return db ?: Room.databaseBuilder(
                context,
                AppDataBase::class.java,
                DATABASE_NAME
            ).build()
        }
    }
}