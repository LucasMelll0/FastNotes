package com.example.fastnotes.repositories

import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.fastnotes.R
import com.example.fastnotes.model.User
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.ktx.userProfileChangeRequest

class UserRepository(private val fragment: Fragment) {

    private val firebaseAuth = FirebaseAuth.getInstance()

    fun connect(user: User) {
        firebaseAuth.signInWithEmailAndPassword(
            user.email,
            user.password
        ).addOnCompleteListener {
            if (it.isSuccessful) {
                goTo(R.id.action_loginFragment_to_notesListFragment)
            } else {
                Snackbar.make(
                    fragment.requireView(),
                    fragment.getString(R.string.error_on_login),
                    Snackbar.LENGTH_SHORT
                ).show()
            }
        }
    }
    private fun goTo(destination: Int){
        fragment
            .findNavController()
            .navigate(destination)
    }


    fun register(user: User) {
        firebaseAuth.createUserWithEmailAndPassword(
            user.email,
            user.password
        ).addOnCompleteListener {
            if (it.isSuccessful) {
                changeUserNameOnFirebase(user.name!!)

                Toast.makeText(
                    fragment.requireContext(),
                    fragment.getString(R.string.success_message_for_register_user),
                    Toast.LENGTH_SHORT
                ).show()
                goTo(R.id.action_registerFragment_to_loginFragment)

            } else {
                val error = it.exception.toString()
                handdlesErrorForRegister(error)
            }

        }


    }

    fun disconnect() {
        firebaseAuth.signOut()
    }

    private fun handdlesErrorForRegister(error: String) {
        when {
            (error.contains("least 6 characters")) -> {
                Snackbar.make(
                    fragment.requireView(),
                    fragment.getString(R.string.error_lenght_password),
                    Snackbar.LENGTH_SHORT
                ).show()
            }
            (error.contains("address is badly")) -> {
                Toast.makeText(
                    fragment.requireContext(),
                    fragment.getString(R.string.error_invalid_email),
                    Toast.LENGTH_SHORT
                ).show()
            }
            (error.contains("address is already")) -> {
                Snackbar.make(
                    fragment.requireView(),
                    fragment.getString(R.string.error_already_used_email),
                    Snackbar.LENGTH_SHORT
                ).show()
            }
            else -> {
                Snackbar.make(
                    fragment.requireView(),
                    fragment.getString(R.string.error_message_for_register_user),
                    Snackbar.LENGTH_SHORT
                ).show()
            }
        }
    }

    private fun changeUserNameOnFirebase(name: String) {
        firebaseAuth.currentUser?.let {
            val nameChangeRequest = userProfileChangeRequest {
                displayName = name
            }
            it.updateProfile(nameChangeRequest)
        }
    }

    fun getUser(): FirebaseUser? = firebaseAuth.currentUser


}