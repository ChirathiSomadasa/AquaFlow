package com.chirathi.aquaflow.NotificationService

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.chirathi.aquaflow.NotificationFragment
import com.chirathi.aquaflow.R

data class NotificationItem(
    val notificationId: String,
    val title: String,
    val message: String,
    val timestamp: String,
    val isRead: Boolean = false
)

class NotificationAdapter(private val notificationList: List<NotificationItem>) : RecyclerView.Adapter<NotificationAdapter.NotificationViewHolder>() {

    class NotificationViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val titleTextView: TextView = itemView.findViewById(R.id.notification_title)
        val titleTextView2: TextView = itemView.findViewById(R.id.notification_title2)
        val messageTextView: TextView = itemView.findViewById(R.id.notification_message)
        val timestampTextView: TextView = itemView.findViewById(R.id.tvNotificationTime) // Reference to timestamp TextView
        val expandCollapseBtn: ImageView = itemView.findViewById(R.id.btnExpandCollapse) // Expand/Collapse Button
        val expandedLayout: LinearLayout = itemView.findViewById(R.id.expandedLayout) // Expanded layout that will be shown/hidden
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NotificationViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.notification_item, parent, false)
        return NotificationViewHolder(view)
    }

    override fun onBindViewHolder(holder: NotificationViewHolder, position: Int) {
        val currentItem = notificationList[position]
        holder.titleTextView.text = currentItem.title
        holder.titleTextView2.text = currentItem.title
        holder.messageTextView.text = currentItem.message
        holder.timestampTextView.text = currentItem.timestamp // Set the timestamp


        // Change appearance if notification is read
        if (currentItem.isRead) {
            holder.itemView.alpha = 0.5f // Dim the view to show it has been read
        } else {
            holder.itemView.alpha = 1.0f // Full opacity if unread
        }

        // Handle notification click
        holder.itemView.setOnClickListener {
            // Mark the notification as read in Firestore
            (holder.itemView.context as? NotificationFragment)?.markNotificationAsRead(currentItem.notificationId)

            // Optional: Change the UI to reflect the read status immediately
            holder.itemView.alpha = 0.5f
        }



        // Initialize the expanded state (collapsed by default)
        holder.expandedLayout.visibility = View.GONE

        // Set click listener for expand/collapse button
        holder.expandCollapseBtn.setOnClickListener {
            if (holder.expandedLayout.visibility == View.GONE) {
                // Expand the layout
                holder.expandedLayout.visibility = View.VISIBLE
                holder.expandCollapseBtn.rotation = 180f  // Rotate the arrow down
            } else {
                // Collapse the layout
                holder.expandedLayout.visibility = View.GONE
                holder.expandCollapseBtn.rotation = 0f  // Rotate the arrow up
            }
        }
    }

    override fun getItemCount(): Int {
        return notificationList.size
    }
}
