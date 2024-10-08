package com.chirathi.aquaflow

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AlertDialog
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class MembershipDetailsActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private val db = Firebase.firestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_membership_details)

        auth = FirebaseAuth.getInstance()

        val next = findViewById<Button>(R.id.collect_button)

        val back = findViewById<ImageView>(R.id.backbtn)
        back.setOnClickListener {
            finish() // Go back to the previous activity
        }

        // Fetch user ID from shared preferences and get user details
        getUserIdOnSharedPreferences()?.let { customerId ->
            fetchUserName(customerId) // Fetch user's name
            fetchPoints(customerId) // Fetch user's points
        }

        // Collect button click listener for dialog
        next.setOnClickListener {
            showCongratulationsDialog()
        }
    }

    private fun fetchUserName(customerId: String) {
        val currentUser = auth.currentUser
        if (currentUser != null) {
            val userId = currentUser.uid

            db.collection("users").document(customerId)
                .get()
                .addOnSuccessListener { document ->
                    if (document != null) {
                        // Set user data to UI
                        val firstName = document.getString("firstName") ?: "User"
                        // Save the user's first name to shared preferences
                        saveUserNameToSharedPreferences(firstName)
                    }
                }
                .addOnFailureListener { exception ->
                    Log.e("MembershipDetailsActivity", "Error fetching user data", exception)
                }
        }
    }

    private fun showCongratulationsDialog() {
        val builder = AlertDialog.Builder(this)
        val inflater = layoutInflater
        val dialogView = inflater.inflate(R.layout.custome_dialog_congrats, null)

        builder.setView(dialogView)

        val dialog: AlertDialog = builder.create()
        val dialogButton: Button = dialogView.findViewById(R.id.dialog_button)
        val congratNameTextView: TextView = dialogView.findViewById(R.id.congrat_name)

        val userName = getUserNameFromSharedPreferences()
        congratNameTextView.text = userName ?: "User" // Default to "User" if name is null

        dialogButton.setOnClickListener {
            dialog.dismiss()

            // Navigate to PaymentActivity instead of restarting MembershipDetailsActivity
            val intent = Intent(this, PaymentActivity::class.java)
            startActivity(intent)
        }

        dialog.show()
    }
    private fun fetchPoints(customerId: String) {
        val weeklyPointsTextView = findViewById<TextView>(R.id.weekly_points_amount)
        val availablePointsTextView = findViewById<TextView>(R.id.available_points_amount)
        val discountTextView = findViewById<TextView>(R.id.discount_amount)

        db.collection("points").document(customerId)
            .get()
            .addOnSuccessListener { document ->
                if (document != null && document.exists()) {
                    // Retrieve the points data from Firestore
                    val weeklyPoints = document.getLong("points") ?: 0

                    val availablePoints = calculateAvailablePoints(weeklyPoints)

                    // Update the TextViews with fetched points
                    weeklyPointsTextView.text = weeklyPoints.toString()
                    availablePointsTextView.text = availablePoints.toString()

                    val discount = getDiscountPercentage(availablePoints)
                    discountTextView.text = "$discount%"
                } else {
                    Log.e("Firestore", "No points data found for this customer")
                }
            }
            .addOnFailureListener { exception ->
                Log.e("Firestore", "Error fetching points data: ${exception.message}")
            }
    }

    private fun calculateAvailablePoints(weeklyPoints: Long): Long {
        val minimumRequiredPoints = 5
        return if (weeklyPoints > minimumRequiredPoints) {
            weeklyPoints - minimumRequiredPoints
        } else {
            0 // User can't use any points if they have less than 5
        }
    }

    private fun getDiscountPercentage(availablePoints: Long): Int {
        return when {
            availablePoints < 20 -> 0           // 0 - 19 points: no discount
            availablePoints in 20..39 -> 2      // 20 - 39 points: 2% discount
            availablePoints in 40..59 -> 4      // 40 - 59 points: 4% discount
            availablePoints in 60..79 -> 6      // 60 - 79 points: 6% discount
            availablePoints in 80..99 -> 8      // 80 - 99 points: 8% discount
            availablePoints == 100L -> 10       // 100 points: 10% discount
            else -> 0 // Fallback in case of unexpected input
        }
    }

    // Save the user's name to SharedPreferences
    private fun saveUserNameToSharedPreferences(userName: String) {
        val sharedPreferences = getSharedPreferences("AquaFlow", MODE_PRIVATE)
        with(sharedPreferences.edit()) {
            putString("user_name_key", userName) // Replace with your key
            apply()
        }
    }

    // Retrieve user's name from SharedPreferences
    private fun getUserNameFromSharedPreferences(): String? {
        val sharedPreferences = getSharedPreferences("AquaFlow", MODE_PRIVATE)
        return sharedPreferences.getString("user_name_key", null) // Replace with your key
    }

    // Retrieve user ID from shared preferences
    private fun getUserIdOnSharedPreferences(): String? {
        val sharedPreferences = getSharedPreferences("AquaFlow", MODE_PRIVATE)
        return sharedPreferences.getString("userId", "")
    }
}
