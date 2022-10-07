package com.example.fastnotes

import android.app.Application
import com.google.firebase.database.FirebaseDatabase


class Application : Application() {

    override fun onCreate() {
        super.onCreate()

        val database = FirebaseDatabase.getInstance()
        database.setPersistenceEnabled(true)
    }
}

