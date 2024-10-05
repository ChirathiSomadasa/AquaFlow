package com.chirathi.aquaflow

import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AlertDialog

class PaymentProcessActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_payment_process)

        val back= findViewById<ImageView>(R.id.backbtn)
        back.setOnClickListener {
            finish()
        }

        val submitBtn: Button = findViewById(R.id.submit_btn)

        submitBtn.setOnClickListener {
            // Create an AlertDialog.Builder
            val builder = AlertDialog.Builder(this)

            // Inflate the custom layout
            val inflater = layoutInflater
            val dialogView = inflater.inflate(R.layout.custom_submit, null)

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
            }

            // Show the dialog
            dialog.show()
        }

    }
}