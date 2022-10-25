package com.example.fastnotes.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.example.fastnotes.databinding.FragmentNoteDetailsBinding
import com.example.fastnotes.extensions.tryLoadImage
import com.example.fastnotes.model.Note


class NoteDetailsFragment : Fragment() {

    private val args: NoteDetailsFragmentArgs by navArgs()
    private var _binding: FragmentNoteDetailsBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentNoteDetailsBinding.inflate(inflater, container, false)

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
            if (note.image.isNotEmpty()){
                imageviewAllNoteFragment.tryLoadImage(note.image)
                cardviewImageview.visibility = View.VISIBLE
            }
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