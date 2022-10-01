package com.example.fastnotes.ui.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.fastnotes.databinding.ActivityLoginUserBinding
import com.example.fastnotes.ui.activities.extensions.goTo
import com.google.firebase.auth.FirebaseAuth

class LoginUserActivity : AppCompatActivity() {

    private val binding by lazy { ActivityLoginUserBinding.inflate(layoutInflater) }
    private val currentUser by lazy { FirebaseAuth.getInstance().currentUser }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        checkAreConnected()
        setsUpFab()
        setsUpRegisterText()
    }

    private fun checkAreConnected() {
        currentUser?.let {
            goTo(NotesListActivity::class.java)
            finish()
        }
    }

    private fun setsUpFab() {
        binding.fabLogin.setOnClickListener {
            goTo(NotesListActivity::class.java)
            finish()
        }
    }

    private fun setsUpRegisterText() {
        binding.textviewRegisterMessageLogin.setOnClickListener {
            goTo(RegisterUserActivity::class.java)
        }
    }
}