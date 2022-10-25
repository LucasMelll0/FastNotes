package com.example.fastnotes.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.addCallback
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import com.example.fastnotes.R
import com.example.fastnotes.database.AppDataBase
import com.example.fastnotes.databinding.FragmentNotesListBinding
import com.example.fastnotes.extensions.showDialog
import com.example.fastnotes.repositories.NoteRepository
import com.example.fastnotes.repositories.UserRepository
import com.example.fastnotes.ui.viewpager.ViewPagerAdapter
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import kotlinx.coroutines.launch

class NotesListFragment : Fragment() {

    private var _binding: FragmentNotesListBinding? = null
    private val binding get() = _binding!!
    private val repository by lazy { UserRepository(this) }
    private val notesRepository by lazy {
        NoteRepository(
            this,
            AppDataBase.instance(requireContext()).noteDao()
        )
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentNotesListBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setsUpToolbar()
        setsUpTabLayout()
        trySyncNotes()
    }

    private fun trySyncNotes() {
        lifecycleScope.launch {
            notesRepository.trySyncNotes()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setsUpOnBackPressed()
    }


    private fun setsUpOnBackPressed() {
        requireActivity().onBackPressedDispatcher.addCallback(this) {
            requireActivity().finish()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun setsUpToolbar() {
        setsUpDisconnectButton()
        setsUpProfileButton()
    }

    private fun setsUpProfileButton() {
        binding.imagebuttonProfileUser.setOnClickListener {
            Toast.makeText(requireContext(), "User Profile", Toast.LENGTH_SHORT).show()
        }
    }

    private fun setsUpDisconnectButton() {
        binding.imagebuttonDisconnectUser.setOnClickListener {
            showDialog(message = R.string.disconnect_question) {
                binding.progressbarListFragment.visibility = View.VISIBLE
                repository.disconnect()
                binding.progressbarListFragment.visibility = View.GONE
                findNavController().navigate(R.id.action_notesListFragment_to_loginFragment)
            }
        }
    }

    private fun setsUpTabLayout() {
        val adapter = ViewPagerAdapter(requireActivity())
        binding.apply {
            viewpager.adapter = adapter
            adapter.addFragment(MyNotesFragment(), getString(R.string.my_notes))
            adapter.addFragment(AllNotesFragment(), getString(R.string.all_notes))
            viewpager.offscreenPageLimit = adapter.itemCount
            val mediator =
                TabLayoutMediator(tablayout, viewpager) { tab: TabLayout.Tab, position: Int ->
                    tab.text = adapter.getTitle(position)
                }
            mediator.attach()
        }
    }

}