package com.chirathi.aquaflow

import android.app.ProgressDialog
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.messaging.FirebaseMessaging

class LoginActivity : AppCompatActivity() {

    private lateinit var emailInput: TextInputEditText
    private lateinit var passwordInput: TextInputEditText
    private lateinit var auth: FirebaseAuth
    private val db = Firebase.firestore
    private var userType: String? = null // Variable to store user type
    private lateinit var progressDialog: ProgressDialog // ProgressDialog instance

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        // Initialize FirebaseAuth
        auth = FirebaseAuth.getInstance()

        // Initialize views
        emailInput = findViewById(R.id.login_email)
        passwordInput = findViewById(R.id.login_password)

        // Initialize ProgressDialog
        progressDialog = ProgressDialog(this).apply {
            setMessage("Logging in...") // Set message shown during loading
            setCancelable(false) // Prevent user from dismissing the dialog
        }

        // Retrieve user type from intent
        userType = intent.getStringExtra("userType")

        // Login button click listener
        val login = findViewById<Button>(R.id.btn_signin)
        login.setOnClickListener {
            val emailText = emailInput.text.toString().trim()
            val passwordText = passwordInput.text.toString().trim()

            // Check if email and password are not empty
            if (emailText.isEmpty() || passwordText.isEmpty()) {
                Toast.makeText(this, "Please enter email and password", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // Show ProgressDialog before starting the login process
            progressDialog.show()

            // Sign in the user with FirebaseAuth
            auth.signInWithEmailAndPassword(emailText, passwordText)
                .addOnCompleteListener { task ->
                    // Dismiss the progress dialog once task completes
                    progressDialog.dismiss()

                    if (task.isSuccessful) {
                        // Get the user ID
                        val userId = auth.currentUser?.uid

                        if (userId != null) {
                            // Retrieve the user's role (Customer or Supplier) from Firestore
                            db.collection("users").document(userId).get()
                                .addOnSuccessListener { document ->
                                    if (document != null) {
                                        saveUserIdOnSharedPreferences(userId)
                                        val isSupplier = document.getBoolean("isSupplier") ?: false

                                        // Get FCM token
                                        FirebaseMessaging.getInstance().token
                                            .addOnCompleteListener { tokenTask ->
                                                if (tokenTask.isSuccessful) {
                                                    val fcmToken = tokenTask.result

                                                    // Store FCM token in Firestore
                                                    db.collection("users").document(userId)
                                                        .update("fcmToken", fcmToken)
                                                        .addOnSuccessListener {
                                                            Toast.makeText(
                                                                this,
                                                                "FCM Token saved",
                                                                Toast.LENGTH_SHORT
                                                            ).show()
                                                        }
                                                        .addOnFailureListener { e ->
                                                            Toast.makeText(
                                                                this,
                                                                "Error saving FCM token: ${e.message}",
                                                                Toast.LENGTH_SHORT
                                                            ).show()
                                                        }
                                                } else {
                                                    Toast.makeText(
                                                        this,
                                                        "Failed to get FCM token",
                                                        Toast.LENGTH_SHORT
                                                    ).show()
                                                }
                                            }

                                        // Navigate based on user role(supplier / consumer)
                                        if (isSupplier) {
                                            // Navigate to Supplier Dashboard
                                            val intent = Intent(this, SupplierHomeActivity::class.java)
                                            startActivity(intent)
                                            finish()  // Close LoginActivity
                                        } else {
                                            // Navigate to Customer Dashboard
                                            val intent = Intent(this, HomeActivity::class.java)
                                            startActivity(intent)
                                            finish()  // Close LoginActivity
                                        }
                                    } else {
                                        Toast.makeText(this, "User data not found", Toast.LENGTH_SHORT).show()
                                    }
                                }
                                .addOnFailureListener {
                                    Toast.makeText(this, "Error fetching user data", Toast.LENGTH_SHORT).show()
                                }
                        }
                    } else {
                        // Display error message if login failed
                        Toast.makeText(this, "Login failed: ${task.exception?.message}", Toast.LENGTH_SHORT).show()
                    }
                }
        }

        // Create Account button click listener
        val createAccount = findViewById<Button>(R.id.btn_create_account)
        createAccount.setOnClickListener {
            // Check user type and navigate to the respective registration activity
            if (userType == "Consumer") {
                val intent = Intent(this, CustomerRegistrationActivity::class.java)
                startActivity(intent)
            } else if (userType == "Supplier") {
                val intent = Intent(this, SupplierRegistrationActivity::class.java)
                startActivity(intent)
            } else {
                Toast.makeText(this, "User type not recognized", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun saveUserIdOnSharedPreferences(userId: String){
        val sharedPreferences=getSharedPreferences("AquaFlow", MODE_PRIVATE)
        val myEdit=sharedPreferences.edit()
        myEdit.putString("userId", userId)
        myEdit.apply()
    }
}
