package com.example.fastnotes.ui.fragments

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.example.fastnotes.R
import com.example.fastnotes.database.AppDataBase
import com.example.fastnotes.databinding.FragmentMyNotesBinding
import com.example.fastnotes.extensions.showDialog
import com.example.fastnotes.repositories.NoteRepository
import com.example.fastnotes.ui.recyclerview.adapter.NotesAdapter
import kotlinx.coroutines.launch

const val USER_NOTES_LIST_LAYOUT = "USER_NOTES_LIST_LAYOUT"


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
    private val sharedPreference: SharedPreferences by lazy {
        requireContext().getSharedPreferences(
            USER_NOTES_LIST_LAYOUT,
            Context.MODE_PRIVATE
        )
    }


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
        setsUpFabSwitchListLayout()

    }

    private fun setsUpFabSwitchListLayout() {
        binding.apply {
            binding.fabSwitchLayoutMyNotes.setOnClickListener {
                switchRecyclerViewLayoutManager()
            }
        }
    }

    private fun switchRecyclerViewLayoutManager() {
        binding.apply {
            val editor = sharedPreference.edit()
            val staggeredLayout = sharedPreference.getBoolean("staggeredLayout", true)
            if (staggeredLayout) {
                recyclerviewNotesMynotes.layoutManager = LinearLayoutManager(requireContext())
                fabSwitchLayoutMyNotes.setImageResource(R.drawable.ic_staggeredgrid)
                editor.putBoolean("staggeredLayout", false)
                editor.apply()
            } else {
                recyclerviewNotesMynotes.layoutManager = StaggeredGridLayoutManager(2, 1)
                fabSwitchLayoutMyNotes.setImageResource(R.drawable.ic_linear_orientation)
                editor.putBoolean("staggeredLayout", true)
                editor.apply()
            }
        }
    }


    private fun setsUpRecyclerView() {
        binding.recyclerviewNotesMynotes.adapter = adapter
        binding.recyclerviewNotesMynotes.setHasFixedSize(true)
        setsUpRecyclerViewLayoutManager()
        setsUpAdapterFunctions()
        setsUpSwipeRefresh()
    }

    private fun setsUpRecyclerViewLayoutManager() {
        binding.apply {
            val staggeredLayout = sharedPreference.getBoolean("staggeredLayout", true)
            if (staggeredLayout) {
                recyclerviewNotesMynotes.layoutManager = StaggeredGridLayoutManager(2, 1)
                fabSwitchLayoutMyNotes.setImageResource(R.drawable.ic_linear_orientation)
            } else {
                recyclerviewNotesMynotes.layoutManager = LinearLayoutManager(requireContext())
                fabSwitchLayoutMyNotes.setImageResource(R.drawable.ic_staggeredgrid)
            }
        }
    }

    private fun setsUpAdapterFunctions() {
        adapter.whenClickItem = { note ->
            val action = NotesListFragmentDirections
                .actionNotesListFragmentToFormNoteFragment(note.id)
            findNavController().navigate(action)
        }
        adapter.whenClickDelete = { note ->
            showDialog {
                lifecycleScope.launch {
                    binding.progressbarMyNotesFragment.visibility = View.VISIBLE
                    repository.remove(note)
                    binding.progressbarMyNotesFragment.visibility = View.GONE
                }
            }
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
        binding.progressbarMyNotesFragment.visibility = View.VISIBLE
        repository.getUserNotes()?.let {
            it.collect { notes ->
                adapter.update(notes)
                binding.progressbarMyNotesFragment.visibility = View.GONE
            }
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