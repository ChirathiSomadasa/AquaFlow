package com.chirathi.aquaflow

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ImageView

class WaterQuotaActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_water_quota)

        val back= findViewById<ImageView>(R.id.backbtn)
        back.setOnClickListener {
            finish()
        }
    }
}