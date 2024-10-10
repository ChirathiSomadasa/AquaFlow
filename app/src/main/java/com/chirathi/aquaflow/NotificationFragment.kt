package com.chirathi.aquaflow

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.LinearLayoutManager
import com.chirathi.aquaflow.NotificationService.NotificationAdapter
import com.chirathi.aquaflow.NotificationService.NotificationItem
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.Query


class NotificationFragment : Fragment() {

    private lateinit var notificationRecyclerView: RecyclerView
    private lateinit var notificationAdapter: NotificationAdapter
    private lateinit var notificationList: ArrayList<NotificationItem>
    private lateinit var db: FirebaseFirestore
    private lateinit var auth: FirebaseAuth  // Firebase Authentication instance
    private lateinit var userId: String      // To store current user's ID

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val view = inflater.inflate(R.layout.fragment_notification, container, false)

        // Initialize RecyclerView
        notificationRecyclerView = view.findViewById(R.id.recycler_view_notifications)
        notificationRecyclerView.layoutManager = LinearLayoutManager(context)

        // Initialize the list and adapter
        notificationList = ArrayList()
        notificationAdapter = NotificationAdapter(notificationList)
        notificationRecyclerView.adapter = notificationAdapter

        db = FirebaseFirestore.getInstance()   // Initialize Firestore
        auth = FirebaseAuth.getInstance()

        // Get the current user's ID
        userId = auth.currentUser?.uid ?: ""  // Retrieve the user ID from FirebaseAuth

        if (userId.isNotEmpty()) {
            // Fetch notifications from the current user's subcollection "notifications"
            db.collection("users").document(userId).collection("notifications")
                .orderBy("timestamp", Query.Direction.DESCENDING)
                .get()
                .addOnSuccessListener { documents ->
                    for (document in documents) {
                        val title = document.getString("title") ?: "No Title"
                        val message = document.getString("body") ?: "No Message"
                        val timestamp = document.getString("timestamp") ?: "Now"
                        val notificationItem = NotificationItem(title, message, timestamp)
                        notificationList.add(notificationItem)
                    }
                    notificationAdapter.notifyDataSetChanged()
                }
                .addOnFailureListener { e ->
                    // Handle failure
                }
        } else {
            // Handle the case where userId is null or empty
        }

        // Fetch notifications from Firestore and order by time (newest first)
        /*db.collection("notifications")
            .orderBy("timestamp", Query.Direction.DESCENDING)
            .get()
            .addOnSuccessListener { documents ->
                for (document in documents) {
                    val title = document.getString("title") ?: "No Title"
                    val message = document.getString("body") ?: "No Message"
                    val timestamp = document.getString("timestamp") ?: "Now"
                    val notificationItem = NotificationItem(title, message, timestamp)
                    notificationList.add(notificationItem)
                }
                notificationAdapter.notifyDataSetChanged()
            }
            .addOnFailureListener { e ->
                // Handle failure
            }*/



        return view
    }


}
