package com.example.fastnotes.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.example.fastnotes.database.AppDataBase
import com.example.fastnotes.database.dao.NoteDao
import com.example.fastnotes.databinding.FragmentAllNotesBinding
import com.example.fastnotes.repositories.NoteRepository
import com.example.fastnotes.ui.recyclerview.adapter.NotesAdapter
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch


class AllNotesFragment : Fragment() {

    private val binding by lazy { FragmentAllNotesBinding.inflate(LayoutInflater.from(requireContext())) }
    private val adapter by lazy { NotesAdapter(requireContext()) }
    private val repository by lazy { NoteRepository(AppDataBase.instance(requireContext()).noteDao()) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setsUpRecyclerView()
    }

    private fun setsUpRecyclerView() {
        binding.recyclerviewNotesAllnotes.adapter = adapter
        lifecycleScope.launch {
            repository.getAll().collect{ notes ->
                adapter.addAll(notes)
            }
        }

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return binding.root
    }
}