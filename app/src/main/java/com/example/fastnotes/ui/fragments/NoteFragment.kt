package com.example.fastnotes.ui.fragments

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.example.fastnotes.R
import com.example.fastnotes.database.DATABASE_ERROR
import com.example.fastnotes.database.NOTE_PATH
import com.example.fastnotes.databinding.FragmentNoteBinding
import com.example.fastnotes.extensions.tryLoadImage
import com.example.fastnotes.model.Note
import com.example.fastnotes.repositories.NoteRepository
import com.example.fastnotes.repositories.UserRepository
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener


class NoteFragment : Fragment() {

    private val args: NoteFragmentArgs by navArgs()
    private var _binding: FragmentNoteBinding? = null
    private val binding get() = _binding!!
    private val repository by lazy { NoteRepository(this) }
    private val userRepository by lazy { UserRepository(this) }
    private var noteId: String? = null


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
        checkHasArgs()

    }

    private fun setsUpFabDelete() {
        binding.fabDeleteNoteFragment.setOnClickListener {
            noteId?.let { id ->
                repository.removeNote(id)
            } ?: findNavController().popBackStack()
        }
    }

    private fun checkHasArgs() {
        args.noteId?.let { id ->
            userRepository.getUser()?.let { user ->
                repository
                    .database
                    .child(NOTE_PATH)
                    .child(user.uid)
                    .child(id)
                    .addValueEventListener(object : ValueEventListener {
                        override fun onDataChange(snapshot: DataSnapshot) {
                            if (snapshot.exists()) {
                                val note = snapshot.getValue(Note::class.java) as Note
                                fillFields(note)
                                setsUpFabDelete()
                            }
                        }

                        override fun onCancelled(error: DatabaseError) {
                            Log.e(DATABASE_ERROR, "onCancelled: $error")
                            Snackbar.make(
                                requireView(),
                                getString(R.string.error_loading_note),
                                Snackbar.LENGTH_LONG
                            ).show()
                        }

                    })
            }
        } ?: setsUpFabDelete()
    }

    private fun fillFields(note: Note) {
        binding.apply {
            noteId = note.id
            imageviewNoteActivity.tryLoadImage(note.image)
            textinputTitleNoteActivity.editText?.setText(note.title)
            textinputDescriptionNoteActivity.editText?.setText(note.title)
        }
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
            return if (noteId != null) {
                Note(
                    id = noteId!!,
                    user = userRepository.getUser()?.displayName.toString(),
                    title = title,
                    description = description
                )
            } else {
                Note(
                    user = userRepository.getUser()?.displayName.toString(),
                    title = title,
                    description = description
                )
            }
        }
    }

    private fun titleNotEmpty(): Boolean =
        binding.textinputTitleNoteActivity.editText!!.text.isNotEmpty()


}