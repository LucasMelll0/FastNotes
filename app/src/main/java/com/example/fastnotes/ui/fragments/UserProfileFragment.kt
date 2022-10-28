package com.example.fastnotes.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.fastnotes.R
import com.example.fastnotes.databinding.ConfirmationChangePasswordBottomSheetBinding
import com.example.fastnotes.databinding.ConfirmationUserDeleteBottomSheetBinding
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
        setsUpButtonChangePassword()
        setsUpButtonDeleteUser()

    }

    private fun setsUpButtonChangePassword() {
        binding.buttonChangePasswordUserProfile.setOnClickListener {
            repository.getUser()?.let {
                setsUpBottomSheetChangePassword()
            }
        }
    }

    private fun setsUpBottomSheetChangePassword() {
        val dialog = BottomSheetDialog(requireContext(), R.style.BottomSheetDialog)
        val view = ConfirmationChangePasswordBottomSheetBinding.inflate(layoutInflater, null, false)
        view.apply {
            buttonConfirmationChangePasswordBottomSheet.setOnClickListener {
                if (samePasswords(view) && fieldsNotEmpty(view)) {
                    val oldPassword = edittextOldPasswordChangePassword.editText!!.text.toString()
                    val newPassword = edittextConfirmPasswordChangePassword.editText!!.text.toString()
                    repository.authenticatesForChangePassWord(oldPassword, newPassword)
                }
            }
        }
        dialog.setCancelable(true)
        dialog.setContentView(view.root)
        dialog.show()
    }

    private fun fieldsNotEmpty(view: ConfirmationChangePasswordBottomSheetBinding): Boolean {
        return if (view.edittextOldPasswordChangePassword.editText?.text.toString().isEmpty() ||
                view.edittextNewPasswordChangePassword.editText?.text.toString().isEmpty() ||
                view.edittextConfirmPasswordChangePassword.editText?.text.toString().isEmpty()) {
            Toast.makeText(
                requireContext(),
                getText(R.string.error_empty_field),
                Toast.LENGTH_LONG
            ).show()
            false
        } else {
            true
        }
    }

    private fun samePasswords(
        view: ConfirmationChangePasswordBottomSheetBinding
    ): Boolean {
        view.apply {
            val password = edittextNewPasswordChangePassword.editText?.text.toString()
            val passwordRepeated = edittextConfirmPasswordChangePassword.editText?.text.toString()

            return if (password != passwordRepeated) {
                Toast.makeText(
                    requireContext(),
                    getText(R.string.error_password_not_same),
                    Toast.LENGTH_SHORT
                ).show()
                false
            } else {
                true
            }
        }
    }

    private fun setsUpButtonDeleteUser() {
        binding.buttonDeleteUserProfile.setOnClickListener {
            val user = repository.getUser()
            user?.let {
                setsUpBottomSheetDeleteUser()
            }
        }
    }

    private fun setsUpBottomSheetDeleteUser() {
        val dialog = BottomSheetDialog(requireContext(), R.style.BottomSheetDialog)
        val view = ConfirmationUserDeleteBottomSheetBinding.inflate(layoutInflater, null, false)
        view.apply {
            buttonConfirmationDeleteUserBottomSheet.setOnClickListener {
                val password = edittextConfirmPasswordDeleteUser.editText?.text.toString()
                if (password.isNotEmpty()) {
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