package com.example.delta

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.util.Base64
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.SetOptions
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.io.ByteArrayOutputStream
import java.io.InputStream

data class UserProfile(
    val name: String,
    val photoBase64: String? ,// Store Base64 string here instead of URL
    val firstName: String = ""
)

class ProfileViewModel : ViewModel() {

    private val firestore = Firebase.firestore
    private val auth = FirebaseAuth.getInstance()

    private val _userProfile = MutableStateFlow<UserProfile?>(null)
    val userProfile: StateFlow<UserProfile?> = _userProfile.asStateFlow()

    init {
        loadUserProfile()
    }

    private fun loadUserProfile() {
        val uid = auth.currentUser?.uid ?: return
        firestore.collection("users").document(uid)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    Log.e("ProfileViewModel", "Listen failed.", error)
                    return@addSnapshotListener
                }
                if (snapshot != null && snapshot.exists()) {
                    val firstName = snapshot.getString("firstName") ?: ""
                    val lastName = snapshot.getString("lastName") ?: ""
                    val name = (firstName + " " + lastName).trim().ifEmpty { "No Name" }
                    val photoBase64 = snapshot.getString("photoBase64")
                    _userProfile.value = UserProfile(name, photoBase64, firstName)
                }
            }
    }

    fun uploadProfilePhoto(context: Context, uri: Uri) {
        val uid = auth.currentUser?.uid

        if (uid == null) {
            Toast.makeText(context, "User not logged in", Toast.LENGTH_SHORT).show()
            return
        }

        viewModelScope.launch(Dispatchers.IO) {
            try {
                val base64String = uriToBase64(context, uri)
                Log.d("ProfileViewModel", "Base64 string length: ${base64String.length}")

                Firebase.firestore.collection("users").document(uid)
                    .set(mapOf("photoBase64" to base64String), SetOptions.merge())
                    .addOnSuccessListener {
                        Log.d("ProfileViewModel", "Successfully uploaded photoBase64")
                        Toast.makeText(context, "Profile photo updated", Toast.LENGTH_SHORT).show()
                    }
                    .addOnFailureListener { e ->
                        Log.e("ProfileViewModel", "Firestore update failed", e)
                        Toast.makeText(context, "Failed to update photo", Toast.LENGTH_SHORT).show()
                    }

            } catch (e: Exception) {
                Log.e("ProfileViewModel", "Failed to convert image", e)
                Toast.makeText(context, "Failed to upload photo", Toast.LENGTH_SHORT).show()
            }
        }
    }


    private fun uriToBase64(context: Context, uri: Uri): String {
        val inputStream: InputStream? = context.contentResolver.openInputStream(uri)
        val bitmap = BitmapFactory.decodeStream(inputStream)
        inputStream?.close()

        val outputStream = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.JPEG, 40, outputStream) // compress to reduce size
        val compressedBytes = outputStream.toByteArray()
        outputStream.close()

        return Base64.encodeToString(compressedBytes, Base64.DEFAULT)
    }

}
