package com.chirathi.aquaflow

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.Window
import android.view.WindowManager
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {
    private lateinit var logo: ImageView
    private lateinit var title: TextView
    private lateinit var subtitle: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        window.setFlags(
            // Set the window to fullscreen
            WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN
        )
        requestWindowFeature(Window.FEATURE_NO_TITLE) // Request to hide the title bar

        setContentView(R.layout.activity_main)


        Handler(Looper.getMainLooper()).postDelayed({
            if(getOnboardingFlagOnSharedPreferences()){
                val intent = Intent(this, CreateAccountActivity::class.java)
                startActivity(intent)
            }else{
                val intent = Intent(this, Onboarding1Activity::class.java)
                startActivity(intent)
            }
        },2000)

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


        // Initialize views
        logo = findViewById(R.id.imageView) // Ensure this ID matches your XML
        title = findViewById(R.id.tvTitle)
        subtitle = findViewById(R.id.tvSubTitle)

        // Initially hide the title and subtitle
        title.alpha = 0f
        subtitle.alpha = 0f

        // Start the animations
        startAnimations()

        // Navigate to the next activity after a delay
        Handler(Looper.getMainLooper()).postDelayed({
            val intent = Intent(this, Onboarding1Activity::class.java)
            startActivity(intent)
            finish() // Optionally finish this activity
        }, 5000) // Adjust the delay to match your animation duration
    }

    private fun startAnimations() {
        // Animate logo appearing and moving up
        val fadeInLogo = ObjectAnimator.ofFloat(logo, "alpha", 0f, 1f)
        val moveUpLogo = ObjectAnimator.ofFloat(logo, "translationY", 100f, 0f)
        fadeInLogo.duration = 1000
        moveUpLogo.duration = 1000

        // Set up the animator set for logo
        val logoAnimatorSet = AnimatorSet()
        logoAnimatorSet.playSequentially(fadeInLogo, moveUpLogo)
        logoAnimatorSet.addListener(object : AnimatorListenerAdapter() {
            override fun onAnimationEnd(animation: Animator) {
                // Now animate the title and subtitle
                fadeInTitleAndSubtitle()
            }
        })
        logoAnimatorSet.start()
    }

    private fun fadeInTitleAndSubtitle() {
        val fadeInTitle = ObjectAnimator.ofFloat(title, "alpha", 0f, 1f)
        val fadeInSubtitle = ObjectAnimator.ofFloat(subtitle, "alpha", 0f, 1f)
        fadeInTitle.duration = 1000
        fadeInSubtitle.duration = 1000

        // Start title and subtitle animations together
        val titleSubtitleAnimatorSet = AnimatorSet()
        titleSubtitleAnimatorSet.playTogether(fadeInTitle, fadeInSubtitle)
        titleSubtitleAnimatorSet.start()
    }
}


