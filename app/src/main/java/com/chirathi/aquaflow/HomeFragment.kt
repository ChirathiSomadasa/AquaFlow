package com.chirathi.aquaflow

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import cjh.WaveProgressBarlibrary.WaveProgressBar

class HomeFragment : Fragment() {

    private lateinit var quotaValue: TextView
    private lateinit var waveProgressBarHome: WaveProgressBar
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_home, container, false)

        // Initialize views here
        quotaValue = view.findViewById(R.id.water_quota_value)
        waveProgressBarHome = view.findViewById(R.id.wave_progress_bar) // Update the ID as per your layout

        val waterQuotaView = view.findViewById<Button>(R.id.view_more_quota)
        waterQuotaView.setOnClickListener {
            val intent = Intent(context, WaterQuotaActivity::class.java)
            startActivity(intent)
        }

        return view
    }

}