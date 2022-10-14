package com.example.fastnotes.ui.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.navigation.fragment.NavHostFragment
import com.example.fastnotes.R
import com.example.fastnotes.databinding.ActivityMainBinding

class MainActivity : BaseActivity() {


    private val binding by lazy { ActivityMainBinding.inflate(layoutInflater) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        supportFragmentManager.findFragmentById(binding.navHostFragment.id) as NavHostFragment


    }
}