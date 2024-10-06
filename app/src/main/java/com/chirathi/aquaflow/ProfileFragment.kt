package com.chirathi.aquaflow

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RelativeLayout
import android.widget.TextView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class ProfileFragment : Fragment() {

    private lateinit var auth: FirebaseAuth
    private val db = Firebase.firestore

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_profile, container, false)

        auth = FirebaseAuth.getInstance()

        // Reference to profile name and email TextViews
        val profileName = view.findViewById<TextView>(R.id.profile_name) // Add this

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
                        profileName.text = "$firstName $lastName"

                    }
                }
                .addOnFailureListener { exception ->
                    // Handle errors
                    Log.e("ProfileFragment", "Error fetching user data", exception)
                }
        }

        // Handle profile details click
        val profileView = view.findViewById<RelativeLayout>(R.id.profile_details)
        profileView.setOnClickListener {
            val intent = Intent(activity, ProfileDetailsActivity::class.java)
            startActivity(intent)
        }

        // Handle QR code view click
        val qrcodeView = view.findViewById<RelativeLayout>(R.id.qrcode)
        qrcodeView.setOnClickListener {
            val intent = Intent(context, QRCodeActivity::class.java)
            startActivity(intent)
        }

        // Handle membership view click
        val membershipView = view.findViewById<RelativeLayout>(R.id.membership)
        membershipView.setOnClickListener {
            val intent = Intent(context, MembershipActivity::class.java)
            startActivity(intent)
        }

        // Handle payment view click
        val paymentView = view.findViewById<RelativeLayout>(R.id.payment)
        paymentView.setOnClickListener {
            val intent = Intent(context, PaymentActivity::class.java)
            startActivity(intent)
        }

        return view
    }
}
