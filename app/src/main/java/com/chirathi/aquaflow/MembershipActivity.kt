package com.chirathi.aquaflow

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class MembershipActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_membership)

        val next = findViewById<Button>(R.id.continue_button)
        next.setOnClickListener {
            val intent = Intent(this, MembershipDetailsActivity::class.java)
            startActivity(intent)
        }

        val back= findViewById<ImageView>(R.id.backbtn)
        back.setOnClickListener {
            finish()
        }

    }
}