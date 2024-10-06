package com.chirathi.aquaflow

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import cjh.WaveProgressBarlibrary.WaveProgressBar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import java.text.SimpleDateFormat
import java.util.*

class HomeFragment : Fragment() {

    private lateinit var quotaValue: TextView
    private lateinit var waveProgressBarHome: WaveProgressBar
    private lateinit var updatedDate: TextView
    private lateinit var firestore: FirebaseFirestore
    private lateinit var auth: FirebaseAuth
    private val totalQuotaValue = 1000 // Set total quota value as a constant

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_home, container, false)

        // Initialize views
        quotaValue = view.findViewById(R.id.water_quota_value)
        waveProgressBarHome = view.findViewById(R.id.wave_progress_bar)
        updatedDate = view.findViewById(R.id.updated_date)
        firestore = Firebase.firestore
        auth = FirebaseAuth.getInstance()

        // Set the updated date as today's date
        setUpdatedDate()

        // Fetch water data when fragment is created
        getUserIdOnSharedPreferences()?.let { customerId ->
            fetchRealTimeWaterAmount(customerId)
        }

        // Button to navigate to WaterQuotaActivity
        val waterQuotaView = view.findViewById<Button>(R.id.view_more_quota)
        waterQuotaView.setOnClickListener {
            val intent = Intent(context, WaterQuotaActivity::class.java)
            startActivity(intent)
        }

        return view
    }

    // Function to set the updated date as today's date
    private fun setUpdatedDate() {
        val currentDate = SimpleDateFormat("dd MMM yyyy", Locale.getDefault()).format(Date())
        updatedDate.text = "Updated Date: $currentDate"
    }

    // Function to fetch water amount in real-time using Firestore snapshot listener
    private fun fetchRealTimeWaterAmount(customerId: String) {
        firestore.collection("water").document(customerId)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    Toast.makeText(context, "Error listening for updates: ${error.message}", Toast.LENGTH_SHORT).show()
                    return@addSnapshotListener
                }

                if (snapshot != null && snapshot.exists()) {
                    // Retrieve "waterAmount" as a String
                    val currentWaterAmount = snapshot.getString("waterAmount") ?: "0"
                    val waterAmount = currentWaterAmount.toIntOrNull() ?: 0

                    // Set the remaining and usage amounts
                    val remainingWaterAmountValue: Int
                    val usageWaterAmountValue: Int

                    when {
                        waterAmount == 1000 -> {
                            // Supplier has entered 1000 liters for a new week
                            remainingWaterAmountValue = totalQuotaValue
                            usageWaterAmountValue = 0
                        }
                        waterAmount == 0 -> {
                            // Initial state for a new customer: Set to 0 until supplier enters quota
                            remainingWaterAmountValue = 0
                            usageWaterAmountValue = 0
                        }
                        else -> {
                            // Calculate remaining and used amounts for the week
                            remainingWaterAmountValue = totalQuotaValue - waterAmount
                            usageWaterAmountValue = waterAmount
                        }
                    }

                    // Update the quota value text
                    quotaValue.text = "$remainingWaterAmountValue L"

                    // Calculate percentage
                    val remainingPercentageValue = (remainingWaterAmountValue.toFloat() / totalQuotaValue) * 100

                    // Ensure the wave animation continues smoothly by resetting and updating the progress
                    waveProgressBarHome.apply {
                        setProgress(0) // Reset progress first
                        postDelayed({
                            setProgress(remainingPercentageValue.toInt())
                        }, 200) // Update after a short delay to ensure smooth transition
                    }
                } else {
                    Toast.makeText(context, "No water data found for this customer", Toast.LENGTH_SHORT).show()
                }
            }
    }

    // Retrieve user ID from shared preferences
    private fun getUserIdOnSharedPreferences(): String? {
        val sharedPreferences = activity?.getSharedPreferences("AquaFlow", Context.MODE_PRIVATE)
        return sharedPreferences?.getString("userId", "")
    }
}
