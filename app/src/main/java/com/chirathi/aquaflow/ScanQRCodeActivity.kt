package com.chirathi.aquaflow

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import android.widget.Toast
import com.google.zxing.integration.android.IntentIntegrator
import com.google.zxing.integration.android.IntentResult

class ScanQRCodeActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_scan_qrcode)

        val back= findViewById<ImageView>(R.id.backbtn)
        back.setOnClickListener {
            finish()
        }

        // Scan button listener
        val scanButton = findViewById<Button>(R.id.scan_button) // Updated to match the button ID in the XML
        scanButton.setOnClickListener {
            initiateQRCodeScan()
        }
     }

    // Function to start QR code scan
    private fun initiateQRCodeScan() {
        val qrScanner = IntentIntegrator(this)
        qrScanner.setDesiredBarcodeFormats(IntentIntegrator.QR_CODE)
        qrScanner.setPrompt("Scan a QR code")
        qrScanner.setBeepEnabled(true)
        qrScanner.setOrientationLocked(false)
        qrScanner.initiateScan()
    }

    // Handle QR code scan result
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        val result: IntentResult = IntentIntegrator.parseActivityResult(requestCode, resultCode, data)
        if (result != null) {
            if (result.contents == null) {
                // If QR scan was cancelled
                Toast.makeText(this, "Scan cancelled", Toast.LENGTH_SHORT).show()
            } else {
                // If QR scan was successful, handle the result
                val scannedData = result.contents
                Toast.makeText(this, "Scanned: $scannedData", Toast.LENGTH_SHORT).show()

                // Navigate to CustomerDetailsActivity with scanned data
                navigateToCustomerDetails(scannedData)
            }
        } else {
            super.onActivityResult(requestCode, resultCode, data)
        }
    }

    // Function to handle the scanned QR data and navigate
    private fun navigateToCustomerDetails(scannedData: String) {
        // Create an Intent to start CustomerDetailsActivity
        val intent = Intent(this, CustomerDetailsActivity::class.java)
        // Pass the scanned data as an extra
        intent.putExtra("CUSTOMER_ID", scannedData)
        // Start the activity
        startActivity(intent)
    }
}
