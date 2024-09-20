package com.chirathi.aquaflow

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.ImageView

class QRCodeActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_qrcode)

        val back= findViewById<ImageView>(R.id.backbtn)
        back.setOnClickListener {
            finish()
        }

        val createAccount = findViewById<Button>(R.id.ok_button)
        createAccount.setOnClickListener {
            finish()
        }


    }
}