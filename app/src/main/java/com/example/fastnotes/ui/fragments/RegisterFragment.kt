package com.example.fastnotes.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.fastnotes.R
import com.example.fastnotes.database.AppDataBase
import com.example.fastnotes.databinding.FragmentRegisterBinding
import com.example.fastnotes.model.User
import com.example.fastnotes.repositories.NoteRepository
import com.example.fastnotes.repositories.UserRepository
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth


class RegisterFragment : Fragment() {

    private var _binding: FragmentRegisterBinding? = null
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

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setsUpFab()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentRegisterBinding.inflate(inflater, container, false)
        return binding.root
    }

    private fun setsUpFab() {
        binding.fabRegister.setOnClickListener {
            tryRegisterNewUser()
        }
    }

    private fun tryRegisterNewUser() {
        if (noEmptyField() && samePasswords()) {
            val user = createUser()
            repository.register(user)
        }
    }

    private fun samePasswords(): Boolean {
        binding.apply {
            val passWord = edittextPasswordRegister.editText!!.text.toString()
            val confirmationPassword = edittextConfirmPasswordRegister.editText!!.text.toString()

            return if (passWord == confirmationPassword) {
                true
            } else {
                Snackbar.make(
                    requireView(),
                    "The passwords is not same!!",
                    Snackbar.LENGTH_SHORT
                ).show()
                false
            }
        }
    }

    private fun noEmptyField(): Boolean {
        binding.apply {
            return if (edittextUsernameRegister.editText?.text!!.isEmpty()
                || edittextEmailRegister.editText?.text!!.isEmpty()
                || edittextPasswordRegister.editText?.text!!.isEmpty()
                || edittextConfirmPasswordRegister.editText?.text!!.isEmpty()
            ) {
                Snackbar.make(
                    requireView(),
                    getString(R.string.error_empty_field),
                    Snackbar.LENGTH_SHORT
                ).show()
                false
            } else {
                true
            }
        }
    }

    private fun createUser(): User {
        binding.apply {
            val email = edittextEmailRegister.editText!!.text.toString().trim()
            val name = edittextUsernameRegister.editText!!.text.toString().trim()
            val password = edittextConfirmPasswordRegister.editText!!.text.toString().trim()
            return User(
                email = email,
                name = name,
                password = password
            )
        }
    }

}