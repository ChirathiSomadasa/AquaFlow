package com.chirathi.aquaflow

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.fragment.app.Fragment

class HomeFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_home, container, false)

        // Button to view more quota
        val scanqrcodeView = view.findViewById<Button>(R.id.view_more_quota)
        scanqrcodeView.setOnClickListener {
            val intent = Intent(context, WaterQuotaActivity::class.java)
            startActivity(intent)
        }

        // Button to view more tips
        val viewMoreTips = view.findViewById<Button>(R.id.view_more_tip)
        viewMoreTips.setOnClickListener {
            val intent = Intent(context, WasteWaterAdviceActivity::class.java)
            startActivity(intent)
        }

        return view
    }
}
