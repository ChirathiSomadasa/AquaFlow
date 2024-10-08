package com.chirathi.aquaflow

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.widget.Button
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.progressindicator.CircularProgressIndicator
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class MembershipActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private val db = Firebase.firestore
    private lateinit var progressBar: CircularProgressIndicator
    private lateinit var progressText: TextView
    private val handler = Handler(Looper.getMainLooper())
    private var progressStatus = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_membership)

        auth = FirebaseAuth.getInstance()

        val next = findViewById<Button>(R.id.continue_button)
        next.setOnClickListener {
            val intent = Intent(this, MembershipDetailsActivity::class.java)
            startActivity(intent)
        }

        val back = findViewById<ImageView>(R.id.backbtn)
        back.setOnClickListener {
            finish()
        }

        // Initialize UI elements
        progressBar = findViewById(R.id.progressBar)
        progressText = findViewById(R.id.progress_text)

        // Fetch user ID from shared preferences and water amount from Firestore
        getUserIdOnSharedPreferences()?.let { customerId ->
            fetchWaterAmount(customerId)
        }
    }

    // Fetch water amount and calculate points
    private fun fetchWaterAmount(customerId: String) {
        db.collection("water").document(customerId)
            .get()
            .addOnSuccessListener { document ->
                if (document != null) {
                    val currentWaterAmount = document.getString("waterAmount") ?: "0"
                    val waterAmount = currentWaterAmount.toIntOrNull() ?: 0

                    // Calculate points based on water usage (max 100 points if waterAmount >= 1000L)
                    val points = if (waterAmount < 1000) {
                        waterAmount / 10  // 1 point for every 10L
                    } else {
                        0
                    }
                    // Display the points in the progressText
                    progressText.text = points.toString()

                    // Save the points and customerId in the "points" collection
                    savePoints(customerId, points)

                    // Start updating the progress bar
                    startProgressBar(points)

                } else {
                    Log.e("Firestore", "No water data found for this customer")
                }
            }
            .addOnFailureListener { exception ->
                Log.e("Firestore", "Error fetching water data: ${exception.message}")
            }
    }

    // Function to save points in Firestore
    private fun savePoints(customerId: String, points: Int) {
        // Create a data object to save the points
        val pointsData = hashMapOf(
            "customerId" to customerId,
            "points" to points,
            "timestamp" to System.currentTimeMillis()  // Optional: add a timestamp for when the points were saved
        )

        // Save the points in the "points" collection using customerId as the document ID
        db.collection("points").document(customerId)
            .set(pointsData)
            .addOnSuccessListener {
                Log.d("Firestore", "Points successfully saved for customerId: $customerId")
            }
            .addOnFailureListener { exception ->
                Log.e("Firestore", "Error saving points: ${exception.message}")
            }
    }

    // Dynamically update the progress bar based on points
    private fun startProgressBar(points: Int) {
        // Ensure the progress bar does not exceed the maximum points (100)
        progressBar.max = 100  // Set the maximum value for the progress bar
        progressStatus = 0  // Initialize the progress status

        // Update the progress bar dynamically based on the points
        Thread {
            while (progressStatus < points) {  // Continue until progress reaches the calculated points
                progressStatus += 1
                handler.post {
                    progressBar.progress = progressStatus
                    progressText.text = "$progressStatus"
                }
                try {
                    // Sleep for 50ms to create a smooth transition (adjust as needed)
                    Thread.sleep(50)
                } catch (e: InterruptedException) {
                    e.printStackTrace()
                }
            }
        }.start()
    }

    // Retrieve user ID from shared preferences
    private fun getUserIdOnSharedPreferences(): String? {
        val sharedPreferences = getSharedPreferences("AquaFlow", MODE_PRIVATE)
        return sharedPreferences.getString("userId", "")
    }
}
