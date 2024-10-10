package com.chirathi.aquaflow

import android.app.Activity
import android.app.AlertDialog
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import de.hdodenhof.circleimageview.CircleImageView
import java.util.Date

class SupplierProfileDetailsActivity : AppCompatActivity() {

    private lateinit var profileImage: CircleImageView
    private lateinit var imageUploadIcon: ImageView
    private lateinit var ProfileName: TextView
    private lateinit var selectedImg: Uri
    private lateinit var dialog: AlertDialog

    private lateinit var fullNameTextView: TextView
    private lateinit var firstNameTextView: TextView
    private lateinit var lastNameTextView: TextView
    private lateinit var profileAddress: TextView
    private lateinit var profileEmail: TextView
    private lateinit var saveButton: Button
    private lateinit var auth: FirebaseAuth
    private lateinit var firestore: FirebaseFirestore
    private lateinit var storage: FirebaseStorage


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_supplier_profile_details)


        // Initialize Firebase services
        auth = FirebaseAuth.getInstance()
        firestore = FirebaseFirestore.getInstance()

        // Initialize UI components
        fullNameTextView = findViewById(R.id.profileName)
        firstNameTextView = findViewById(R.id.fName)
        lastNameTextView = findViewById(R.id.lName)
        profileAddress = findViewById(R.id.prof_address)  // Address (location)
        profileEmail = findViewById(R.id.prof_email)  // Email
        saveButton = findViewById(R.id.ok_button)

        profileImage = findViewById(R.id.profilePic)
//        imageUploadIcon = findViewById(R.id.image_upload_icon)
        ProfileName = findViewById(R.id.profileName)


        // Load user profile data from Firestore
        loadProfileData()
        saveButton.setOnClickListener { updateUserProfile() }


    }

    private fun loadProfileData() {
        val userId = auth.currentUser?.uid
        if (userId != null) {
            firestore.collection("users").document(userId)
                .get()
                .addOnSuccessListener { document ->
                    if (document != null && document.exists()) {
                        // Retrieve and display the user details
                        firstNameTextView.text = document.getString("firstName") ?: "N/A"
                        lastNameTextView.text = document.getString("lastName") ?: "N/A"
                        profileAddress.text = document.getString("address") ?: "N/A"
                        profileEmail.text = document.getString("email") ?: "N/A"

                        val firstName = document.getString("firstName") ?: "N/A"
                        val lastName = document.getString("lastName") ?: "N/A"
                        fullNameTextView.text = "$firstName $lastName"

                        // Load the profile image if available
                        val imageUrl = document.getString("profileImageUrl") ?: "N/A"
                        if (!imageUrl.isNullOrEmpty()) {
                            Glide.with(this)
                                .load(imageUrl)
                                .placeholder(R.drawable.user3) // Add a placeholder image
                                .error(R.drawable.user3) // Error image if loading fails
                                .into(profileImage)
                        }


                    } else {
                        Toast.makeText(this, "User profile not found", Toast.LENGTH_SHORT).show()
                    }
                }
                .addOnFailureListener {
                    Toast.makeText(this, "Failed to load profile data", Toast.LENGTH_SHORT).show()
                }

        }
    }

    private fun updateUserProfile() {

        val newFirstName = firstNameTextView.text.toString()
        val newLastName = lastNameTextView.text.toString()
        val newAddress = profileAddress.text.toString()
        val newEmail = profileEmail.text.toString()

        // Create a map with updated data
        val updatedData = hashMapOf(
            "firstName" to newFirstName,
            "lastName" to newLastName,
            "address" to newAddress,
            "email" to newEmail
        )

        val userId = auth.currentUser?.uid

        userId?.let {
            firestore.collection("users").document(userId)
                .update(updatedData as Map<String, Any>)
                .addOnSuccessListener {
                    Toast.makeText(this, "Profile successfully updated", Toast.LENGTH_SHORT).show()

                    // Disable input fields
                    firstNameTextView.isEnabled = false
                    lastNameTextView.isEnabled = false
                    profileAddress.isEnabled = false
                    profileEmail.isEnabled = false

                    // Reload updated data
                    loadProfileData()
                }
                .addOnFailureListener { e ->
                    Toast.makeText(this, "Error updating profile", Toast.LENGTH_SHORT).show()
                    Log.e("ProfileDetailsActivity", "Error updating profile", e)
                }

        }
    }


/*
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == 1000 && resultCode == Activity.RESULT_OK && data != null) {
            selectedImg = data.data!!

            // Set the selected image to the profileImage
            profileImage.setImageURI(selectedImg)

            // Upload the selected image to Firebase Storage
            uploadImageToFirebaseStorage()
        }
    }

    private fun uploadImageToFirebaseStorage() {
        // Show a dialog for uploading
        dialog = AlertDialog.Builder(this)  // Use `this` instead of `requireContext()`
            .setMessage("Uploading Profile Image...")
            .setCancelable(false)
            .create()
        dialog.show()

        // Create a reference for the image in Firebase Storage
        val reference = storage.reference.child("profileImages")
            .child(auth.currentUser?.uid + "_" + Date().time.toString())

        // Start the upload task
        reference.putFile(selectedImg)
            .addOnSuccessListener {
                // On success, get the download URL and save it
                reference.downloadUrl.addOnSuccessListener { uri ->
                    saveImageUrlToFirestore(uri.toString())
                }
            }
            .addOnFailureListener {
                dialog.dismiss()
                Toast.makeText(this, "Failed to upload image", Toast.LENGTH_SHORT).show()
            }
    }

    private fun saveImageUrlToFirestore(imageUrl: String) {
        val userId = auth.currentUser?.uid
        if (userId != null) {
            firestore.collection("users").document(userId)
                .update("profileImageUrl", imageUrl)
                .addOnSuccessListener {
                    dialog.dismiss()
                    Toast.makeText(this, "Profile Image Updated", Toast.LENGTH_SHORT).show()
                }
                .addOnFailureListener {
                    dialog.dismiss()
                    Toast.makeText(this, "Failed to update profile", Toast.LENGTH_SHORT).show()
                }
        }
    }
*/


}