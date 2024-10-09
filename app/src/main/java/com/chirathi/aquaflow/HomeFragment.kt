package com.chirathi.aquaflow

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.constraintlayout.widget.ConstraintLayout
import cjh.WaveProgressBarlibrary.WaveProgressBar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import java.text.SimpleDateFormat
import java.util.*

class HomeFragment : Fragment() {

    private lateinit var waterQuotaLayout: ConstraintLayout
    private lateinit var emptyWaterQuotaLayout: ConstraintLayout
    private lateinit var quotaValue: TextView
    private lateinit var waveProgressBarHome: WaveProgressBar
    private lateinit var updatedDate: TextView
    private lateinit var firestore: FirebaseFirestore
    private lateinit var auth: FirebaseAuth
    private val totalQuotaValue = 1000 // Set total quota value as a constant
    private lateinit var pointsTextView: TextView
    private lateinit var rewardTextView: TextView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_home, container, false)

        // Initialize views
        quotaValue = view.findViewById(R.id.water_quota_value)
        waterQuotaLayout = view.findViewById(R.id.water_quota_layout)
        emptyWaterQuotaLayout = view.findViewById(R.id.empty_water_quota_layout)
        waveProgressBarHome = view.findViewById(R.id.wave_progress_bar)
        updatedDate = view.findViewById(R.id.updated_date)
        pointsTextView = view.findViewById(R.id.loyalty_balance_text)
        rewardTextView = view.findViewById(R.id.reward_text)
        firestore = Firebase.firestore
        auth = FirebaseAuth.getInstance()

        // Set the updated date as today's date
        setUpdatedDate()

        // Fetch water data when fragment is created
        getUserIdOnSharedPreferences()?.let { customerId ->
            fetchRealTimeWaterAmount(customerId)
            fetchCustomerPoints(customerId)
        }

        // Reference to profile name and email TextViews
        val profileName = view.findViewById<TextView>(R.id.userName) // Add this

        // Fetch current user data from Firebase
        val currentUser = auth.currentUser
        if (currentUser != null) {
            val userId = currentUser.uid

            // Fetch user data from Firestore
            firestore.collection("users").document(userId)
                .get()
                .addOnSuccessListener { document ->
                    if (document != null) {
                        // Set user data to UI
                        val firstName = document.getString("firstName") ?: ""
                        val lastName = document.getString("lastName") ?: ""

                        // Concatenate first and last names and display them
                        profileName.text = "Hello $firstName $lastName"

                    }
                }
                .addOnFailureListener { exception ->
                    // Handle errors
                    Log.e("ProfileFragment", "Error fetching user data", exception)
                }
        }

        // Button to navigate to WaterQuotaActivity
        val waterQuotaView = view.findViewById<Button>(R.id.view_more_quota)
        waterQuotaView.setOnClickListener {
            val intent = Intent(context, WaterQuotaActivity::class.java)
            startActivity(intent)
        }
        val loyaltyPointsView = view.findViewById<Button>(R.id.view_more_loyalty)
        loyaltyPointsView.setOnClickListener {
            val intent = Intent(context, MembershipActivity::class.java)
            startActivity(intent)
        }
        val TipView = view.findViewById<Button>(R.id.view_more_tip)
        TipView.setOnClickListener {
            val intent = Intent(context, WasteWaterAdviceActivity::class.java)
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
                    waterQuotaLayout.visibility = View.VISIBLE
                    emptyWaterQuotaLayout.visibility = View.GONE
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
                    waterQuotaLayout.visibility = View.GONE
                    emptyWaterQuotaLayout.visibility = View.VISIBLE
                    // Toast.makeText(context, "No water data found for this customer", Toast.LENGTH_SHORT).show()
                }
            }
    }

    private fun fetchCustomerPoints(customerId: String){
        firestore.collection("points").document(customerId)
            .addSnapshotListener { snapshot, exception ->
                if (exception != null) {
                    return@addSnapshotListener
                }
                if (snapshot != null && snapshot.exists()) {
                    // Retrieve the points data from Firestore
                    val weeklyPoints = snapshot.getLong("points") ?: 0

                    if (weeklyPoints > 0 ){
                        pointsTextView.text = "$weeklyPoints Current Points"
                    }else{
                        pointsTextView.text = "No Current Points"
                    }

                    val rewardMessage: String

                    when {
                        weeklyPoints < 20 -> {
                            rewardMessage = " Basic Reward"
                        }

                        weeklyPoints in 20..39 -> {
                            rewardMessage = "Water Saver Reward"
                        }

                        weeklyPoints in 40..59 -> {
                            rewardMessage = "Eco Saver Reward"
                        }

                        weeklyPoints in 60..79 -> {
                            rewardMessage = "Blue Tier Reward"
                        }

                        weeklyPoints in 80..99 -> {
                            rewardMessage = "Aqua Champion"
                        }

                        weeklyPoints == 100L -> {
                            rewardMessage = "Hydro Hero"
                        }

                        else -> {
                            rewardMessage = " "
                        }
                    }

                    // Update the reward text view
                    rewardTextView.text = rewardMessage

                }
            }
    }

    // Retrieve user ID from shared preferences
    private fun getUserIdOnSharedPreferences(): String? {
        val sharedPreferences = activity?.getSharedPreferences("AquaFlow", Context.MODE_PRIVATE)
        return sharedPreferences?.getString("userId", "")
    }
}