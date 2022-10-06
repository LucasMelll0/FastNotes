package com.example.fastnotes.ui.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.example.fastnotes.R
import com.example.fastnotes.databinding.FragmentNoteBinding
import com.example.fastnotes.model.Note
import com.example.fastnotes.repositories.NoteRepository
import com.example.fastnotes.repositories.UserRepository


class NoteFragment : Fragment() {

    private var _binding: FragmentNoteBinding? = null
    private val binding get() = _binding!!
    private val repository by lazy { NoteRepository(this) }
    private val userRepository by lazy { UserRepository(this) }



    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentNoteBinding.inflate(inflater, container, false)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setsUpSaveButton()
        setsUpBackButton()
    }

    private fun setsUpBackButton() {
        binding.imagebuttonBackNoteActivity.setOnClickListener {
            findNavController().navigate(R.id.action_noteFragment_to_notesListFragment)
        }
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
        binding.textinputTitleNoteActivity.editText!!.text.isNotEmpty()


}