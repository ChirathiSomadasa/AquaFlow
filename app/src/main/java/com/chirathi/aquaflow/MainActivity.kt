package com.chirathi.aquaflow

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
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
            WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN
        )

        setContentView(R.layout.activity_main)

        // Initialize views
        logo = findViewById(R.id.imageView)
        title = findViewById(R.id.tvTitle)
        subtitle = findViewById(R.id.tvSubTitle)

        // Initially hide the title and subtitle
        title.alpha = 0f
        subtitle.alpha = 0f

        // Start the animations
        startAnimations()
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
                // After the logo animation, start title and subtitle animations
                fadeInTitleAndSubtitle()
            }
        })
        logoAnimatorSet.start()
    }

    private fun fadeInTitleAndSubtitle() {
        // Animate title and subtitle fading in
        val fadeInTitle = ObjectAnimator.ofFloat(title, "alpha", 0f, 1f)
        val fadeInSubtitle = ObjectAnimator.ofFloat(subtitle, "alpha", 0f, 1f)
        fadeInTitle.duration = 1000
        fadeInSubtitle.duration = 1000

        // Set up the animator set for title and subtitle
        val titleSubtitleAnimatorSet = AnimatorSet()
        titleSubtitleAnimatorSet.playTogether(fadeInTitle, fadeInSubtitle)
        titleSubtitleAnimatorSet.start()

        // Navigate to the next activity after a delay to allow titles to be visible
        titleSubtitleAnimatorSet.addListener(object : AnimatorListenerAdapter() {
            override fun onAnimationEnd(animation: Animator) {
                // Delay before navigating to the next activity
                Handler(Looper.getMainLooper()).postDelayed({
                    navigateToNextActivity()
                }, 2000) // Adjust delay if necessary
            }
        })
    }

    private fun navigateToNextActivity() {
        val intent = if (getOnboardingFlagOnSharedPreferences()) {
            Intent(this, LoginActivity::class.java)
        } else {
            Intent(this, Onboarding1Activity::class.java)
        }
        startActivity(intent)
        finish() // Optionally finish this activity
    }

    private fun getOnboardingFlagOnSharedPreferences(): Boolean {
        val sharedPreferences = getSharedPreferences("AquaFlow", MODE_PRIVATE)
        return sharedPreferences.getBoolean("onBoardingFlag", false)
    }
}
