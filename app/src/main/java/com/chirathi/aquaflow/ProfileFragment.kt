package com.chirathi.aquaflow

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RelativeLayout
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.google.firebase.auth.FirebaseAuth

class ProfileFragment : Fragment() {

    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        auth = FirebaseAuth.getInstance() // Initialize FirebaseAuth
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val view = inflater.inflate(R.layout.fragment_profile, container, false)

        val profileView = view.findViewById<RelativeLayout>(R.id.profile_details)
        profileView.setOnClickListener {
            // Start ProfileDetailsActivity
            val intent = Intent(activity, ProfileDetailsActivity::class.java)
            startActivity(intent)
        }

        val qrcodeView = view.findViewById<RelativeLayout>(R.id.qrcode)
        qrcodeView.setOnClickListener {
            val intent = Intent(context, QRCodeActivity::class.java)
            startActivity(intent)
        }

        val logoutView = view.findViewById<RelativeLayout>(R.id.logout_button)
        logoutView.setOnClickListener {
            performLogout()
        }

        return view
    }

    private fun performLogout() {
        auth.signOut() // Sign out from Firebase
        Toast.makeText(context, "Logged out successfully.", Toast.LENGTH_SHORT).show()

        // Navigate to LoginActivity
        val intent = Intent(activity, LoginActivity::class.java)
        startActivity(intent)
        requireActivity().finish()
    }
}
