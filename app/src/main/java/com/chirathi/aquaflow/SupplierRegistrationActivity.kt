package com.chirathi.aquaflow

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView

class SupplierRegistrationActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_supplier_registration)

        val back= findViewById<ImageView>(R.id.back)
        back.setOnClickListener {
            finish()
        }

        val SigninLink = findViewById<TextView>(R.id.link_signin)
        SigninLink.setOnClickListener {
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
        }

        val SignupBtnk = findViewById<Button>(R.id.signup_btn)
        SignupBtnk.setOnClickListener {
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
        }
    }
}