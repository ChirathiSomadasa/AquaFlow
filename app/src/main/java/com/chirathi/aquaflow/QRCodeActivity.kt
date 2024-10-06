package com.chirathi.aquaflow

import android.graphics.Bitmap
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidmads.library.qrgenearator.QRGContents;
import androidmads.library.qrgenearator.QRGEncoder;
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.zxing.WriterException

class QRCodeActivity : AppCompatActivity() {

    private lateinit var qrCodeImageView: ImageView
    private lateinit var profileName: TextView
    var bitmap: Bitmap?=null
    private val TAG="QRActivity"
    private val db = Firebase.firestore
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_qrcode)

        // Initialize UI components
        qrCodeImageView = findViewById(R.id.qr_code)
        profileName = findViewById(R.id.profile_name)
        auth = FirebaseAuth.getInstance()

        // Get the current user ID
        val userId = auth.currentUser?.uid
        if (userId != null) {
            generateQRCode(userId)
        } else {
            Toast.makeText(this, "User not logged in", Toast.LENGTH_SHORT).show()
        }

        // Back button listener
        val back = findViewById<ImageView>(R.id.backbtn)
        back.setOnClickListener {
            finish()
        }

        // OK button listener
        val okbtn = findViewById<Button>(R.id.ok_button)
        okbtn.setOnClickListener {
            finish()
        }
    }

    // Function to generate the QR code
    private fun generateQRCode(userId: String) {
        // Fetch user data from Firestore
        db.collection("users").document(userId).get()
            .addOnSuccessListener { document ->
                if (document != null) {
                    // Get user details (FirstName, LastName)
                    val firstName = document.getString("firstName") ?: "FirstName"
                    val lastName = document.getString("lastName") ?: "LastName"

                    // Combine first name and last name for profile name
                    val fullName = "$firstName $lastName"

                    // Set the profile name TextView to the full name
                    profileName.text = fullName

                    Log.e("aaaaaa", "First Name: $firstName")

                    // Initializing the QR Encoder with your value to be encoded, type you required and Dimension
                    val qrgEncoder= QRGEncoder(userId, null, QRGContents.Type.TEXT, 300)
                    qrgEncoder.setColorBlack(Color.WHITE)
                    qrgEncoder.setColorWhite(Color.BLACK)
                    try {
                        // Getting QR-Code as Bitmap
                        bitmap=qrgEncoder.getBitmap()
                        // Setting Bitmap to ImageView
                        qrCodeImageView.setImageBitmap(bitmap)
                    } catch (e: WriterException) {
                        Log.v(TAG, e.toString())
                    }


                } else {
                    Toast.makeText(this, "User data not found", Toast.LENGTH_SHORT).show()
                }
            }
            .addOnFailureListener { exception ->
                Toast.makeText(this, "Error fetching user data: ${exception.message}", Toast.LENGTH_SHORT).show()
            }
    }
}
