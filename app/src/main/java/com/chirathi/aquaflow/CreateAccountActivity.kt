package com.chirathi.aquaflow

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button

class CreateAccountActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_account)

        val consumer = findViewById<Button>(R.id.consumer_btn)
        consumer.setOnClickListener {
            val intent = Intent(this, CustomerRegistrationActivity::class.java)
            startActivity(intent)
        }
    }
}