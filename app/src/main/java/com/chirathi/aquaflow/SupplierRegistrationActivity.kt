package com.chirathi.aquaflow

import android.app.ProgressDialog
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class SupplierRegistrationActivity : AppCompatActivity() {

    // Declare views and Firebase instances
    lateinit var sup_firstName: TextInputEditText
    lateinit var sup_lastName: TextInputEditText
    lateinit var sup_address: TextInputEditText
    lateinit var sup_email: TextInputEditText
    lateinit var sup_password: TextInputEditText
    lateinit var sup_confirmPassword: TextInputEditText
    private lateinit var auth: FirebaseAuth
    private val db = Firebase.firestore
    private lateinit var progressDialog: ProgressDialog  // Declare ProgressDialog

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_supplier_registration)

        // Initialize FirebaseAuth
        auth = FirebaseAuth.getInstance()

        // Initialize views
        sup_firstName = findViewById(R.id.sup_firstName)
        sup_lastName = findViewById(R.id.sup_lastName)
        sup_address = findViewById(R.id.sup_address)
        sup_email = findViewById(R.id.sup_email)
        sup_password = findViewById(R.id.sup_password)
        sup_confirmPassword = findViewById(R.id.sup_confirmPassword)

        // Initialize ProgressDialog
        progressDialog = ProgressDialog(this)
        progressDialog.setMessage("Registering... Please wait.")
        progressDialog.setCancelable(false)

        // Back button listener
        val back = findViewById<ImageView>(R.id.back)
        back.setOnClickListener {
            finish()
        }

        // Sign-in link click listener
        val signinLink = findViewById<TextView>(R.id.link_signin)
        signinLink.setOnClickListener {
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
        }

        // Sign-up button listener
        val signupBtn = findViewById<Button>(R.id.signup_btn)
        signupBtn.setOnClickListener {
            val firstNameText = sup_firstName.text.toString().trim()
            val lastNameText = sup_lastName.text.toString().trim()
            val addressText = sup_address.text.toString().trim()
            val emailText = sup_email.text.toString().trim()
            val passwordText = sup_password.text.toString().trim()
            val confirmPasswordText = sup_confirmPassword.text.toString().trim()

            // Check if all fields are filled
            if (firstNameText.isEmpty() || lastNameText.isEmpty() || addressText.isEmpty() || emailText.isEmpty() || passwordText.isEmpty() || confirmPasswordText.isEmpty()) {
                Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // Check if passwords match
            if (passwordText != confirmPasswordText) {
                Toast.makeText(this, "Passwords do not match", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // Show ProgressDialog before starting registration
            progressDialog.show()

            // Create user with email and password
            auth.createUserWithEmailAndPassword(emailText, passwordText)
                .addOnCompleteListener { task ->
                    // Dismiss ProgressDialog after task is completed
                    progressDialog.dismiss()

                    if (task.isSuccessful) {
                        // Get the user ID of the newly created user
                        val userId = auth.currentUser?.uid

                        // Create a user map to store in Firestore
                        val userMap = hashMapOf(
                            "firstName" to firstNameText,
                            "lastName" to lastNameText,
                            "address" to addressText,
                            "email" to emailText,
                            "isSupplier" to true  // Supplier is marked as true
                        )

                        // Store user data in Firestore
                        if (userId != null) {
                            db.collection("users").document(userId).set(userMap)
                                .addOnSuccessListener {
                                    Toast.makeText(this, "Successfully Registered!", Toast.LENGTH_SHORT).show()

                                    // Clear fields
                                    sup_firstName.text?.clear()
                                    sup_lastName.text?.clear()
                                    sup_address.text?.clear()
                                    sup_email.text?.clear()
                                    sup_password.text?.clear()
                                    sup_confirmPassword.text?.clear()

                                    // Navigate to LoginActivity
                                    val intent = Intent(this, LoginActivity::class.java)
                                    startActivity(intent)
                                    finish()
                                }
                                .addOnFailureListener {
                                    Toast.makeText(this, "Failed to save user data", Toast.LENGTH_SHORT).show()
                                }
                        }
                    } else {
                        // Handle different errors more specifically
                        val errorMessage = when (task.exception?.message) {
                            "The email address is already in use by another account." -> "Email is already registered. Try another email."
                            "The given password is invalid. [ Password should be at least 6 characters ]" -> "Password must be at least 6 characters."
                            "A network error (such as timeout, interrupted connection or unreachable host) has occurred." -> "Network error. Please check your connection."
                            else -> "Registration Failed: ${task.exception?.message}"
                        }

                        // Display appropriate error message
                        Toast.makeText(this, errorMessage, Toast.LENGTH_SHORT).show()
                    }
                }
        }
    }
}
