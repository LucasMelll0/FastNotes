package com.example.fastnotes.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.fastnotes.database.AppDataBase
import com.example.fastnotes.databinding.FragmentResetPasswordBinding
import com.example.fastnotes.repositories.NoteRepository
import com.example.fastnotes.repositories.UserRepository
import com.google.firebase.auth.FirebaseAuth

class ResetPasswordFragment : Fragment() {

    private var _binding: FragmentResetPasswordBinding? = null
    private val binding get() = _binding!!
    private val repository by lazy {
        UserRepository(
            this,
            FirebaseAuth.getInstance(),
            NoteRepository(
                this,
                AppDataBase.instance(requireContext()).noteDao()
            )
        )
    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentResetPasswordBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setsUpBackButton()
        setsUpFabSubmit()

    }

    private fun setsUpFabSubmit() {
        binding.apply {
            fabSubmitResetPassword.setOnClickListener {
                if (fieldNotEmpty()) {
                    val email = binding.edittextEmailResetPassword.editText?.text.toString()
                    repository.sendPasswordResetEmail(email)
                }
            }
        }
    }

    private fun fieldNotEmpty(): Boolean =
        binding.edittextEmailResetPassword.editText?.text.toString().isNotEmpty()


    private fun setsUpBackButton() {
        binding.imagebuttonBackResetPassword.setOnClickListener {
            findNavController().popBackStack()
        }
    }

}


