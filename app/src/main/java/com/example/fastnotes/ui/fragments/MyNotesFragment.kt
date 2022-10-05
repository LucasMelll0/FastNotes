package com.example.fastnotes.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.fastnotes.databinding.FragmentMyNotesBinding
import com.example.fastnotes.ui.activities.NoteActivity
import com.example.fastnotes.ui.activities.extensions.goTo


class MyNotesFragment : Fragment() {
    private val binding by lazy { FragmentMyNotesBinding.inflate(LayoutInflater.from(requireContext())) }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return binding.root
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setsUpFabAddNote()
    }

    private fun setsUpFabAddNote() {
        binding.fabAddNoteMyNotes.setOnClickListener {
            Toast.makeText(requireContext(), "Testes", Toast.LENGTH_SHORT).show()
        }
    }


}