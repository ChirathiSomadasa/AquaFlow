package com.chirathi.aquaflow

import android.os.Bundle
import android.util.Log
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
        notificationAdapter = NotificationAdapter(notificationList, this)
        notificationRecyclerView.adapter = notificationAdapter

        db = FirebaseFirestore.getInstance()   // Initialize Firestore
        auth = FirebaseAuth.getInstance()
        userId = auth.currentUser?.uid ?: ""  // Get the current user's ID from FirebaseAuth


        if (userId.isNotEmpty()) {
            // Fetch notifications from the current user's subcollection "notifications"
            db.collection("users").document(userId).collection("notifications")
                .orderBy("timestamp", Query.Direction.DESCENDING)
                .get()
                .addOnSuccessListener { documents ->
                    for (document in documents) {
                        val notificationId = document.id
                        val title = document.getString("title") ?: "No Title"
                        val message = document.getString("body") ?: "No Message"
                        val timestamp = document.getString("timestamp") ?: "Now"
                        val isRead = document.getBoolean("isRead") ?: false // Get isRead field


                        val notificationItem = NotificationItem(notificationId, title, message, timestamp, isRead)
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

        return view
    }
    fun markNotificationAsRead(notificationId: String) {
        db.collection("users").document(userId).collection("notifications")
            .document(notificationId)
            .update("isRead", true)  // Mark the notification as read
            .addOnSuccessListener {
                Log.d("NotificationFragment", "Notification marked as read")
            }
            .addOnFailureListener { exception ->
                Log.e("NotificationFragment", "Error updating notification: ${exception.message}")
            }
    }
    fun deleteNotification(notificationId: String, position: Int) {
        db.collection("users").document(userId).collection("notifications")
            .document(notificationId)
            .delete()
            .addOnSuccessListener {
                Log.d("NotificationFragment", "Notification deleted successfully")

                // Remove the notification from the list and update the adapter
                notificationList.removeAt(position)
                notificationAdapter.notifyItemRemoved(position)
            }
            .addOnFailureListener { exception ->
                Log.e("NotificationFragment", "Error deleting notification: ${exception.message}")
            }
    }



}
