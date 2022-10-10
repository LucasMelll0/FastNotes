package com.example.fastnotes.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.example.fastnotes.R
import com.example.fastnotes.database.NOTE_PATH
import com.example.fastnotes.databinding.FragmentAllNoteBinding
import com.example.fastnotes.extensions.tryLoadImage
import com.example.fastnotes.model.Note
import com.example.fastnotes.repositories.NoteRepository
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener


class AllNoteFragment : Fragment() {

    private val args: AllNoteFragmentArgs by navArgs()
    private var _binding: FragmentAllNoteBinding? = null
    private val binding get() = _binding!!
    private val noteRepository by lazy { NoteRepository(this) }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAllNoteBinding.inflate(inflater, container, false)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        checkHasArgs()
        setsUpBackButton()
    }

    private fun setsUpBackButton() {
        binding.imagebuttonBackAllNoteActivity.setOnClickListener {
            findNavController().popBackStack()
        }
    }

    private fun checkHasArgs() {
        args.note?.let { note ->
            fiilFields(note)
        } ?: findNavController().popBackStack()
    }

    private fun fiilFields(note: Note) {
        binding.apply {
            imageviewAllNoteFragment.tryLoadImage(note.image)
            textviewTitleAllNote.text = note.title
            textviewUserNameAllNote.text = note.user
            textviewDescriptionAllNote.text = note.description
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}