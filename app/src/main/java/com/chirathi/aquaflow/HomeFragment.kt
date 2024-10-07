package com.chirathi.aquaflow

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class HomeFragment : Fragment() {

    private lateinit var auth: FirebaseAuth
    private val db = Firebase.firestore

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_home, container, false)

        auth = FirebaseAuth.getInstance()

        // Reference to profile name and email TextViews
        val userName = view.findViewById<TextView>(R.id.userName) // Add this
        val PointsTextView = view.findViewById<TextView>(R.id.loyalty_balance_text)
        val rewardTextView = view.findViewById<TextView>(R.id.reward_text)


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

                        // Concatenate first and last names and display them
                        userName.text = " Hello $firstName $lastName"

                    }
                }
                .addOnFailureListener { exception ->
                    // Handle errors
                    Log.e("HomeFragment", "Error fetching user data", exception)
                }

            db.collection("points").document(userId)
                .get()
                .addOnSuccessListener { document ->
                    if (document != null && document.exists()) {
                        // Retrieve the points data from Firestore
                        val weeklyPoints = document.getLong("points") ?: 0

                        PointsTextView.text = "$weeklyPoints Current Points"

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
                .addOnFailureListener { exception ->
                    Log.e("Firestore", "Error fetching points data: ${exception.message}")
                }


        }

        val scanqrcodeView = view.findViewById<Button>(R.id.view_more_quota)
        scanqrcodeView.setOnClickListener {
            val intent = Intent(context, WaterQuotaActivity::class.java)
            startActivity(intent)
        }

        val loyaltyBalanceView = view.findViewById<Button>(R.id.view_more_loyalty)
        loyaltyBalanceView.setOnClickListener {
            val intent = Intent(context, MembershipActivity::class.java)
            startActivity(intent)
        }

        return view
    }



}