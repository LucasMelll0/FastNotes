package com.example.fastnotes.ui.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.example.fastnotes.R
import com.example.fastnotes.databinding.ActivityNotesBinding
import com.example.fastnotes.repositories.UserRepository
import com.example.fastnotes.ui.activities.extensions.goTo
import com.example.fastnotes.ui.fragments.AllNotesFragment
import com.example.fastnotes.ui.fragments.MyNotesFragment
import com.example.fastnotes.ui.viewpager.ViewPagerAdapter
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator

class NotesListActivity : AppCompatActivity() {

    private val binding by lazy { ActivityNotesBinding.inflate(layoutInflater) }
    private val repository by lazy { UserRepository(this) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        setSupportActionBar(binding.toolbarListActivity)
        title = ""
        setsUpToolbar()
        setsUpTabLayout()
    }

    private fun setsUpToolbar() {
        setsUpDisconnectButton()
        setsUpProfileButton()
    }

    private fun setsUpProfileButton() {
        binding.imagebuttonProfileUser.setOnClickListener {
            Toast.makeText(this, "User Profile", Toast.LENGTH_SHORT).show()
        }
    }

    private fun setsUpDisconnectButton() {
        binding.imagebuttonDisconnectUser.setOnClickListener {
            repository.disconnect()
            goTo(LoginUserActivity::class.java)
            finish()
        }
    }

    private fun setsUpTabLayout() {
        val adapter = ViewPagerAdapter(this)
        binding.apply {
            viewpager.adapter = adapter
            adapter.addFragment(MyNotesFragment(), getString(R.string.my_notes))
            adapter.addFragment(AllNotesFragment(), getString(R.string.all_notes))
            viewpager.offscreenPageLimit = adapter.itemCount
            val mediator =
                TabLayoutMediator(tablayout, viewpager) { tab: TabLayout.Tab, position: Int ->
                    tab.text = adapter.getTitle(position)
                }
            mediator.attach()
        }
    }
}