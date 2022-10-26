package com.example.fastnotes.ui.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.fastnotes.R
import com.example.fastnotes.databinding.ConfirmUserDeleteBottomSheetBinding
import com.example.fastnotes.databinding.FragmentUserProfileBinding
import com.example.fastnotes.repositories.UserRepository
import com.google.android.material.bottomsheet.BottomSheetDialog


class UserProfileFragment : Fragment() {

    private var _binding: FragmentUserProfileBinding? = null
    private val binding get() = _binding!!
    private val repository by lazy { UserRepository(this) }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentUserProfileBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setsUpUserName()
        setsUpButtonDeleteUser()

    }

    private fun setsUpButtonDeleteUser() {
        binding.buttonDeleteUserProfile.setOnClickListener {
            val user = repository.getUser()
            user?.let {
                setsUpBottomSheet()
            }
        }
    }

    private fun setsUpBottomSheet() {
        val dialog = BottomSheetDialog(requireContext(), R.style.BottomSheetDialog)
        val view = ConfirmUserDeleteBottomSheetBinding.inflate(layoutInflater, null, false)
        view.apply {
            buttonConfirmationDeleteUserBottomSheet.setOnClickListener {
                val password = edittextConfirmPasswordDeleteUser.editText?.text.toString()
                if (password.isNotEmpty()){
                    repository.authenticatesForDelete(password)
                    dialog.dismiss()
                }
            }
        }
        dialog.setCancelable(true)
        dialog.setContentView(view.root)
        dialog.show()
    }

    private fun setsUpUserName() {
        val user = repository.getUser()
        val name = user?.displayName
        name?.let {
            binding.textviewUserNameProfile.text = it
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

}