package com.example.fastnotes.ui.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.fastnotes.databinding.ActivityLoginUserBinding
import com.example.fastnotes.ui.activities.extensions.goTo

class LoginUserActivity : AppCompatActivity() {

    private val binding by lazy { ActivityLoginUserBinding.inflate(layoutInflater) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        setsUpFab()
        setsUpRegisterText()
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
            finish()
        }
    }
}