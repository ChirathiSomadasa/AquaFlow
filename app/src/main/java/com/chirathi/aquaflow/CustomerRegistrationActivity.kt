package com.chirathi.aquaflow

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView

class CustomerRegistrationActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_customer_registration)

        val back= findViewById<ImageView>(R.id.back)
        back.setOnClickListener {
            finish()
        }

        val SigninLink = findViewById<TextView>(R.id.link_signin)
        SigninLink.setOnClickListener {
            finish()
        }
    }
}