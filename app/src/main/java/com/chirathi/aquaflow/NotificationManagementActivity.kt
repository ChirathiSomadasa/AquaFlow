package com.chirathi.aquaflow

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import androidx.core.text.HtmlCompat
import com.chirathi.aquaflow.NotificationType.ReminderNoticeActivity
import com.chirathi.aquaflow.NotificationType.UrgentNoticeActivity

class NotificationManagementActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_notification_management)

        val back= findViewById<ImageView>(R.id.backbtn)
        back.setOnClickListener {
            finish()
        }

        // Find the Button and set the HTML formatted text
        val btnReminder = findViewById<Button>(R.id.btnReminder) // find the button by ID
        btnReminder.text = HtmlCompat.fromHtml(getString(R.string.reminder_water_supply), HtmlCompat.FROM_HTML_MODE_LEGACY)

        btnReminder.setOnClickListener{
            val intent = Intent(this, ReminderNoticeActivity::class.java)
            intent.putExtra("notice_type", "Reminder")
            startActivity(intent)
        }



        // Set the Urgent Notice button text
        val btnUrgentNotice = findViewById<Button>(R.id.btnUrgentNotice) // Assuming there's a separate button
        btnUrgentNotice.text = HtmlCompat.fromHtml(getString(R.string.urgent_notice_water_supply_disruption), HtmlCompat.FROM_HTML_MODE_LEGACY)

        btnUrgentNotice.setOnClickListener{
            val intent = Intent(this, UrgentNoticeActivity::class.java)
            intent.putExtra("notice_type", "Urgent Notice")
            startActivity(intent)
        }
    }
}