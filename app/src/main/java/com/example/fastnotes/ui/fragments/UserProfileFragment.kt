package com.example.fastnotes.ui.fragments

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.fastnotes.R
import com.example.fastnotes.databinding.ConfirmationChangePasswordBottomSheetBinding
import com.example.fastnotes.databinding.ConfirmationUserDeleteBottomSheetBinding
import com.example.fastnotes.databinding.FragmentUserProfileBinding
import com.example.fastnotes.repositories.FIREBASE_AUTH_TEST
import com.example.fastnotes.repositories.UserRepository
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.ktx.userProfileChangeRequest


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
        setsUpChangeNameButton()
        setsUpButtonChangePassword()
        setsUpButtonDeleteUser()

    }

    private fun setsUpChangeNameButton() {
        binding.apply {
            imagebuttonEditUserName.setOnClickListener {
                val oldUserName = textviewUserNameProfile.text
                if(edittextUserNameProfile.visibility == View.VISIBLE){
                    val newUserName = edittextUserNameProfile.text.toString()
                    if(newUserName != oldUserName.toString()){
                        tryChangeUserName(newUserName)
                    }else{
                        changeUserNameMode()
                    }
                }else{
                    changeUserNameMode()
                    setsUserNameEditText(oldUserName)
                    setsUpCancelChangeUserNameButton()
                }
            }
        }
    }

    private fun setsUpCancelChangeUserNameButton() {
        binding.imagebuttonCancelEditUserName.setOnClickListener {
            changeUserNameMode()
        }
    }

    private fun tryChangeUserName(newUserName: String) {
        repository.getUser()?.let { user ->
            val nameChangeRequest = userProfileChangeRequest {
                displayName = newUserName
            }
            user.updateProfile(nameChangeRequest)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful){
                        changeUserNameMode()
                    }else{
                        Log.e(FIREBASE_AUTH_TEST, "tryChangeUserName: ", task.exception)
                        Snackbar.make(
                            requireView(),
                            getString(R.string.error_change_username),
                            Snackbar.LENGTH_LONG
                        ).show()
                    }
                }
                .addOnCanceledListener {
                    Snackbar.make(
                        requireView(),
                        getString(R.string.error_change_username),
                        Snackbar.LENGTH_LONG
                    ).show()
                }
        }
    }

    private fun changeUserNameMode() {
        changeUserNameTextViewVisibility()
        changeUserNameEditTextVisibility()
    }

    private fun setsUserNameEditText(oldName: CharSequence?) {
        binding.edittextUserNameProfile.setText(oldName)
    }

    private fun changeUserNameEditTextVisibility() {
        binding.apply {
            when(edittextUserNameProfile.visibility){

                View.GONE -> {
                    edittextUserNameProfile.visibility = View.VISIBLE
                    imagebuttonEditUserName.setImageResource(R.drawable.ic_check)
                    imagebuttonCancelEditUserName.visibility = View.VISIBLE
                }
                View.INVISIBLE -> {
                    edittextUserNameProfile.visibility = View.VISIBLE
                    imagebuttonEditUserName.setImageResource(R.drawable.ic_check)
                    imagebuttonCancelEditUserName.visibility = View.VISIBLE
                }
                View.VISIBLE -> {
                    edittextUserNameProfile.visibility = View.GONE

                }
            }

        }
    }

    private fun changeUserNameTextViewVisibility() {
        binding.apply {
            when(textviewUserNameProfile.visibility){
                View.GONE -> {
                    textviewUserNameProfile.visibility = View.VISIBLE
                    imagebuttonCancelEditUserName.visibility = View.GONE
                    imagebuttonEditUserName.setImageResource(R.drawable.ic_edit)
                }
                View.VISIBLE -> {
                    textviewUserNameProfile.visibility = View.INVISIBLE

                }
                View.INVISIBLE -> {
                    textviewUserNameProfile.visibility = View.VISIBLE
                    imagebuttonCancelEditUserName.visibility = View.GONE
                    imagebuttonEditUserName.setImageResource(R.drawable.ic_edit)
                    setsUpUserName()
                }
            }
        }
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
                    if (oldPassword != newPassword){
                        repository.authenticatesForChangePassWord(oldPassword, newPassword)
                    }else{
                        Toast.makeText(
                            requireContext(),
                            getString(R.string.passwords_are_same),
                            Toast.LENGTH_LONG
                        ).show()
                    }
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
            view.edittextConfirmPasswordChangePassword.editText?.text.toString().isEmpty()
        ) {
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
        user?.let {
            val name = user.displayName
            binding.textviewUserNameProfile.text = name

        }
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

}