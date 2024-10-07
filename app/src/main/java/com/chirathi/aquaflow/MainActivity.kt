package com.chirathi.aquaflow

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.Window
import android.view.WindowManager

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        window.setFlags(
            // Set the window to fullscreen
            WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN
        )
        requestWindowFeature(Window.FEATURE_NO_TITLE)// Request to hide the title bar

        setContentView(R.layout.activity_main)

        /*Handler(Looper.getMainLooper()).postDelayed({
            if(getOnboardingFlagOnSharedPreferences()){
                val intent = Intent(this, CreateAccountActivity::class.java)
                startActivity(intent)
            }else{
                val intent = Intent(this, Onboarding1Activity::class.java)
                startActivity(intent)
            }
        },2000)*/

        Handler(Looper.getMainLooper()).postDelayed({
            if(getOnboardingFlagOnSharedPreferences()){
                val intent = Intent(this, LoginActivity::class.java)
                startActivity(intent)
            }else{
                val intent = Intent(this, Onboarding1Activity::class.java)
                startActivity(intent)
            }
        },2000)

    }

    private fun getOnboardingFlagOnSharedPreferences(): Boolean {
        val sharedPreferences = getSharedPreferences("AquaFlow", MODE_PRIVATE)
        return sharedPreferences.getBoolean("onBoardingFlag", false)
    }

}