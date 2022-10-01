package com.example.fastnotes.ui.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.widget.Toast
import androidx.core.view.isEmpty
import com.example.fastnotes.R
import com.example.fastnotes.databinding.ActivityRegisterUserBinding
import com.example.fastnotes.model.User
import com.example.fastnotes.repositories.UserRepository
import com.google.firebase.auth.FirebaseAuth

const val TAG = "Register Tests"
class RegisterUserActivity : AppCompatActivity() {

    private val binding by lazy { ActivityRegisterUserBinding.inflate(layoutInflater) }
    private val repository by lazy { UserRepository(this) }
    private val firebaseAuth = FirebaseAuth.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        setsUpFab()
        Log.i(TAG, "onCreate: ${firebaseAuth.currentUser!!.displayName}")
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

            return if(passWord.equals(confirmationPassword)){
                true
            }else{
                Toast.makeText(this@RegisterUserActivity, "The passwords is not same!!", Toast.LENGTH_SHORT).show()
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
                Toast.makeText(this@RegisterUserActivity, getString(R.string.error_empty_field), Toast.LENGTH_SHORT).show()
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