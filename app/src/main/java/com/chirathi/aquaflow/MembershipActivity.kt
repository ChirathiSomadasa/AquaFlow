package com.chirathi.aquaflow

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.widget.Button
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity


class MembershipActivity : AppCompatActivity() {

    private lateinit var progressBar: ProgressBar
    private lateinit var progressText: TextView
    private val handler = Handler(Looper.getMainLooper())
    private var progressStatus = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_membership)

        val next = findViewById<Button>(R.id.continue_button)
        next.setOnClickListener {
            val intent = Intent(this, MembershipDetailsActivity::class.java)
            startActivity(intent)
        }

        val back = findViewById<ImageView>(R.id.backbtn)
        back.setOnClickListener {
            finish()
        }

        progressBar = findViewById(R.id.progressBar)
        progressText = findViewById(R.id.progress_text)

        startProgressBar() // Start the progress bar animation
    }

    private fun startProgressBar() {
        // Set progress and text dynamically over time
        Thread {
            while (progressStatus < 30) {  // Update progress up to 100%
                progressStatus += 1
                handler.post {
                    progressBar.progress = progressStatus
                    progressText.text = "$progressStatus"
                }
                try {
                    // Sleep for 100ms to simulate the time delay (adjust as necessary)
                    Thread.sleep(100)
                } catch (e: InterruptedException) {
                    e.printStackTrace()
                }
            }
        }.start()
    }
}
