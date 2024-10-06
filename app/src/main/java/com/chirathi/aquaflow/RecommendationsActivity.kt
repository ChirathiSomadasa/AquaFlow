package com.chirathi.aquaflow

import android.content.Intent
import android.os.Bundle
import android.widget.ImageButton
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity

class RecommendationsActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_recommendations)

        val back = findViewById<ImageButton>(R.id.back)

        back.setOnClickListener {
            startActivity(Intent(this, WasteWaterAdviceActivity::class.java))
        }
    }
}