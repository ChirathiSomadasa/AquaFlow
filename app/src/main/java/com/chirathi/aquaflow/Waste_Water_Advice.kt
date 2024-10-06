package com.chirathi.aquaflow

import android.content.Intent
import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity


class Waste_Water_Advice : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_waste_water_advice)

            val btnWaterFootprint = findViewById<TextView>(R.id.btnWaterFootprint)
            val btnUsageGraphs = findViewById<TextView>(R.id.btnUsageGraphs)
            val btnRecommendations = findViewById<TextView>(R.id.btnRecommendations)

            btnWaterFootprint.setOnClickListener {
                startActivity(Intent(this, WaterFootprintActivity::class.java))
            }

            btnUsageGraphs.setOnClickListener {
                startActivity(Intent(this, UsageGraphsActivity::class.java))
            }

            btnRecommendations.setOnClickListener {
                startActivity(Intent(this, RecommendationsActivity::class.java))
            }
        }
    }
