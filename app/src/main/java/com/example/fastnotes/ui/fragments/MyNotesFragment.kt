package com.example.fastnotes.ui.fragments

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.example.fastnotes.database.AppDataBase
import com.example.fastnotes.databinding.FragmentMyNotesBinding
import com.example.fastnotes.repositories.NoteRepository
import com.example.fastnotes.ui.recyclerview.adapter.NotesAdapter
import kotlinx.coroutines.launch


class MyNotesFragment : Fragment() {

    private val binding by lazy { FragmentMyNotesBinding.inflate(LayoutInflater.from(requireContext())) }
    private val repository by lazy {
        NoteRepository(
            this,
            AppDataBase
                .instance(requireContext())
                .noteDao()
        )
    }
    private val adapter by lazy { NotesAdapter(requireContext(), userFieldEnable = false) }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return binding.root
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        lifecycleScope.launch {
            launch {
                repeatOnLifecycle(Lifecycle.State.RESUMED) {
                    getNotes()
                }
            }
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setsUpFabAddNote()
        setsUpRecyclerView()
    }


    private fun setsUpRecyclerView() {
        binding.recyclerviewNotesMynotes.adapter = adapter
        binding.recyclerviewNotesMynotes.setHasFixedSize(true)
        binding.recyclerviewNotesMynotes.layoutManager = StaggeredGridLayoutManager(2, 1)
        setsUpAdapterFunctions()
        setsUpSwipeRefresh()
    }

    private fun setsUpAdapterFunctions() {
        adapter.whenClickItem = { note ->
            val action = NotesListFragmentDirections
                .actionNotesListFragmentToFormNoteFragment(note)
            findNavController().navigate(action)
        }
    }

    private fun setsUpSwipeRefresh() {
        val swipe = binding.swipeRefreshUserNotes
        swipe.setOnRefreshListener {
            lifecycleScope.launch {
                launch {
                    repository.trySyncNotes()
                }
                launch {
                    swipe.isRefreshing = false
                }
            }
        }
    }

    private suspend fun getNotes() {
        repository.getUserNotes()
            .collect { notes ->
                adapter.update(notes)
            }
    }

    private fun setsUpFabAddNote() {
        binding.fabAddNoteMyNotes.setOnClickListener {
            NotesListFragmentDirections.actionNotesListFragmentToFormNoteFragment(null).apply {
                findNavController().navigate(this)
            }
        }
    }


}