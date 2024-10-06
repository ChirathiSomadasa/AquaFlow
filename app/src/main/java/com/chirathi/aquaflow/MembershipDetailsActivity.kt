package com.chirathi.aquaflow

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AlertDialog
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
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
        next.setOnClickListener {
            val intent = Intent(this, MembershipDetailsActivity::class.java)
            startActivity(intent)
        }

        val back= findViewById<ImageView>(R.id.backbtn)
        back.setOnClickListener {
            finish()
        }


        val collectButton: Button = findViewById(R.id.collect_button)

        collectButton.setOnClickListener {
            // Create an AlertDialog.Builder
            val builder = AlertDialog.Builder(this)

            // Inflate the custom layout
            val inflater = layoutInflater
            val dialogView = inflater.inflate(R.layout.custome_dialog_congrats, null)

            // Set the custom layout to the dialog
            builder.setView(dialogView)

            // Create the dialog instance first
            val dialog: AlertDialog = builder.create()

            // Get the views from the custom layout if needed
            val dialogButton: Button = dialogView.findViewById(R.id.dialog_button)

            // Set an onClickListener to the button in the custom layout
            dialogButton.setOnClickListener {
                // Dismiss the dialog when button is clicked
                dialog.dismiss()  // Now you can call dismiss() on the dialog object

                val intent = Intent(this, PaymentActivity::class.java)
                startActivity(intent)
            }

            // Show the dialog
            dialog.show()
        }

        // Fetch user ID from shared preferences and water amount from Firestore
        getUserIdOnSharedPreferences()?.let { customerId ->
            fetchPoints(customerId)
        }


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

                    val availablePoints = calculateAvailabePoints(weeklyPoints)


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

    private fun calculateAvailabePoints(weeklyPoints: Long): Long {
        val minimumRequiredPoints = 5
        return if (weeklyPoints > minimumRequiredPoints) {
            weeklyPoints - minimumRequiredPoints
        } else {
            0  // User can't use any points if they have less than 5
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
            else -> 0                   // Fallback in case of unexpected input
        }
    }

    // Retrieve user ID from shared preferences
    private fun getUserIdOnSharedPreferences(): String? {
        val sharedPreferences = getSharedPreferences("AquaFlow", MODE_PRIVATE)
        return sharedPreferences.getString("userId", "")
    }


}