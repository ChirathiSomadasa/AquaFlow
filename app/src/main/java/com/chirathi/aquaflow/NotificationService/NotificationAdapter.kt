package com.chirathi.aquaflow.NotificationService

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.chirathi.aquaflow.NotificationFragment
import com.chirathi.aquaflow.R

data class NotificationItem(
    val notificationId: String,
    val title: String,
    val message: String,
    val timestamp: String,
    var isRead: Boolean
)

class NotificationAdapter(
    private val notificationList: List<NotificationItem>,
    private val fragment: NotificationFragment
) : RecyclerView.Adapter<NotificationAdapter.NotificationViewHolder>() {


    class NotificationViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val titleTextView: TextView = itemView.findViewById(R.id.notification_title)
        val titleTextView2: TextView = itemView.findViewById(R.id.notification_title2)
        val messageTextView: TextView = itemView.findViewById(R.id.notification_message)
        val timestampTextView: TextView = itemView.findViewById(R.id.tvNotificationTime) // Reference to timestamp TextView
        val expandCollapseBtn: ImageView = itemView.findViewById(R.id.btnExpandCollapse) // Expand/Collapse Button
        val expandedLayout: LinearLayout = itemView.findViewById(R.id.expandedLayout) // Expanded layout that will be shown/hidden
        val notificationCard: LinearLayout = itemView.findViewById(R.id.main_notification_card)

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


        // Set background color based on read status
        if (currentItem.isRead) {
            holder.notificationCard.setBackgroundResource(R.drawable.notification_read)
        } else {
            holder.notificationCard.setBackgroundResource(R.drawable.notification_unread)
        }

        // Handle click to mark notification as read
//        holder.notificationCard.setOnClickListener {
//            if (!currentItem.isRead) {
//                fragment.markNotificationAsRead(currentItem.notificationId) // Call method to mark as read
//                currentItem.isRead = true // Update the notification object
//                notifyItemChanged(position) // Notify the adapter to refresh this item
//            }
//        }



        // Initialize the expanded state (collapsed by default)
        holder.expandedLayout.visibility = View.GONE

        // Set click listener for expand/collapse button
        holder.expandCollapseBtn.setOnClickListener {
            if (holder.expandedLayout.visibility == View.GONE) {
                // Expand the layout
                holder.expandedLayout.visibility = View.VISIBLE
                holder.expandCollapseBtn.rotation = 180f  // Rotate the arrow down


                // Handle click to mark notification as read
                holder.notificationCard.setBackgroundResource(R.drawable.notification_read)




            } else {
                // Collapse the layout
                holder.expandedLayout.visibility = View.GONE
                holder.expandCollapseBtn.rotation = 0f  // Rotate the arrow up
                if (!currentItem.isRead) {
                    fragment.markNotificationAsRead(currentItem.notificationId) // Call method to mark as read
                    currentItem.isRead = true // Update the notification object
                    notifyItemChanged(position) // Notify the adapter to refresh this item
                }
            }
        }
    }

    override fun getItemCount(): Int {
        return notificationList.size
    }
}
