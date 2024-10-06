package com.chirathi.aquaflow

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity


class WaterFootprintActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_water_footprint)

        val btnCalculateFootprint = findViewById<Button>(R.id.btnCalculateFootprint)

        btnCalculateFootprint.setOnClickListener {
            startActivity(Intent(this, FootprintResultActivity::class.java))
        }
    }

}
