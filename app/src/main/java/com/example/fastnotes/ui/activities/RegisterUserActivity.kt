package com.example.fastnotes.ui.activities

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.fastnotes.R
import com.example.fastnotes.databinding.ActivityRegisterUserBinding
import com.example.fastnotes.model.User
import com.example.fastnotes.repositories.UserRepository
import com.google.android.material.snackbar.Snackbar

class RegisterUserActivity : AppCompatActivity() {

    private val binding by lazy { ActivityRegisterUserBinding.inflate(layoutInflater) }
    private val repository by lazy { UserRepository(this) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        setsUpFab()
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
            val passWord = edittextPasswordRegister.editText!!.text.toString().trim()
            val confirmationPassword = edittextConfirmPasswordRegister.editText!!.text.toString().trim()

            return if(passWord.equals(confirmationPassword)){
                true
            }else{
                Snackbar.make(
                    binding.root,
                    R.string.error_password_not_same,
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
            ){
                Snackbar.make(
                    binding.root,
                    R.string.error_empty_field,
                    Snackbar.LENGTH_SHORT
                ).show()
                false
            }else{
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