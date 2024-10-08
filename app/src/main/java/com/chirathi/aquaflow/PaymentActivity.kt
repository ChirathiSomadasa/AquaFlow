package com.chirathi.aquaflow

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class PaymentActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private val db = Firebase.firestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_payment)

        auth = FirebaseAuth.getInstance()

        // Back button to return to the previous activity
        val back = findViewById<ImageView>(R.id.backbtn)
        back.setOnClickListener {
            finish() // End this activity and return to the previous screen
        }

        // Button to go to HomeActivity
        val next = findViewById<Button>(R.id.backhome_btn)
        next.setOnClickListener {
            val intent = Intent(this, HomeActivity::class.java)
            startActivity(intent)
        }

        getUserIdOnSharedPreferences()?.let { customerId ->
            calculateDiscounts(customerId)

        }
    }

    private fun calculateDiscounts(customerID: String) {

        val discountPaymentTextView = findViewById<TextView>(R.id.discount_Amount)
        val totalPaymentTextView = findViewById<TextView>(R.id.total_amount)

        db.collection("points").document(customerID)
            .get()
            .addOnSuccessListener { document ->
                if (document != null && document.exists()) {
                    val points = document.getLong("points") ?: 0

                    val availablePoints = points - 5

                    calculatePayment(customerID) { payment ->
                        val discountPercentage = getDiscountPercentage(availablePoints)
                        val discount = (payment * discountPercentage) / 100
                        val totalAmount = payment - discount

                        discountPaymentTextView.text = "Rs.$discount"
                        totalPaymentTextView.text = "Rs.$totalAmount"
                    }



                } else {
                    // Corrected error message
                    Log.e("Firestore", "No water data found for this customer")
                }
            }
            .addOnFailureListener { exception ->
                // Corrected error message
                Log.e("Firestore", "Error fetching water data: ${exception.message}")
            }
    }

    private fun calculatePayment(customerID: String, callback: (Long) -> Unit) {

        val waterPaymentTextView = findViewById<TextView>(R.id.water_payment)

        db.collection("water").document(customerID)
            .get()
            .addOnSuccessListener { document ->
                if (document != null && document.exists()) {
                    val waterAmount = document.getString("waterAmount")!!.toLong() ?: 0
                    val waterPayment = waterAmount * 3

                    // Display the water payment with currency symbol
                    waterPaymentTextView.text = "Rs.$waterPayment"

                    callback(waterPayment)

                } else {
                    // Corrected error message
                    Log.e("Firestore", "No water data found for this customer")
                }
            }
            .addOnFailureListener { exception ->
                // Corrected error message
                Log.e("Firestore", "Error fetching water data: ${exception.message}")
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

    private fun getUserIdOnSharedPreferences(): String? {
        val sharedPreferences = getSharedPreferences("AquaFlow", MODE_PRIVATE)
        return sharedPreferences.getString("userId", "")
    }


}
