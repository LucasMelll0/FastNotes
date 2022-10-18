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
import com.example.fastnotes.database.AppDataBase
import com.example.fastnotes.database.DATABASE_ERROR
import com.example.fastnotes.database.NOTE_PATH
import com.example.fastnotes.database.firebase.FirebaseDatabaseHelper
import com.example.fastnotes.databinding.FragmentAllNotesBinding
import com.example.fastnotes.model.Note
import com.example.fastnotes.repositories.NoteRepository
import com.example.fastnotes.repositories.UserRepository
import com.example.fastnotes.ui.recyclerview.adapter.NotesAdapter
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener


class AllNotesFragment : Fragment() {

    private val binding by lazy { FragmentAllNotesBinding.inflate(LayoutInflater.from(requireContext())) }
    private val noteRepository by lazy {
        NoteRepository(
            this,
            AppDataBase.instance(requireContext()).noteDao()
        )
    }
    private val userRepository by lazy { UserRepository(this) }
    private val noteList = mutableListOf<Note>()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        getNotes()
    }

    private fun getNotes() {
        userRepository.getUser()?.let {
            FirebaseDatabaseHelper
                .dbFirebase
                .child(NOTE_PATH)
                .addValueEventListener(object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        if (snapshot.exists()) {
                            noteList.clear()
                            for (snapUser in snapshot.children) {
                                if (snapUser.exists()) {
                                    for (snapKey in snapUser.children) {
                                        if (snapKey.exists()) {
                                            for (snapNote in snapKey.children){
                                                if (snapNote.exists()){
                                                    val note = snapNote.getValue(Note::class.java) as Note
                                                    if (note.public) noteList.add(note)
                                                }
                                            }
                                        }
                                    }
                                }
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
        val adapter = NotesAdapter(requireContext(), noteList, true)
        adapter.whenClickItem = { note ->
            val action = NotesListFragmentDirections
                .actionNotesListFragmentToNoteDetailsFragment(note)
            findNavController().navigate(action)
        }
        binding.recyclerviewNotesAllnotes.apply {
            this.adapter = adapter
            layoutManager =
                StaggeredGridLayoutManager(2, 1) // Orientation 0 = horizontal | 1 = vertical
        }

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return binding.root
    }
}