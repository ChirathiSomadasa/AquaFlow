package com.chirathi.aquaflow.NotificationType

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ImageView
import androidx.fragment.app.Fragment
import com.chirathi.aquaflow.R

class UrgentNoticeActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_urgent_notice)

        val back= findViewById<ImageView>(R.id.backbtn)
        back.setOnClickListener {
            finish()
        }

        // Set the initial fragment
        if (savedInstanceState == null) {
            replaceFragment(UrgentNoticeFragment())
        }


    }
    fun replaceFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction()
            .replace(R.id.UrgentNotice_fragment_container, fragment)
            .commit()
    }


}