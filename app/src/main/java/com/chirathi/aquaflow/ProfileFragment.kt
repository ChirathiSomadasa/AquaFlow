package com.chirathi.aquaflow

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RelativeLayout


class ProfileFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_profile, container, false)

        val profileView = view.findViewById<RelativeLayout>(R.id.profile_details)
        // Set a click listener on the profile details layout
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

        return view

    }



}