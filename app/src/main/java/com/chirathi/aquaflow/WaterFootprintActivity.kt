package com.chirathi.aquaflow

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.RadioButton
import android.widget.RadioGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.firestore.FirebaseFirestore

class WaterFootprintActivity : AppCompatActivity() {
    private lateinit var firestore: FirebaseFirestore


    override fun onCreate(savedInstanceState: Bundle?) {
        setContentView(R.layout.activity_water_footprint)
        super.onCreate(savedInstanceState)


        val inputShowersPerDay = findViewById<EditText>(R.id.inputShowersPerDay)
        val inputLengthOfShower = findViewById<EditText>(R.id.inputLengthOfShower)
        val inputLaundryPerWeek = findViewById<EditText>(R.id.inputLaundryPerWeek)
        val radioGroupShowerHead = findViewById<RadioGroup>(R.id.radioGroupshowerHead)
        val inputBrushTeethWashHands = findViewById<EditText>(R.id.inputdetails)
        val radioGroupTapRunning = findViewById<RadioGroup>(R.id.radioGroupTapRunning)
        val inputLaundryLoads = findViewById<EditText>(R.id.inputlaundrydetails)
        val radioGroupToilet = findViewById<RadioGroup>(R.id.radioGroupToilet)
        val inputDishesWashed = findViewById<EditText>(R.id.inputdisheswashed)
        val inputWashTime = findViewById<EditText>(R.id.inputwash)
        val btnCalculateFootprint = findViewById<Button>(R.id.btnCalculateFootprint)

        // Initialize Firestore
        firestore = FirebaseFirestore.getInstance()

        // Handle Calculate button click
        btnCalculateFootprint.setOnClickListener {
            // Collect inputs from user
            val showersPerDay = inputShowersPerDay.text.toString().toIntOrNull() ?: 0
            val lengthOfShower = inputLengthOfShower.text.toString().toIntOrNull() ?: 0
            val laundryPerWeek = inputLaundryPerWeek.text.toString().toIntOrNull() ?: 0
            val showerHeadType =
                findViewById<RadioButton>(radioGroupShowerHead.checkedRadioButtonId)?.text.toString()
            val brushTeethPerDay = inputBrushTeethWashHands.text.toString().toIntOrNull() ?: 0
            val tapRunning =
                findViewById<RadioButton>(radioGroupTapRunning.checkedRadioButtonId)?.text.toString()
            val laundryLoads = inputLaundryLoads.text.toString().toIntOrNull() ?: 0
            val toiletType =
                findViewById<RadioButton>(radioGroupToilet.checkedRadioButtonId)?.text.toString()
            val dishesWashed = inputDishesWashed.text.toString().toIntOrNull() ?: 0
            val washTime = inputWashTime.text.toString().toIntOrNull() ?: 0

            // Validate input
            if (showersPerDay == 0 || lengthOfShower == 0 || laundryPerWeek == 0) {
                Toast.makeText(this, "Please fill out all fields", Toast.LENGTH_SHORT).show()
            } else {
                // Call function to calculate and save footprint data
                calculateAndSaveFootprint(
                    showersPerDay, lengthOfShower, laundryPerWeek,
                    showerHeadType, brushTeethPerDay, tapRunning,
                    laundryLoads, toiletType, dishesWashed, washTime
                )
            }
        }
    }

            private fun calculateAndSaveFootprint(
                showersPerDay: Int, lengthOfShower: Int, laundryPerWeek: Int,
                showerHeadType: String?, brushTeethPerDay: Int, tapRunning: String?,
                laundryLoads: Int, toiletType: String?, dishesWashed: Int, washTime: Int
            ) {
                // Fetch average usage data from Firestore
                firestore.collection("average_usage")
                    .document("area_specific_data")
                    .get()
                    .addOnSuccessListener { document ->
                        val avgShowers = document.getDouble("showers") ?: 0.0
                        val avgShowerLength = document.getDouble("showerLength") ?: 0.0
                        val avgLaundry = document.getDouble("laundry") ?: 0.0

                        val intent = Intent(this, FootprintResultActivity::class.java)
                        intent.putExtra("showersPerDay", showersPerDay)
                        intent.putExtra("lengthOfShower", lengthOfShower)
                        intent.putExtra("laundryPerWeek", laundryPerWeek)
                        intent.putExtra("showerHeadType", showerHeadType)
                        intent.putExtra("brushTeethPerDay", brushTeethPerDay)
                        intent.putExtra("tapRunning", tapRunning)
                        intent.putExtra("laundryLoads", laundryLoads)
                        intent.putExtra("toiletType", toiletType)
                        intent.putExtra("dishesWashed", dishesWashed)
                        intent.putExtra("washTime", washTime)
                        intent.putExtra("avgShowers", avgShowers)
                        intent.putExtra("avgShowerLength", avgShowerLength)
                        intent.putExtra("avgLaundry", avgLaundry)

                        // Start the result activity
                        startActivity(intent)

                    }
                    .addOnFailureListener {
                        Toast.makeText(
                            this,
                            "Error fetching data from Firestore",
                            Toast.LENGTH_SHORT
                        ).show()
                    }

            }
        }

