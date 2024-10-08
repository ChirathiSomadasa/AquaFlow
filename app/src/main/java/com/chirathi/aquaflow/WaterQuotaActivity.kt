package com.chirathi.aquaflow

import android.os.Bundle
import android.util.Log
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import cjh.WaveProgressBarlibrary.WaveProgressBar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class WaterQuotaActivity : AppCompatActivity() {

    private lateinit var totalQuota: TextView
    private lateinit var remainingAmount: TextView
    private lateinit var usageAmount: TextView
    private lateinit var remainingPercentage: TextView
    private lateinit var usagePercentage: TextView
    private lateinit var waveProgressBar: WaveProgressBar
    private val firestore: FirebaseFirestore = Firebase.firestore
    private lateinit var auth: FirebaseAuth

    private val totalQuotaValue = 1000 // Set total quota value as constant

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_water_quota)

        // Initialize views
        totalQuota = findViewById(R.id.total_quota)
        remainingAmount = findViewById(R.id.remaining_amount)
        usageAmount = findViewById(R.id.usage_amount)
        remainingPercentage = findViewById(R.id.remaining_percentage)
        usagePercentage = findViewById(R.id.usage_percentage)
        waveProgressBar = findViewById(R.id.waveProgressBar)

        // Initialize Firebase Auth
        auth = FirebaseAuth.getInstance()

        // Fetch user ID and set up a real-time listener
        getUserIdOnSharedPreferences()?.let { customerId ->
            setupRealTimeListener(customerId)
        }

        // Back button listener
        val back = findViewById<ImageView>(R.id.backbtn)
        back.setOnClickListener {
            finish()
        }
    }

    // Set up real-time Firestore listener to monitor changes in water amount
    private fun setupRealTimeListener(customerId: String) {
        firestore.collection("water").document(customerId)
            .addSnapshotListener { snapshot, exception ->
                if (exception != null) {
                    Toast.makeText(this, "Error listening for updates: ${exception.message}", Toast.LENGTH_SHORT).show()
                    return@addSnapshotListener
                }

                if (snapshot != null && snapshot.exists()) {
                    // Retrieve "waterAmount" as a String
                    val currentWaterAmount = snapshot.getString("waterAmount") ?: "0"
                    val waterAmount = currentWaterAmount.toIntOrNull() ?: 0

                    // Set the remaining and usage amounts
                    val remainingWaterAmountValue: Int
                    val usageWaterAmountValue: Int

                    // Check if waterAmount is 1000 (new week or initial quota set)
                    if (waterAmount == 1000) {
                        // Supplier has entered 1000 liters for a new week
                        remainingWaterAmountValue = totalQuotaValue
                        usageWaterAmountValue = 0
                    } else if (waterAmount == 0) {
                        // Initial state for a new customer: Set to 0 until supplier enters quota
                        remainingWaterAmountValue = 0
                        usageWaterAmountValue = 0
                    } else {
                        // Calculate remaining and used amounts for the week
                        remainingWaterAmountValue = totalQuotaValue - waterAmount
                        usageWaterAmountValue = waterAmount
                    }

                    // Update the UI
                    totalQuota.text = "$totalQuotaValue L"
                    usageAmount.text = "$usageWaterAmountValue L"
                    remainingAmount.text = "$remainingWaterAmountValue L"

                    // Calculate percentages
                    val remainingPercentageValue = (remainingWaterAmountValue.toFloat() / totalQuotaValue) * 100
                    val usagePercentageValue = (usageWaterAmountValue.toFloat() / totalQuotaValue) * 100

                    remainingPercentage.text = "${remainingPercentageValue.toInt()}%"
                    usagePercentage.text = "${usagePercentageValue.toInt()}%"

                    // Set progress for WaveProgressBar based on remainingPercentageValue
                    waveProgressBar.setProgress(remainingPercentageValue.toInt())

                    Log.d("waterAmount", "Real-time Update: $waterAmount")
                } else {
                    Toast.makeText(this, "No water data found for this customer", Toast.LENGTH_SHORT).show()
                }
            }
    }

    // Retrieve user ID from shared preferences
    private fun getUserIdOnSharedPreferences(): String? {
        val sharedPreferences = getSharedPreferences("AquaFlow", MODE_PRIVATE)
        return sharedPreferences.getString("userId", "")
    }
}
