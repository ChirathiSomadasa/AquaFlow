package com.chirathi.aquaflow.NotificationType

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ImageView
import androidx.fragment.app.Fragment
import com.chirathi.aquaflow.R

class ReminderNoticeActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_reminder_notice)

        val back= findViewById<ImageView>(R.id.backbtn)
        back.setOnClickListener {
            finish()
        }

        // Set the initial fragment
        if (savedInstanceState == null) {
            replaceFragment(ReminderNoticeFragment())
        }


    }
    fun replaceFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction()
            .replace(R.id.ReminderNotice_fragment_container, fragment)
            .commit()
    }
}