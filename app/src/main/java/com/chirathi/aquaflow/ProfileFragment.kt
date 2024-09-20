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
        val rootView =  inflater.inflate(R.layout.fragment_profile, container, false)

        // Initialize the profile details layout
        val profileDetailsLayout:RelativeLayout = rootView.findViewById(R.id.profile_details)

        // Set a click listener on the profile details layout
        profileDetailsLayout.setOnClickListener {
            // Start ProfileDetailsActivity
            val intent = Intent(activity, ProfileDetailsActivity::class.java)
            startActivity(intent)
        }

        return rootView

    }



}