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
import com.google.firebase.firestore.Query


class NotificationFragment : Fragment() {

    private lateinit var notificationRecyclerView: RecyclerView
    private lateinit var notificationAdapter: NotificationAdapter
    private lateinit var notificationList: ArrayList<NotificationItem>
    private lateinit var db: FirebaseFirestore

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

        // Fetch notifications from Firestore and order by time (newest first)
        db.collection("notifications")
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

        setupExpandCollapse(view)// Set up expand/collapse functionality

        return view
    }

    private fun setupExpandCollapse(view: View) {
        val expandCollapseBtn = view.findViewById<ImageView>(R.id.btnExpandCollapse)
        val expandedLayout = view.findViewById<LinearLayout>(R.id.expandedLayout)

        expandCollapseBtn.setOnClickListener {
            if (expandedLayout.visibility == View.GONE) {
                expandedLayout.visibility = View.VISIBLE
                expandCollapseBtn.rotation = 180f  // Rotate arrow downwards
            } else {
                expandedLayout.visibility = View.GONE
                expandCollapseBtn.rotation = 0f  // Rotate arrow upwards
            }
        }
    }

}
