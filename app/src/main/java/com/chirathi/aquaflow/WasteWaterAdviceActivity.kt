package com.chirathi.aquaflow

import android.content.Intent
import android.os.Bundle
import android.widget.LinearLayout
import androidx.appcompat.app.AppCompatActivity


class WasteWaterAdviceActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_waste_water_advice)

            val btnWaterFootprint = findViewById<LinearLayout>(R.id.btnWaterFootprint)
            val btnUsageGraphs = findViewById<LinearLayout>(R.id.btnUsageGraphs)
            val btnRecommendations = findViewById<LinearLayout>(R.id.btnRecommendations)

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
