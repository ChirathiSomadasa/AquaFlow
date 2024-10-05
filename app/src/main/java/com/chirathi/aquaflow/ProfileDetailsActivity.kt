package com.chirathi.aquaflow

import android.os.Bundle
import android.util.Log
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class ProfileDetailsActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private val db = Firebase.firestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile_details)

        auth = FirebaseAuth.getInstance()

        // Reference to profile name and email TextViews
        var profileName = findViewById<TextView>(R.id.profileName) // Add this
        var FName = findViewById<TextView>(R.id.fName) // Add this
        var LName = findViewById<TextView>(R.id.lName) // Add this
        val profileAddress = findViewById<TextView>(R.id.prof_address)  // Address (location)
        val profileEmail = findViewById<TextView>(R.id.prof_email)  // Email

        // Fetch current user data from Firebase
        val currentUser = auth.currentUser
        if (currentUser != null) {
            val userId = currentUser.uid

            // Fetch user data from Firestore
            db.collection("users").document(userId)
                .get()
                .addOnSuccessListener { document ->
                    if (document != null) {
                        // Set user data to UI
                        val firstName = document.getString("firstName") ?: ""
                        val lastName = document.getString("lastName") ?: ""
                        val location = document.getString("address") ?: ""  // Assuming location refers to address
                        val email = document.getString("email") ?: ""

                        // Concatenate first and last names and display them
                        profileName.text = "$firstName $lastName"
                        FName.text = firstName
                        LName.text = lastName
                        profileAddress.text = location
                        profileEmail.text = email

                    }
                }
                .addOnFailureListener { exception ->
                    // Handle errors
                    Log.e("ProfileDetailsActivity", "Error fetching user data", exception)
                }
        }
    }
}