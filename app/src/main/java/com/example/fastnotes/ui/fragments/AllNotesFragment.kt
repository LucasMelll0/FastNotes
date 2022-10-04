package com.example.fastnotes.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.fastnotes.databinding.FragmentAllNotesBinding
import com.example.fastnotes.ui.recyclerview.adapter.NotesAdapter


class AllNotesFragment : Fragment() {

    private val binding by lazy { FragmentAllNotesBinding.inflate(LayoutInflater.from(requireContext())) }
    private val adapter by lazy { NotesAdapter(requireContext()) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setsUpRecyclerView()
    }

    private fun setsUpRecyclerView() {
        binding.recyclerviewNotesAllnotes.adapter = adapter

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return binding.root
    }
}