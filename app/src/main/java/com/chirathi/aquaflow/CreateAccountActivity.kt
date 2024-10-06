package com.chirathi.aquaflow

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button

class CreateAccountActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_account)

        val consumer = findViewById<Button>(R.id.consumer_btn) // consumer_layout
        consumer.setOnClickListener {
            val intent = Intent(this, CustomerRegistrationActivity::class.java)
            startActivity(intent)
        }
        val supplier = findViewById<Button>(R.id.supplier_btn) // supplier_layout
        supplier.setOnClickListener {
            val intent = Intent(this, SupplierRegistrationActivity::class.java)//
            startActivity(intent)
        }
    }
}