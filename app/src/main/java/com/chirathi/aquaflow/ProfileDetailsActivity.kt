package com.chirathi.aquaflow

import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import de.hdodenhof.circleimageview.CircleImageView

class ProfileDetailsActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private val db = Firebase.firestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile_details)

        auth = FirebaseAuth.getInstance()

        // Reference to profile name and email TextViews
        val profileName = findViewById<TextView>(R.id.profileName) // Add this
        val fName = findViewById<TextView>(R.id.fName) // Add this
        val lName = findViewById<TextView>(R.id.lName) // Add this
        val profileAddress = findViewById<TextView>(R.id.prof_address)  // Address (location)
        val profileEmail = findViewById<TextView>(R.id.prof_email)  // Email
        val saveButton = findViewById<Button>(R.id.ok_button)
        val profileImage = findViewById<CircleImageView>(R.id.profilePic)

        // Fetch current user data from Firebase
        val currentUser = auth.currentUser
        if (currentUser != null) {
            val userId = currentUser.uid

            db.collection("users").document(userId)
                .get()
                .addOnSuccessListener { document ->
                    if (document != null) {
                        // Set user data to UI
                        val image = document.getString("profileImageUrl")?:""
                        val firstName = document.getString("firstName") ?: ""
                        val lastName = document.getString("lastName") ?: ""
                        val location = document.getString("address") ?: ""  // Assuming location refers to address
                        val email = document.getString("email") ?: ""

                        profileName.text = "$firstName $lastName"
                        fName.text = firstName
                        lName.text = lastName
                        profileAddress.text = location
                        profileEmail.text = email

                        if (image.isNotEmpty()) {
                            Glide.with(this)
                                .load(image)
                                .placeholder(R.drawable.user3) // Placeholder image while loading
                                .error(R.drawable.user3) // Error image if loading fails
                                .into(profileImage)
                        }

                    }
                }
                .addOnFailureListener { exception ->
                    // Handle errors
                    Log.e("ProfileDetailsActivity", "Error fetching user data", exception)
                }
        }

        saveButton.setOnClickListener {
            val newFirstName = fName.text.toString()
            val newLastName = lName.text.toString()
            val newAddress = profileAddress.text.toString()
            val newEmail = profileEmail.text.toString()

            // Create a map with updated data
            val updatedData = hashMapOf(
                "firstName" to newFirstName,
                "lastName" to newLastName,
                "address" to newAddress,
                "email" to newEmail
            )

            currentUser?.let {
                db.collection("users").document(it.uid)
                    .update(updatedData as Map<String, Any>)
                    .addOnSuccessListener {
                        Toast.makeText(this, "Profile successfully updated", Toast.LENGTH_SHORT).show()
                    }
                    .addOnFailureListener { e ->
                        Toast.makeText(this, "Error updating profile", Toast.LENGTH_SHORT).show()
                        Log.e("ProfileDetailsActivity", "Error updating profile", e)
                    }

            }
        }
    }
}