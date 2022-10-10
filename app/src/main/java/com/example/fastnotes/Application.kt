package com.example.fastnotes

import android.app.Application
import com.example.fastnotes.database.NOTE_PATH
import com.google.firebase.database.FirebaseDatabase


class Application : Application() {

    override fun onCreate() {
        super.onCreate()

        val database = FirebaseDatabase.getInstance()
        database.setPersistenceEnabled(true)
        database.getReference(NOTE_PATH).keepSynced(true)
    }
}

