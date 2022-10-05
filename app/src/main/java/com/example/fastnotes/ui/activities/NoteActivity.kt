package com.example.fastnotes.ui.activities

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.fastnotes.databinding.ActivityNoteBinding
import com.example.fastnotes.model.Note
import com.example.fastnotes.repositories.NoteRepository
import com.example.fastnotes.repositories.UserRepository

class NoteActivity : AppCompatActivity() {

    /*private val binding by lazy { ActivityNoteBinding.inflate(layoutInflater) }
    private val repository by lazy { NoteRepository(this) }
    private val userRepository by lazy { UserRepository(this) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        setsUpSaveButton()
    }

    private fun setsUpSaveButton() {
        binding.imagebuttonConfirmNoteActivity.setOnClickListener {
            if (titleNotEmpty()) {
                val note = createNote()
                repository.saveNote(note)
            }
        }
    }

    private fun createNote(): Note {
        binding.apply {
            val title = textinputTitleNoteActivity.editText!!.text.toString().trim()
            val description = textinputDescriptionNoteActivity.editText!!.text.toString().trim()
            return Note(
                user = userRepository.getUser()?.displayName.toString(),
                title = title,
                description = description
            )
        }
    }

    private fun titleNotEmpty(): Boolean =
        binding.textinputTitleNoteActivity.editText!!.text.isNotEmpty()*/

}