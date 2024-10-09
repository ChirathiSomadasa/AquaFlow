package com.chirathi.aquaflow

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class Onboarding2Activity : AppCompatActivity() {

    private lateinit var onboardingImg2: ImageView
    private lateinit var topic2: TextView
    private lateinit var description2: TextView
    private lateinit var nextButton: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_onboarding2)

        onboardingImg2 = findViewById(R.id.onboardingimg2)
        topic2 = findViewById(R.id.topic2)
        description2 = findViewById(R.id.description2)
        nextButton = findViewById(R.id.btn_next2)

        fadeInAndSlideIn(onboardingImg2, topic2, description2)

        nextButton.setOnClickListener {
            fadeOut(onboardingImg2, topic2, description2) {
                val intent = Intent(this@Onboarding2Activity, Onboarding3Activity::class.java)
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
