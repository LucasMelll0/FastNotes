package com.example.fastnotes.repositories

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.util.Log
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.example.fastnotes.R
import com.example.fastnotes.extensions.goTo
import com.example.fastnotes.model.User
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.EmailAuthProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.ktx.userProfileChangeRequest
import kotlinx.coroutines.launch

const val FIREBASE_AUTH_TEST = "FireBaseAuth Test"
class UserRepository(
    private val fragment: Fragment,
    private val firebaseAuth: FirebaseAuth,
    private val noteRepository: NoteRepository) {

    var connected: Boolean = false

    companion object {
        fun getUser(): FirebaseUser? = FirebaseAuth.getInstance().currentUser
    }


    private fun checkConnection(): Boolean {
        val context = fragment.requireContext()
        val connectivityManager =
            context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

        val network = connectivityManager.activeNetwork ?: return false

        val activeNetwork = connectivityManager.getNetworkCapabilities(network) ?: return false

        return when {
            activeNetwork.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> {
                true
            }
            activeNetwork.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> {
                true
            }
            else -> {
                false
            }
        }
    }


    fun connect(user: User) {
        connected = checkConnection()
        if (connected){
            firebaseAuth.signInWithEmailAndPassword(
                user.email,
                user.password
            ).addOnCompleteListener {
                if (it.isSuccessful) {
                    fragment.goTo(R.id.action_loginFragment_to_notesListFragment)
                } else {
                    Snackbar.make(
                        fragment.requireView(),
                        fragment.getString(R.string.error_on_login),
                        Snackbar.LENGTH_SHORT
                    ).show()
                }
            }
        }else{
            Snackbar.make(
                fragment.requireView(),
                fragment.getString(R.string.offline_message),
                Snackbar.LENGTH_SHORT
            ).show()
        }
    }

    fun register(user: User) {
        connected = checkConnection()
        if (connected){
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
                    fragment.goTo(R.id.action_registerFragment_to_loginFragment)

                } else {
                    val error = it.exception.toString()
                    handdlesErrorForRegister(error)
                }

            }
        }else{
            Snackbar.make(
                fragment.requireView(),
                fragment.getString(R.string.offline_message),
                Snackbar.LENGTH_SHORT
            ).show()
        }


    }

    fun disconnect() {
        connected = checkConnection()
        if (connected){
            firebaseAuth.signOut()
        }
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

    private fun deleteUser() {
        connected = checkConnection()
        if (connected){
            getUser()?.let { user ->
                user
                    .delete()
                    .addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            fragment.lifecycleScope.launch {
                                noteRepository.removeAllUserNotes(user.uid)
                            }
                            fragment.findNavController()
                                .navigate(R.id.action_userProfileFragment_to_loginFragment)
                        } else {
                            Log.e(FIREBASE_AUTH_TEST, "deleteUser: ", task.exception)
                            Toast.makeText(
                                fragment.requireContext(),
                                fragment.getString(R.string.error_delete_user),
                                Toast.LENGTH_LONG
                            ).show()
                        }

                    }.addOnCanceledListener {
                        Log.i(FIREBASE_AUTH_TEST, "deleteUser: onCanceledListener")
                        Toast.makeText(
                            fragment.requireContext(),
                            fragment.getString(R.string.error_delete_user),
                            Toast.LENGTH_LONG
                        ).show()
                    }
            }
        }else{
            Toast.makeText(
                fragment.requireContext(),
                fragment.getString(R.string.offline_message),
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    fun authenticatesForDelete(password: String) {
        connected = checkConnection()
        if (connected){
            getUser()?.let { user ->
                val email = user.email!!
                val credential = EmailAuthProvider
                    .getCredential(email, password)
                user.reauthenticate(credential)
                    .addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            deleteUser()
                        } else {
                            Log.e(FIREBASE_AUTH_TEST, "reauthenticatesForDelete: ", task.exception)
                            Toast.makeText(
                                fragment.requireContext(),
                                fragment.getString(R.string.check_the_password_and_try_again),
                                Toast.LENGTH_LONG
                            ).show()
                        }
                    }.addOnCanceledListener {
                        Toast.makeText(
                            fragment.requireContext(),
                            fragment.getString(R.string.error_delete_user),
                            Toast.LENGTH_LONG
                        ).show()
                    }
            }
        }else{
            Snackbar.make(
                fragment.requireView(),
                fragment.getString(R.string.offline_message),
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    fun authenticatesForChangePassWord(oldPassword: String, newPassword: String) {
        connected = checkConnection()
        if (connected){
            getUser()?.let { user ->
                val email = user.email!!
                val credentials = EmailAuthProvider
                    .getCredential(email, oldPassword)
                user.reauthenticate(credentials)
                    .addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            changePassword(newPassword)
                        } else {
                            Log.e(
                                FIREBASE_AUTH_TEST,
                                "authenticatesForChangePassWord: ",
                                task.exception
                            )
                            Toast.makeText(
                                fragment.requireContext(),
                                fragment.getString(R.string.check_the_password_and_try_again),
                                Toast.LENGTH_LONG
                            ).show()
                        }
                    }.addOnCanceledListener {
                        Toast.makeText(
                            fragment.requireContext(),
                            fragment.getString(R.string.error_on_change_password),
                            Toast.LENGTH_LONG
                        ).show()
                    }

            }
        }else{
            Snackbar.make(
                fragment.requireView(),
                fragment.getString(R.string.offline_message),
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    private fun changePassword(newPassword: String) {
        connected = checkConnection()
        if (connected){
            getUser()?.let { user ->
                user
                    .updatePassword(newPassword)
                    .addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            Toast.makeText(
                                fragment.requireContext(),
                                fragment.getString(R.string.success_change_password),
                                Toast.LENGTH_SHORT
                            ).show()
                        } else {
                            Log.e(FIREBASE_AUTH_TEST, "changePassword: ", task.exception)
                            Toast.makeText(
                                fragment.requireContext(),
                                fragment.getString(R.string.error_on_change_password),
                                Toast.LENGTH_LONG
                            ).show()
                        }
                    }.addOnCanceledListener {
                        Toast.makeText(
                            fragment.requireContext(),
                            fragment.getString(R.string.error_on_change_password),
                            Toast.LENGTH_LONG
                        ).show()
                    }
            }

        }else{
            Snackbar.make(
                fragment.requireView(),
                fragment.getString(R.string.offline_message),
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    fun sendPasswordResetEmail(email: String) {
        connected = checkConnection()
        if (connected){
            firebaseAuth
                .sendPasswordResetEmail(email)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        Toast.makeText(
                            fragment.requireContext(),
                            fragment.getString(R.string.email_reset_password_sent_successful),
                            Toast.LENGTH_LONG
                        ).show()
                    } else {
                        Log.e(FIREBASE_AUTH_TEST, "sendPasswordResetEmail: ", task.exception)
                        Toast.makeText(
                            fragment.requireContext(),
                            fragment.getString(R.string.default_error_message),
                            Toast.LENGTH_LONG
                        ).show()
                    }
                }
                .addOnCanceledListener {
                    Toast.makeText(
                        fragment.requireContext(),
                        fragment.getString(R.string.default_error_message),
                        Toast.LENGTH_LONG
                    ).show()
                }
        }else{
            Snackbar.make(
                fragment.requireView(),
                fragment.getString(R.string.offline_message),
                Toast.LENGTH_SHORT
            ).show()
        }
    }



}

