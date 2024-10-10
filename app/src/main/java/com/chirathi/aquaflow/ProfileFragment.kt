package com.chirathi.aquaflow

import android.app.Activity
import android.app.AlertDialog
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.widget.ImageView
import android.widget.Toast
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RelativeLayout
import android.widget.TextView
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import de.hdodenhof.circleimageview.CircleImageView
import java.util.*
import androidx.activity.result.contract.ActivityResultContracts

class ProfileFragment : Fragment() {

    private lateinit var profileImage: CircleImageView
    private lateinit var imageUploadIcon: ImageView
    private lateinit var ProfileName: TextView
    private lateinit var storage: FirebaseStorage
    private lateinit var firestore: FirebaseFirestore
    private lateinit var auth: FirebaseAuth
    private lateinit var selectedImg: Uri
    private lateinit var dialog: AlertDialog
    private var profileListener: ListenerRegistration? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_profile, container, false)

        // Initialize Firebase services
        auth = FirebaseAuth.getInstance() // Initialize auth before use
        storage = FirebaseStorage.getInstance()
        firestore = FirebaseFirestore.getInstance()

        // Initialize views
        profileImage = view.findViewById(R.id.profile_image)
        imageUploadIcon = view.findViewById(R.id.image_upload_icon)
        ProfileName = view.findViewById(R.id.user_profile_name)

        // Set onClickListener to upload icon
        imageUploadIcon.setOnClickListener {
            // Open the gallery to select an image
            val intent = Intent(Intent.ACTION_GET_CONTENT)
            intent.type = "image/*"
            startActivityForResult(intent, 1)
        }

        // Load user profile data including name
        loadProfileData()

        // Set click listeners for profile details and QR code views
        val profileView = view.findViewById<RelativeLayout>(R.id.profile_details)
        profileView.setOnClickListener {
            val intent = Intent(activity, ProfileDetailsActivity::class.java)
            startActivity(intent)
        }

        val qrcodeView = view.findViewById<RelativeLayout>(R.id.qrcode)
        qrcodeView.setOnClickListener {
            val intent = Intent(context, QRCodeActivity::class.java)
            startActivity(intent)
        }

        // Handle membership view click
        val membershipView = view.findViewById<RelativeLayout>(R.id.membership)
        membershipView.setOnClickListener {
            val intent = Intent(context, MembershipActivity::class.java)
            startActivity(intent)
        }

        // Handle payment view click
        val paymentView = view.findViewById<RelativeLayout>(R.id.payment)
        paymentView.setOnClickListener {
            val intent = Intent(context, PaymentActivity::class.java)
            startActivity(intent)
        }

        val logoutView = view.findViewById<RelativeLayout>(R.id.logout_button)
        logoutView.setOnClickListener {
            performLogout()
        }

        return view // Ensure this is here
    }

    private fun performLogout() {
        auth.signOut() // Sign out from Firebase
        Toast.makeText(context, "Logged out successfully.", Toast.LENGTH_SHORT).show()

        // Navigate to LoginActivity
        val intent = Intent(activity, LoginActivity::class.java)
        startActivity(intent)
        requireActivity().finish()
    }

    // Function to load profile data (name and image)
    private fun loadProfileData() {
        val userId = auth.currentUser?.uid
        if (userId != null) {
            profileListener = firestore.collection("users").document(userId)
                .addSnapshotListener { document, error ->
                    if (error != null) {
                        Toast.makeText(requireContext(), "Failed to load profile data", Toast.LENGTH_SHORT).show()
                        return@addSnapshotListener
                    }
                    if (document != null && document.exists()) {
                        // Retrieve the first name and last name from Firestore and set it to ProfileName
                        val firstName = document.getString("firstName") ?: "Unknown"
                        val lastName = document.getString("lastName") ?: "User"
                        ProfileName.text = "$firstName $lastName"

                        // Load the profile image if available
                        val imageUrl = document.getString("profileImageUrl")
                        if (!imageUrl.isNullOrEmpty()) {
                            Glide.with(this)
                                .load(imageUrl)
                                .placeholder(R.drawable.user3) // Add a placeholder image
                                .into(profileImage)
                        }
                    }
                }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        // Check if the request is successful and data is not null
        if (resultCode == Activity.RESULT_OK && data != null && data.data != null) {
            selectedImg = data.data!!

            // Set the selected image to the profileImage
            profileImage.setImageURI(selectedImg)

            // Upload the selected image to Firebase Storage
            uploadImageToFirebaseStorage()
        }
    }

    private fun uploadImageToFirebaseStorage() {
        // Show a dialog for uploading
        dialog = AlertDialog.Builder(requireContext())
            .setMessage("Uploading Profile Image...")
            .setCancelable(false)
            .create()
        dialog.show()

        // Create a reference for the image in Firebase Storage
        val reference = storage.reference.child("profileImages")
            .child(auth.currentUser?.uid + "_" + Date().time.toString())

        // Start the upload task
        reference.putFile(selectedImg)
            .addOnSuccessListener {
                // On success, get the download URL and save it
                reference.downloadUrl.addOnSuccessListener { uri ->
                    saveImageUrlToFirestore(uri.toString())
                }
            }
            .addOnFailureListener {
                dialog.dismiss()
                Toast.makeText(requireContext(), "Failed to upload image", Toast.LENGTH_SHORT).show()
            }
    }

    private fun saveImageUrlToFirestore(imageUrl: String) {
        // Save the image URL to Firestore
        val userId = auth.currentUser?.uid
        if (userId != null) {
            firestore.collection("users").document(userId)
                .update("profileImageUrl", imageUrl)
                .addOnSuccessListener {
                    dialog.dismiss()
                    Toast.makeText(requireContext(), "Profile Image Updated", Toast.LENGTH_SHORT).show()
                }
                .addOnFailureListener {
                    dialog.dismiss()
                    Toast.makeText(requireContext(), "Failed to update profile", Toast.LENGTH_SHORT).show()
                }
        }
    }

    override fun onStart() {
        super.onStart()
        // Start listening for real-time updates when the fragment becomes visible
        loadProfileData()
    }

    override fun onStop() {
        super.onStop()
        // Remove the real-time listener when the fragment is stopped to avoid memory leaks
        profileListener?.remove()
    }
}
