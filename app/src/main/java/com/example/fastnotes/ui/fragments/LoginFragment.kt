package com.example.fastnotes.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.addCallback
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.fastnotes.R
import com.example.fastnotes.databinding.FragmentLoginBinding
import com.example.fastnotes.model.User
import com.example.fastnotes.repositories.UserRepository
import com.google.android.material.snackbar.Snackbar

class LoginFragment : Fragment() {

    private var _binding: FragmentLoginBinding? = null
    private val binding get() = _binding!!

    private val repository by lazy { UserRepository(this) }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentLoginBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        checkAreConnected()
        setsUpFab()
        setsUpRegisterText()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requireActivity().onBackPressedDispatcher.addCallback(this) {
            requireActivity().finish()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun checkAreConnected() {
        repository.getUser()?.let {
            findNavController().navigate(R.id.action_loginFragment_to_notesListFragment)
        }
    }

    private fun setsUpFab() {
        binding.fabLogin.setOnClickListener {
            if (fieldsNotEmpty()) {
                val user = createUser()
                repository.connect(user)
            }
        }
    }

    private fun createUser(): User {
        binding.apply {
            val email = edittextEmailLogin.editText!!.text.toString()
            val password = edittextPasswordLogin.editText!!.text.toString()

            return User(
                email = email,
                password = password
            )
        }
    }

    private fun fieldsNotEmpty(): Boolean {
        binding.apply {
            return if (edittextEmailLogin.editText!!.text.isEmpty()
                && edittextPasswordLogin.editText!!.text.isEmpty()
            ) {
                Snackbar.make(
                    binding.root,
                    R.string.error_empty_field,
                    Snackbar.LENGTH_SHORT
                ).show()
                false
            } else {
                true
            }
        }
    }

    private fun setsUpRegisterText() {
        binding.textviewRegisterMessageLogin.setOnClickListener {
            findNavController().navigate(R.id.action_loginFragment_to_registerFragment)
        }
    }


}