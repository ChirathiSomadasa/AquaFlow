package com.chirathi.aquaflow

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class Onboarding3Activity : AppCompatActivity() {

    private lateinit var onboardingImg3: ImageView
    private lateinit var topic3: TextView
    private lateinit var description3: TextView
    private lateinit var nextButton: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_onboarding3)

        onboardingImg3 = findViewById(R.id.onboardingimg3)
        topic3 = findViewById(R.id.topic3)
        description3 = findViewById(R.id.description3)
        nextButton = findViewById(R.id.btn_next3)

        fadeInAndSlideIn(onboardingImg3, topic3, description3)

        nextButton.setOnClickListener {
            fadeOut(onboardingImg3, topic3, description3) {
                val intent = Intent(this@Onboarding3Activity, CreateAccountActivity::class.java)
                startActivity(intent)
                // Optional: add a transition animation
                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
            }
        }
    }

    private fun fadeInAndSlideIn(imageView: ImageView, topicTextView: TextView, descTextView: TextView) {
        imageView.alpha = 0f
        imageView.animate().alpha(1f).setDuration(2000).start()

        topicTextView.translationY = 100f
        topicTextView.alpha = 0f
        topicTextView.animate().translationY(0f).alpha(1f).setDuration(1000).start()

        descTextView.translationY = 100f
        descTextView.alpha = 0f
        descTextView.animate().translationY(0f).alpha(1f).setDuration(1000).start()
    }

    private fun fadeOut(imageView: ImageView, topicTextView: TextView, descTextView: TextView, onEnd: () -> Unit) {
        val fadeDuration = 1000L

        imageView.animate().alpha(0f).setDuration(fadeDuration).start()

        topicTextView.animate().alpha(0f).setDuration(fadeDuration).start()

        descTextView.animate().alpha(0f).setDuration(fadeDuration).withEndAction {
            onEnd() // Call the onEnd callback once the fade-out is complete
        }.start()
    }
}
