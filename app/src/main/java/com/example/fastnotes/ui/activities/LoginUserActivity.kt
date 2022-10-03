package com.example.fastnotes.ui.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.fastnotes.databinding.ActivityLoginUserBinding
import com.example.fastnotes.model.User
import com.example.fastnotes.repositories.UserRepository
import com.example.fastnotes.ui.activities.extensions.goTo
import com.google.firebase.auth.FirebaseAuth

class LoginUserActivity : AppCompatActivity() {

    private val binding by lazy { ActivityLoginUserBinding.inflate(layoutInflater) }
    private val repository by lazy { UserRepository(this) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        checkAreConnected()
        setsUpFab()
        setsUpRegisterText()
    }

    private fun checkAreConnected() {
        repository.getUser()?.let {
            goTo(NotesListActivity::class.java)
            finish()
        }
    }

    private fun setsUpFab() {
        binding.fabLogin.setOnClickListener {
            if(fieldsNotEmpty()){
                val user = createUser()
                    repository.connect(user)
            }
        }
    }

    private fun createUser() : User {
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
            return !(edittextEmailLogin.editText!!.text.isEmpty()
                    && edittextPasswordLogin.editText!!.text.isEmpty())
        }
    }

    private fun setsUpRegisterText() {
        binding.textviewRegisterMessageLogin.setOnClickListener {
            goTo(RegisterUserActivity::class.java)
        }
    }
}