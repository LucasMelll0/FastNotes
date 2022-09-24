package com.example.fastnotes.ui.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.fastnotes.databinding.ActivityRegisterUserBinding
import com.example.fastnotes.ui.activities.extensions.goTo

class RegisterUserActivity : AppCompatActivity() {

    private val binding by lazy { ActivityRegisterUserBinding.inflate(layoutInflater) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        setsUpFab()
    }

    private fun setsUpFab() {
        binding.fabRegister.setOnClickListener {
            goTo(LoginUserActivity::class.java)
            finish()
        }
    }
}