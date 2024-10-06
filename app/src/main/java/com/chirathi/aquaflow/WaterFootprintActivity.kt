package com.chirathi.aquaflow

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity

class WaterFootprintActivity : AppCompatActivity() {



    override fun onCreate(savedInstanceState: Bundle?) {
        setContentView(R.layout.activity_water_footprint)
        super.onCreate(savedInstanceState)

        val btnCalculateFootprint = findViewById<Button>(R.id.btnCalculateFootprint)
        btnCalculateFootprint.setOnClickListener {
            startActivity(Intent(this, FootprintResultActivity::class.java))
        }

            }
        }

