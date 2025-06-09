package com.example.delta

import android.content.Context
import android.util.Log
import android.widget.Toast
import androidx.compose.runtime.*
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class AuthViewModel : ViewModel() {

    private val auth = FirebaseAuth.getInstance()
    private val firestore = FirebaseFirestore.getInstance()

    var user by mutableStateOf(auth.currentUser)
        private set

    var errorMessage by mutableStateOf<String?>(null)

    fun login(email: String, password: String, onSuccess: () -> Unit) {
        errorMessage = null
        auth.signInWithEmailAndPassword(email, password)
            .addOnSuccessListener {
                user = auth.currentUser
                onSuccess()
            }
            .addOnFailureListener {
                Log.e("AuthViewModel", "Login failed", it)
                errorMessage = it.localizedMessage ?: "An unknown error occurred"
            }
    }

    fun register(
        email: String,
        password: String,
        firstName: String,
        lastName: String,
        onSuccess: () -> Unit
    ) {
        errorMessage = null
        auth.createUserWithEmailAndPassword(email, password)
            .addOnSuccessListener {
                user = auth.currentUser
                saveUserProfile(firstName, lastName, email, onSuccess)
            }
            .addOnFailureListener {
                Log.e("AuthViewModel", "Registration failed", it)
                errorMessage = it.localizedMessage ?: "An unknown error occurred"
            }
    }

    private fun saveUserProfile(
        firstName: String,
        lastName: String,
        email: String,
        onSuccess: () -> Unit
    ) {
        val uid = user?.uid ?: return
        val userData = mapOf(
            "firstName" to firstName,
            "lastName" to lastName,
            "email" to email
        )

        firestore.collection("users")
            .document(uid)
            .set(userData)
            .addOnSuccessListener {
                Log.d("AuthViewModel", "User profile saved")
                onSuccess()
            }
            .addOnFailureListener {
                Log.e("AuthViewModel", "Failed to save user profile", it)
                errorMessage = "Registered, but failed to save user data."
            }
    }

    fun logoutUser(context: Context, onLoggedOut: () -> Unit) {
        FirebaseAuth.getInstance().signOut()
        Toast.makeText(context, "Logged out successfully", Toast.LENGTH_SHORT).show()
        onLoggedOut() // Callback to navigate
    }

}
