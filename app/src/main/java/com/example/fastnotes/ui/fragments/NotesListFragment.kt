package com.example.fastnotes.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.addCallback
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.fastnotes.R
import com.example.fastnotes.databinding.FragmentNotesListBinding
import com.example.fastnotes.repositories.UserRepository
import com.example.fastnotes.ui.viewpager.ViewPagerAdapter
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator

class NotesListFragment : Fragment() {

    private var _binding: FragmentNotesListBinding? = null
    private val binding get() = _binding!!
    private val repository by lazy { UserRepository(this) }

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
            repository.disconnect()
            findNavController().navigate(R.id.action_notesListFragment_to_loginFragment)
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