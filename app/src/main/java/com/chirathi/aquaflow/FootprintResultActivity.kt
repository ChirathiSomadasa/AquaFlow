package com.chirathi.aquaflow

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity

class FootprintResultActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_footprint_result)
        val q1 = findViewById<Button>(R.id.q1)
        val q2 = findViewById<Button>(R.id.q2)
        val q3 = findViewById<Button>(R.id.q3)
        val q4 = findViewById<Button>(R.id.q4)
        val q5 = findViewById<Button>(R.id.q5)
        val q6 = findViewById<Button>(R.id.q6)
        val q7 = findViewById<Button>(R.id.q7)
        val q8 = findViewById<Button>(R.id.q8)
        val q9 = findViewById<Button>(R.id.q9)
        val q10 = findViewById<Button>(R.id.q10)
        val q11 = findViewById<Button>(R.id.q11)
        val backToHomeButton = findViewById<Button>(R.id.backToHomeButton)

        backToHomeButton.setOnClickListener {
            startActivity(Intent(this, HomeActivity::class.java))
        }

            q1.setOnClickListener {
                startActivity(Intent(this, RecommendationsActivity::class.java))
            }

            q2.setOnClickListener {
                startActivity(Intent(this, RecommendationsActivity::class.java))
            }

            q3.setOnClickListener {
                startActivity(Intent(this, RecommendationsActivity::class.java))
            }

            q4.setOnClickListener {
                startActivity(Intent(this, RecommendationsActivity::class.java))
            }

            q5.setOnClickListener {
                startActivity(Intent(this, RecommendationsActivity::class.java))
            }

            q6.setOnClickListener {
                startActivity(Intent(this, RecommendationsActivity::class.java))
            }

            q7.setOnClickListener {
                startActivity(Intent(this, RecommendationsActivity::class.java))
            }

            q8.setOnClickListener {
                startActivity(Intent(this, RecommendationsActivity::class.java))
            }

            q9.setOnClickListener {
                startActivity(Intent(this, RecommendationsActivity::class.java))
            }

            q10.setOnClickListener {
                startActivity(Intent(this, RecommendationsActivity::class.java))
            }

            q11.setOnClickListener {
                startActivity(Intent(this, RecommendationsActivity::class.java))
            }

        }
    }
