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
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class SupplierHomeFragment : Fragment() {
    private lateinit var firestore: FirebaseFirestore
    private lateinit var auth: FirebaseAuth

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_supplier_home, container, false)

        firestore = Firebase.firestore
        auth = FirebaseAuth.getInstance()

        // Reference to profile name and email TextViews
        val profileName = view.findViewById<TextView>(R.id.userNameTextView) // Add this

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

        val NotificationManagementView = view.findViewById<Button>(R.id.Notification_Management_btn)
        NotificationManagementView.setOnClickListener {
            val intent = Intent(context, NotificationManagementActivity::class.java)
            startActivity(intent)
        }

        val DeliverySchedulesView = view.findViewById<Button>(R.id.Delivery_Schedules_btn)
        DeliverySchedulesView.setOnClickListener {
            val intent = Intent(context, NotificationManagementActivity::class.java)
            startActivity(intent)
        }

        val scanqrcodeView = view.findViewById<Button>(R.id.Scan_QR_code_btn)
        scanqrcodeView.setOnClickListener {
            val intent = Intent(context, ScanQRCodeActivity::class.java)
            startActivity(intent)
        }

        // Find the button using the inflated view
        val paymentProcessView = view.findViewById<Button>(R.id.Payment_Process_btn)
        paymentProcessView.setOnClickListener {
            // Start PaymentProcessActivity when button is clicked
            val intent = Intent(context, PaymentProcessActivity::class.java)
            startActivity(intent)
        }

        // Return the inflated view
        return view
    }
}
