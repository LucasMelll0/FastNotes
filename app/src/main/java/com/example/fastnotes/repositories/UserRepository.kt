package com.example.fastnotes.repositories

import android.content.Context
import android.widget.Toast
import com.example.fastnotes.R
import com.example.fastnotes.model.User
import com.example.fastnotes.ui.activities.LoginUserActivity
import com.example.fastnotes.ui.activities.NotesListActivity
import com.example.fastnotes.ui.activities.extensions.goTo
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.ktx.userProfileChangeRequest

class UserRepository(private val context: Context) {

    private val firebaseAuth = FirebaseAuth.getInstance()

    fun connect(user: User) {
        firebaseAuth.signInWithEmailAndPassword(
            user.email,
            user.password
        ).addOnCompleteListener { 
            if (it.isSuccessful){
                context.goTo(NotesListActivity::class.java)
            }else{
                Toast.makeText(context, "", Toast.LENGTH_SHORT).show()
            }
        }
    }

    fun register(user: User) {
        firebaseAuth.createUserWithEmailAndPassword(
            user.email,
            user.password
        ).addOnCompleteListener {
            if (it.isSuccessful) {
                changeUserNameOnFirebase(user.name)
                Toast.makeText(
                    context,
                    context.getString(R.string.success_message_for_register_user),
                    Toast.LENGTH_SHORT
                ).show()
                context.goTo(LoginUserActivity::class.java)

            } else {
                val error = it.exception.toString()
                handdlesErrorForRegister(error)
            }

        }


    }

    private fun handdlesErrorForRegister(error: String) {
        if (error.contains("least 6 characters")){
            Toast.makeText(
                context,
                context.getString(R.string.error_lenght_password),
                Toast.LENGTH_SHORT
            ).show()
        }else if (error.contains("address is badly")){
            Toast.makeText(
                context,
                context.getString(R.string.error_invalid_email),
                Toast.LENGTH_SHORT
            ).show()
        }else if(error.contains("address is already")){
            Toast.makeText(
                context,
                context.getString(R.string.error_already_used_email),
                Toast.LENGTH_SHORT
            ).show()
        }else{
            Toast.makeText(
                context,
                context.getString(R.string.error_message_for_register_user),
                Toast.LENGTH_SHORT
            ).show()
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