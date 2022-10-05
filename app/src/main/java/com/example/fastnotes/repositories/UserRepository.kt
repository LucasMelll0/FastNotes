package com.example.fastnotes.repositories

import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import com.example.fastnotes.R
import com.example.fastnotes.model.User
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
                Toast.makeText(
                    fragment.requireContext(),
                    fragment.getString(R.string.error_on_login),
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }
    fun goTo(destination: Int){
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
                Toast.makeText(
                    fragment.requireContext(),
                    fragment.getString(R.string.error_lenght_password),
                    Toast.LENGTH_SHORT
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
                Toast.makeText(
                    fragment.requireContext(),
                    fragment.getString(R.string.error_already_used_email),
                    Toast.LENGTH_SHORT
                ).show()
            }
            else -> {
                Toast.makeText(
                    fragment.requireContext(),
                    fragment.getString(R.string.error_message_for_register_user),
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }

    private fun changeUserNameOnFirebase(name: String) {
        val nameChangeRequest = userProfileChangeRequest {
            displayName = name
        }
        firebaseAuth.currentUser!!.updateProfile(nameChangeRequest)
    }

    fun getUser(): FirebaseUser? = firebaseAuth.currentUser


}