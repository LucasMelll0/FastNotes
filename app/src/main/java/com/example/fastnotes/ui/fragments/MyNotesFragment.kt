package com.example.fastnotes.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.fastnotes.databinding.FragmentMyNotesBinding


class MyNotesFragment : Fragment() {
    private val binding by lazy { FragmentMyNotesBinding.inflate(LayoutInflater.from(requireContext())) }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return binding.root
    }

}