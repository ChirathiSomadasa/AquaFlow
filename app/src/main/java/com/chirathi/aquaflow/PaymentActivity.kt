package com.chirathi.aquaflow

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.google.android.material.bottomnavigation.BottomNavigationView
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class PaymentActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_payment)

        val navigationView = findViewById<BottomNavigationView>(R.id.bottom_nav_view)
        navigationView.setOnItemSelectedListener  { menuItem ->
            when (menuItem.itemId) {
                R.id.fragment_home -> {
                    replaceFragment(HomeFragment())
                    return@setOnItemSelectedListener true
                }
                R.id.fragment_notification -> {
                    replaceFragment(NotificationFragment())
                    return@setOnItemSelectedListener true
                }
                R.id.fragment_profile -> {
                    replaceFragment(ProfileFragment())
                    return@setOnItemSelectedListener true
                }
                else -> false
            }
        }

    }

    private fun replaceFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, fragment)
            .commit()
    }
}