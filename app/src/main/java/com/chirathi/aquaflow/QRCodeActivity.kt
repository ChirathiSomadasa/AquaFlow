package com.chirathi.aquaflow

import android.graphics.Bitmap
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.zxing.WriterException
import androidmads.library.qrgenearator.QRGContents
import androidmads.library.qrgenearator.QRGEncoder
import de.hdodenhof.circleimageview.CircleImageView

class QRCodeActivity : AppCompatActivity() {

    private lateinit var qrCodeImageView: ImageView
    private lateinit var profileName: TextView
    private lateinit var QRProfileImage: CircleImageView
    private var bitmap: Bitmap? = null
    private val TAG = "QRActivity"
    private val db = Firebase.firestore
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_qrcode)

        // Initialize UI components
        qrCodeImageView = findViewById(R.id.qr_code)
        profileName = findViewById(R.id.profile_name)
        QRProfileImage = findViewById(R.id.profile_image)
        auth = FirebaseAuth.getInstance()

        // Get the logged-in user's ID from FirebaseAuth
        val userId = auth.currentUser?.uid

        // Log for debugging
        Log.d(TAG, "User ID: $userId")

        if (userId != null) {
            // Generate QR code for the user's ID
            generateQRCode(userId)

            // Load profile image using Firestore
            loadProfileImage(userId) // Fetch profile image URL from Firestore
        } else {
            Log.e(TAG, "User not logged in")
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

    // Function to load profile image from Firestore
    private fun loadProfileImage(userId: String) {
        db.collection("users").document(userId).get()
            .addOnSuccessListener { document ->
                if (document != null) {
                    val profileImageUrl = document.getString("profileImageUrl") // Assuming your Firestore document has a field named 'profileImageUrl'

                    if (!profileImageUrl.isNullOrEmpty()) {
                        // Load profile image using Glide
                        Glide.with(this)
                            .load(profileImageUrl)
                            .placeholder(R.drawable.user3) // Placeholder image while loading
                            .into(QRProfileImage)
                    } else {
                        Log.e(TAG, "Profile Image URL is null or empty")
                        //Toast.makeText(this, "No profile image found", Toast.LENGTH_SHORT).show()
                    }
                } else {
                    Log.e(TAG, "No such document")
                    Toast.makeText(this, "User data not found", Toast.LENGTH_SHORT).show()
                }
            }
            .addOnFailureListener { exception ->
                Log.e(TAG, "Error fetching profile image: ${exception.message}")
                Toast.makeText(this, "Error fetching profile image: ${exception.message}", Toast.LENGTH_SHORT).show()
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

                    Log.d(TAG, "User Full Name: $fullName")

                    // Initialize the QR Encoder with the value (userId), type, and dimension
                    val qrgEncoder = QRGEncoder(userId, null, QRGContents.Type.TEXT, 300)
                    qrgEncoder.setColorBlack(Color.WHITE)
                    qrgEncoder.setColorWhite(Color.BLACK)

                    try {
                        // Getting QR-Code as Bitmap
                        bitmap = qrgEncoder.bitmap
                        // Setting Bitmap to ImageView
                        qrCodeImageView.setImageBitmap(bitmap)
                    } catch (e: WriterException) {
                        Log.e(TAG, "QR Code Generation Error: ${e.message}")
                    }

                } else {
                    Log.e(TAG, "No such document")
                    Toast.makeText(this, "User data not found", Toast.LENGTH_SHORT).show()
                }
            }
            .addOnFailureListener { exception ->
                Log.e(TAG, "Error fetching user data: ${exception.message}")
                Toast.makeText(this, "Error fetching user data: ${exception.message}", Toast.LENGTH_SHORT).show()
            }
    }
}
