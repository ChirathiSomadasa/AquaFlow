package com.chirathi.aquaflow

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.LinearLayout

class CreateAccountActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_account)

        /*val consumer = findViewById<Button>(R.id.consumer_btn) // consumer_layout
        consumer.setOnClickListener {
            val intent = Intent(this, LoginActivity::class.java)
            intent.putExtra("userType", "Consumer")
            startActivity(intent)
        }
        val supplier = findViewById<Button>(R.id.supplier_btn) // supplier_layout
        supplier.setOnClickListener {
            val intent = Intent(this, LoginActivity::class.java)
            intent.putExtra("userType", "Supplier")
            startActivity(intent)
        }*/

        val consumer = findViewById<LinearLayout>(R.id.consumer_layout) // consumer_layout
        consumer.setOnClickListener {
            val intent = Intent(this, CustomerRegistrationActivity::class.java)
            intent.putExtra("userType", "Consumer")
            startActivity(intent)
        }
        val supplier = findViewById<LinearLayout>(R.id.supplier_layout) // supplier_layout
        supplier.setOnClickListener {
            val intent = Intent(this, SupplierRegistrationActivity::class.java)
            intent.putExtra("userType", "Supplier")
            startActivity(intent)
        }
    }
}