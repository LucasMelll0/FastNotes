package com.example.fastnotes.repositories

import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.fastnotes.R
import com.example.fastnotes.model.User
import com.example.fastnotes.ui.activities.LoginUserActivity
import com.example.fastnotes.ui.activities.NotesListActivity
import com.example.fastnotes.ui.activities.extensions.goTo
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.ktx.userProfileChangeRequest

class UserRepository(private val activity: AppCompatActivity) {

    private val firebaseAuth = FirebaseAuth.getInstance()

    fun connect(user: User) {
        firebaseAuth.signInWithEmailAndPassword(
            user.email,
            user.password
        ).addOnCompleteListener {
            if (it.isSuccessful) {
                activity.goTo(NotesListActivity::class.java)
                activity.finish()
            } else {
                Toast.makeText(
                    activity,
                    activity.getString(R.string.error_on_login),
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }


    fun register(user: User) {
        firebaseAuth.createUserWithEmailAndPassword(
            user.email,
            user.password
        ).addOnCompleteListener {
            if (it.isSuccessful) {
                changeUserNameOnFirebase(user.name!!)
                Toast.makeText(
                    activity,
                    activity.getString(R.string.success_message_for_register_user),
                    Toast.LENGTH_SHORT
                ).show()
                activity.goTo(LoginUserActivity::class.java)
                activity.finish()

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
                    activity,
                    activity.getString(R.string.error_lenght_password),
                    Toast.LENGTH_SHORT
                ).show()
            }
            (error.contains("address is badly")) -> {
                Toast.makeText(
                    activity,
                    activity.getString(R.string.error_invalid_email),
                    Toast.LENGTH_SHORT
                ).show()
            }
            (error.contains("address is already")) -> {
                Toast.makeText(
                    activity,
                    activity.getString(R.string.error_already_used_email),
                    Toast.LENGTH_SHORT
                ).show()
            }
            else -> {
                Toast.makeText(
                    activity,
                    activity.getString(R.string.error_message_for_register_user),
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