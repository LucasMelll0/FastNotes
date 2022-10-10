package com.example.fastnotes.ui.fragments

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.example.fastnotes.R
import com.example.fastnotes.database.DATABASE_ERROR
import com.example.fastnotes.database.NOTE_PATH
import com.example.fastnotes.databinding.FragmentMyNotesBinding
import com.example.fastnotes.model.Note
import com.example.fastnotes.repositories.NoteRepository
import com.example.fastnotes.repositories.UserRepository
import com.example.fastnotes.ui.recyclerview.adapter.NotesAdapter
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener


class MyNotesFragment : Fragment() {

    private val binding by lazy { FragmentMyNotesBinding.inflate(LayoutInflater.from(requireContext())) }
    private val repository by lazy { NoteRepository(this) }
    private val userRepository by lazy { UserRepository(this) }
    private val noteList = mutableListOf<Note>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        getNotes()
        setsUpFabAddNote()
    }

    private fun getNotes() {
        userRepository.getUser()?.let { user ->
            repository
                .database
                .child(NOTE_PATH)
                .child(user.uid)
                .addValueEventListener(object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        if (snapshot.exists()) {
                            noteList.clear()
                            for (snap in snapshot.children) {
                                val note = snap.getValue(Note::class.java) as Note
                                noteList.add(note)
                            }
                            noteList.reverse()
                            setsUpRecyclerView()
                        }
                    }

                    override fun onCancelled(error: DatabaseError) {
                        Log.e(DATABASE_ERROR, "onCancelled: $error")
                        Snackbar.make(
                            requireView(),
                            getString(R.string.error_on_get_notes),
                            Snackbar.LENGTH_LONG
                        ).show()
                    }
                })
        }
    }

    private fun setsUpRecyclerView() {
        val adapter = NotesAdapter(requireContext(), noteList, false)
        adapter.whenClickItem = { note ->
            val action = NotesListFragmentDirections
                .actionNotesListFragmentToMyNoteFragment(note)
                findNavController().navigate(action)
        }
        binding.recyclerviewNotesMynotes.setHasFixedSize(true)
        binding.recyclerviewNotesMynotes.layoutManager = StaggeredGridLayoutManager(2, 1)
        binding.recyclerviewNotesMynotes.adapter = adapter
    }

    private fun setsUpFabAddNote() {
        binding.fabAddNoteMyNotes.setOnClickListener {
            NotesListFragmentDirections.actionNotesListFragmentToMyNoteFragment(null).apply {
                findNavController().navigate(this)
            }
        }
    }


}