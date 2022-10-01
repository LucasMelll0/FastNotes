package com.example.fastnotes

import android.app.Application
import com.example.fastnotes.database.AppDataBase

class ApplicationClass : Application() {

    override fun onCreate() {
        super.onCreate()
        AppDataBase.instance(this)
    }
}