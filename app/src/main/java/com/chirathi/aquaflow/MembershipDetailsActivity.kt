package com.chirathi.aquaflow

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AlertDialog
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class MembershipDetailsActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_membership_details)

        val next = findViewById<Button>(R.id.collect_button)
        next.setOnClickListener {
            val intent = Intent(this, MembershipDetailsActivity::class.java)
            startActivity(intent)
        }

        val back= findViewById<ImageView>(R.id.backbtn)
        back.setOnClickListener {
            finish()
        }

        val collectButton: Button = findViewById(R.id.collect_button)

        collectButton.setOnClickListener {
            // Create an AlertDialog.Builder
            val builder = AlertDialog.Builder(this)

            // Inflate the custom layout
            val inflater = layoutInflater
            val dialogView = inflater.inflate(R.layout.custome_dialog_congrats, null)

            // Set the custom layout to the dialog
            builder.setView(dialogView)

            // Create the dialog instance first
            val dialog: AlertDialog = builder.create()

            // Get the views from the custom layout if needed
            val dialogImage: ImageView = dialogView.findViewById(R.id.dialogimage)
            val dialogMessage: TextView = dialogView.findViewById(R.id.dialog)
            val dialogButton: Button = dialogView.findViewById(R.id.dialog_button)

            // Set an onClickListener to the button in the custom layout
            dialogButton.setOnClickListener {
                // Dismiss the dialog when button is clicked
                dialog.dismiss()  // Now you can call dismiss() on the dialog object

                val intent = Intent(this, PaymentActivity::class.java)
                startActivity(intent)
            }

            // Show the dialog
            dialog.show()
        }


    }

}