package com.example.fastnotes.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.example.fastnotes.database.AppDataBase
import com.example.fastnotes.databinding.FragmentFormNoteBinding
import com.example.fastnotes.extensions.tryLoadImage
import com.example.fastnotes.model.Note
import com.example.fastnotes.repositories.NoteRepository
import com.example.fastnotes.repositories.UserRepository
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch


class FormNoteFragment : Fragment() {

    private val args: FormNoteFragmentArgs by navArgs()
    private var _binding: FragmentFormNoteBinding? = null
    private val binding get() = _binding!!
    private val repository by lazy {
        NoteRepository(
            this,
            AppDataBase
                .instance(requireContext())
                .noteDao()
        )
    }
    private val userRepository by lazy { UserRepository(this) }
    private var noteId: String? = null
    private var noteKey: String? = null


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentFormNoteBinding.inflate(inflater, container, false)

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
                lifecycleScope.launch {
                    val note = createNote()
                    repository.remove(note)
                }
            } ?: findNavController().popBackStack()
        }
    }

    private fun checkHasArgs() {
        args.note?.let { note ->
            fillFields(note)
            setsUpFabDelete()
        } ?: setsUpFabDelete()
    }

    private fun fillFields(note: Note) {
        binding.apply {
            noteId = note.id
            noteKey = note.key
            imageviewInputNoteFragment.tryLoadImage(note.image)
            textinputTitleInputNoteFragment.editText?.setText(note.title)
            textinputDescriptionInputNoteFragment.editText?.setText(note.description)
        }
    }

    private fun setsUpBackButton() {
        binding.imagebuttonBackNoteActivity.setOnClickListener {
            findNavController().popBackStack()
        }
    }

    private fun setsUpSaveButton() {
        binding.imagebuttonConfirmNoteActivity.setOnClickListener {
            if (titleNotEmpty()) {
                val note = createNote()
                lifecycleScope.launch {
                    noteId?.let {
                        repository.update(note)
                    } ?: repository.save(note)
                }
            }
        }
    }

    private fun createNote(): Note {
        binding.apply {
            val title = textinputTitleInputNoteFragment.editText!!.text.toString().trim()
            val description = textinputDescriptionInputNoteFragment.editText!!.text.toString().trim()
            return if (noteId != null) {
                Note(
                    id = noteId!!,
                    key = noteKey!!,
                    user = userRepository.getUser()?.displayName.toString(),
                    userId = userRepository.getUser()!!.uid,
                    title = title,
                    description = description
                )
            } else {
                Note(
                    user = userRepository.getUser()?.displayName.toString(),
                    userId = userRepository.getUser()!!.uid,
                    title = title,
                    description = description
                )
            }
        }
    }

    private fun titleNotEmpty(): Boolean =
        binding.textinputTitleInputNoteFragment.editText!!.text.isNotEmpty()


}