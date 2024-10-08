package com.chirathi.aquaflow

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.firestore.FirebaseFirestore
import android.util.Log
import android.widget.ImageView
import android.widget.TextView

class CustomerDetailsActivity : AppCompatActivity() {

    private lateinit var customerIdEditText: TextInputEditText
    private lateinit var firstNameEditText: TextInputEditText
    private lateinit var lastNameEditText: TextInputEditText
    private lateinit var waterAmountEditText: TextInputEditText
    private lateinit var BackBtn: ImageView

    private val firestore = FirebaseFirestore.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_customer_details)

        // Initialize EditText fields
        customerIdEditText = findViewById(R.id.customer_id_edit_text)
        firstNameEditText = findViewById(R.id.first_name_edit_text)
        lastNameEditText = findViewById(R.id.last_name_edit_text)
        waterAmountEditText = findViewById(R.id.water_amount_edit_text)

        val BackBtn = findViewById<ImageView>(R.id.backbtn)
        BackBtn.setOnClickListener {
            val intent = Intent(this, SupplierHomeActivity::class.java)
            startActivity(intent)
        }

        // Retrieve the customer ID from the intent
        val customerId = intent.getStringExtra("CUSTOMER_ID")

        // Fetch customer details if customer ID is not null
        customerId?.let {
            fetchCustomerDetails(it)
        }

        // Handle submit button click
        val submitButton = findViewById<Button>(R.id.signup_btn)
        submitButton.setOnClickListener {
            // Handle submit logic here
            submitCustomerDetails()
        }
    }

    // Function to fetch customer details from Firestore
    private fun fetchCustomerDetails(customerId: String) {
        firestore.collection("users").document(customerId)
            .get()
            .addOnSuccessListener { document ->
                if (document != null) {
                    // Assuming your document has fields: firstName, lastName, waterAmount
                    val firstName = document.getString("firstName")
                    val lastName = document.getString("lastName")
                    val waterAmount = document.getDouble("waterAmount")?.toString() // Convert to String if needed

                    // Auto-fill the fields
                    customerIdEditText.setText(customerId)
                    firstNameEditText.setText(firstName)
                    lastNameEditText.setText(lastName)
                    waterAmountEditText.setText(waterAmount)

                    Log.e("aaaaaa", "First Name: $firstName, Last Name: $lastName,Amount: $waterAmount")

                } else {
                    Toast.makeText(this, "No such customer", Toast.LENGTH_SHORT).show()
                }
            }
            .addOnFailureListener { exception ->
                Toast.makeText(this, "Error fetching customer details: ${exception.message}", Toast.LENGTH_SHORT).show()
            }
    }

    // Function to handle submit action
    private fun submitCustomerDetails() {
        val customerId = customerIdEditText.text.toString().trim()
        val waterAmount = waterAmountEditText.text.toString().trim()

        // Validate input before submitting
        if (customerId.isEmpty() || waterAmount.isEmpty()) {
            Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show()
            return
        }

        // Create a map to store in Firestore
        val waterMap = hashMapOf(
            "customerId" to customerId,
            "waterAmount" to waterAmount
        )

        // Store water amount data in the "water" collection with user ID as document
        firestore.collection("water").document(customerId)
            .set(waterMap)
            .addOnSuccessListener {
                Toast.makeText(this, "Water details saved successfully!", Toast.LENGTH_SHORT).show()

                // Clear the fields
                Log.e("water", "Amount: $waterAmount")
                waterAmountEditText.text?.clear()

            }
            .addOnFailureListener { exception ->
                Toast.makeText(this, "Failed to save water details: ${exception.message}", Toast.LENGTH_SHORT).show()
            }
    }
}
