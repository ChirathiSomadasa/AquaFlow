package com.chirathi.aquaflow

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class Onboarding1Activity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_onboarding1)


        val onboardingImg1 = findViewById<ImageView>(R.id.onboardingimg1)
        val topic1 = findViewById<TextView>(R.id.topic1)
        val description1 = findViewById<TextView>(R.id.description1)
        val nextButton = findViewById<Button>(R.id.btn_next1)


        fadeInAndSlideIn(onboardingImg1, topic1, description1)


        nextButton.setOnClickListener {

            fadeOut(onboardingImg1, topic1, description1) {

                val intent = Intent(this@Onboarding1Activity, Onboarding2Activity::class.java)
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
