package com.chirathi.aquaflow

import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AlertDialog
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class PaymentProcessActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private val db = Firebase.firestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_payment_process)

        auth = FirebaseAuth.getInstance()

        val fName = findViewById<TextView>(R.id.firstname) // Add this
        val lName = findViewById<TextView>(R.id.lastname) // Add this
        val customerid = findViewById<TextView>(R.id.customer_id) // Add this
        val payment = findViewById<TextView>(R.id.payment)  // Email


        // Get data from Intent
        val intent = intent
        val customerId = intent.getStringExtra("Customer_ID")
        val firstName =  intent.getStringExtra("First_Name")
        val lastName = intent.getStringExtra("Last_Name")


        customerid.text = customerId
        fName.text = firstName
        lName.text = lastName

        val back = findViewById<ImageView>(R.id.backbtn)
        back.setOnClickListener {
            finish()
        }


        val submitBtn: Button = findViewById(R.id.submit_btn)

        submitBtn.setOnClickListener {
            // Get the details from the UI
            val customerID = customerid.toString().trim()
            val firstName = fName.text.toString().trim()
            val lastName = lName.text.toString().trim()
            val paymentAmount = payment.text.toString().trim()

            // Validate the input fields
            if (firstName.isEmpty() || lastName.isEmpty() || customerID.isEmpty() || paymentAmount.isEmpty()) {
                // Show an error message (e.g., a Toast or a dialog)
                Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            db.collection("users")
                .get()
                .addOnSuccessListener { documents ->
                    if (!documents.isEmpty) {
                        // User exists, proceed with saving payment details
                        val customerId = documents.documents[0].id

                        // Prepare the data to be saved
                        val paymentDetails = hashMapOf(
                            "customerID" to customerId,// Optional, you can use this to link the payment to a user
                            "paymentAmount" to paymentAmount

                        )

                        // Save the data to Firestore
                        db.collection("payment")
                            .add(paymentDetails)
                            .addOnSuccessListener { documentReference ->
                                // Data saved successfully, show a success message
                                Toast.makeText(
                                    this,
                                    "Payment processed successfully!",
                                    Toast.LENGTH_SHORT
                                ).show()

                                // Optionally show the custom submit dialog after saving
                                val builder = AlertDialog.Builder(this)
                                val inflater = layoutInflater
                                val dialogView = inflater.inflate(R.layout.custom_submit, null)
                                builder.setView(dialogView)

                                val dialog: AlertDialog = builder.create()
                                val dialogButton: Button =
                                    dialogView.findViewById(R.id.dialog_button)
                                dialogButton.setOnClickListener {
                                    dialog.dismiss()
                                }
                                dialog.show()

                                // Clear the form fields
                                fName.text = ""
                                lName.text = ""
                                payment.text = ""
                                customerid.text =""
                            }
                            .addOnFailureListener { e ->
                                // Handle the error
                                Toast.makeText(
                                    this,
                                    "Error processing payment: ${e.message}",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                    }
                    else {
                        // Show an error message if no user is found
                        Toast.makeText(this, "This user isn't in our records. Please double-check the details.", Toast.LENGTH_SHORT).show()
                    }
                }
        }
    }
}