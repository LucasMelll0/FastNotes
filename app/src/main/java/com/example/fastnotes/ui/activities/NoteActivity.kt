package com.example.fastnotes.ui.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.fastnotes.database.AppDataBase
import com.example.fastnotes.databinding.ActivityNoteBinding
import com.example.fastnotes.repositories.NoteRepository

class NoteActivity : AppCompatActivity() {

    private val binding by lazy { ActivityNoteBinding.inflate(layoutInflater) }
    private val repository by lazy {
        NoteRepository(AppDataBase
            .instance(this)
            .noteDao())
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
    }
}