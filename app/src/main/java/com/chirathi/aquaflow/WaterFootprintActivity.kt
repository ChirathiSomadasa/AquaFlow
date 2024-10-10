package com.chirathi.aquaflow

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.RadioButton
import android.widget.RadioGroup
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.firestore.FirebaseFirestore

class WaterFootprintActivity : AppCompatActivity() {

    private lateinit var inputShowersPerDay: EditText
    private lateinit var inputLengthOfShower: EditText
    private lateinit var inputHouseUsage: EditText
    private lateinit var inputLaundryDetails: EditText
    private lateinit var inputDishesWashed: EditText
    private lateinit var radioGroupTapRunning: RadioGroup
    private lateinit var radioGroupEvent: RadioGroup
    private lateinit var btnCalculateFootprint: Button
    private lateinit var db: FirebaseFirestore

    private lateinit var amountUsedInput: EditText
    private lateinit var amountUsed: TextView

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_water_footprint)
        val back = findViewById<ImageButton>(R.id.back)

        back.setOnClickListener {
            startActivity(Intent(this, WasteWaterAdviceActivity::class.java))
        }


        db = FirebaseFirestore.getInstance()

        // Initialize UI components
        inputShowersPerDay = findViewById(R.id.inputShowersPerDay)
        inputLengthOfShower = findViewById(R.id.inputLengthOfShower)
        inputHouseUsage = findViewById(R.id.inputhouseusage)
        inputLaundryDetails = findViewById(R.id.inputlaundrydetails)
        inputDishesWashed = findViewById(R.id.inputdisheswashed)
        radioGroupTapRunning = findViewById(R.id.radioGroupTapRunning)
        radioGroupEvent = findViewById(R.id.radioGroupEvent)
        btnCalculateFootprint = findViewById(R.id.btnCalculateFootprint)



        btnCalculateFootprint.setOnClickListener {
            if (validateInputs()) {
                saveDataToFirestore()
            }
        }

        amountUsedInput = findViewById(R.id.amountUsedInput)
        amountUsed = findViewById(R.id.amountUsed)

        radioGroupEvent.setOnCheckedChangeListener { _, checkedId ->
            when (checkedId) {
                R.id.radioTYes -> {
                    amountUsedInput.visibility = View.VISIBLE
                    amountUsed.visibility = View.VISIBLE
                }
                R.id.radioTNo -> {
                    amountUsedInput.visibility = View.GONE
                    amountUsed.visibility = View.GONE
                }
            }
        }
    }

    private fun validateInputs(): Boolean {
        // Get the input values as strings
        val showersPerDay = inputShowersPerDay.text.toString()
        val lengthOfShower = inputLengthOfShower.text.toString()
        val houseUsage = inputHouseUsage.text.toString()
        val laundryDetails = inputLaundryDetails.text.toString()
        val dishesWashed = inputDishesWashed.text.toString()

        // Check if any field is empty
        if (showersPerDay.isEmpty() || lengthOfShower.isEmpty() || houseUsage.isEmpty() ||
             laundryDetails.isEmpty() || dishesWashed.isEmpty()) {
            Toast.makeText(this, "Please fill in all fields.", Toast.LENGTH_SHORT).show()
            return false
        }

        // Check if all inputs are numbers
        if (!isNumeric(showersPerDay) || !isNumeric(lengthOfShower) || !isNumeric(houseUsage) ||
             !isNumeric(laundryDetails) || !isNumeric(dishesWashed)) {
            Toast.makeText(this, "Please enter numbers only.", Toast.LENGTH_SHORT).show()
            return false
        }

        if (radioGroupEvent.checkedRadioButtonId == R.id.radioTYes && amountUsedInput.text.toString().isEmpty()) {
            Toast.makeText(this, "Please enter the amount used for the event.", Toast.LENGTH_SHORT).show()
            return false
        }

        return true
    }

    // Helper function to check if a string is numeric
    private fun isNumeric(str: String): Boolean {
        return str.all { it.isDigit() }
    }

    private fun saveDataToFirestore() {
        val showersPerDay = inputShowersPerDay.text.toString().toInt()
        val lengthOfShower = inputLengthOfShower.text.toString().toInt()
        val houseUsage = inputHouseUsage.text.toString().toInt()
        val laundryDetails = inputLaundryDetails.text.toString().toInt()
        val dishesWashed = inputDishesWashed.text.toString().toInt()

        // Get selected radio button values
        val selectedTapRunningId = radioGroupTapRunning.checkedRadioButtonId
        val radioTapRunning = findViewById<RadioButton>(selectedTapRunningId)
        val tapRunning = radioTapRunning?.text.toString()

        val selectedEventId = radioGroupEvent.checkedRadioButtonId
        val radioEvent = findViewById<RadioButton>(selectedEventId)
        val event = radioEvent?.text.toString()

        // If event is "Yes", capture the amount used; else set it to 0
        val amountUsedForEvent = if (event == "Yes") {
            amountUsedInput.text.toString().toInt()
        } else {
            0
        }

        val footprint = WaterFootprint(
            showersPerDay,
            lengthOfShower,
            houseUsage,
            laundryDetails,
            dishesWashed,
            tapRunning,
            event,
            amountUsedForEvent
        )

        // Save to Firestore
        db.collection("waterFootprint")
            .add(footprint)
            .addOnSuccessListener { documentReference ->
                Toast.makeText(this, "Data saved successfully!", Toast.LENGTH_SHORT).show()

                // Pass document ID to FootprintResultActivity
                val intent = Intent(this, FootprintResultActivity::class.java)
                intent.putExtra("DOCUMENT_ID", documentReference.id) // Pass document ID to result page
                startActivity(intent)

                // Save the document ID to SharedPreferences
                val sharedPreferences = getSharedPreferences("AquaFlowPrefs", MODE_PRIVATE)
                val editor = sharedPreferences.edit()
                editor.putString("DOCUMENT_ID", documentReference.id) // Use documentReference.id here
                editor.apply() // Save the ID
            }
            .addOnFailureListener { e ->
                Toast.makeText(this, "Error saving data: ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }

    // Create a WaterFootprint data model class
    data class WaterFootprint(
        val showersPerDay: Int = 0,
        val lengthOfShower: Int = 0,
        val houseUsage: Int = 0,
        val laundryDetails: Int = 0,
        val dishesWashed: Int = 0,
        val tapRunning: String = "",
        val event: String = "",
        val  amountUsedForEvent : Int = 0
    )
}
