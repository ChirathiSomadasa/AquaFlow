package com.chirathi.aquaflow

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.CheckBox
import android.widget.ImageButton
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.firestore.FirebaseFirestore

class RecommendationsActivity : AppCompatActivity() {
    private lateinit var db: FirebaseFirestore

    // Lists for TextViews and ImageButtons to handle them efficiently
    private lateinit var recommendationTexts: List<TextView>
    private lateinit var recommendationButtons: List<ImageButton>
    private lateinit var recommendationLayouts: List<LinearLayout>
    private lateinit var remindercheckBox: List<CheckBox>


    companion object {
        // Average values as constants
        const val AVERAGE_SHOWERS_PER_DAY = 2
        const val AVERAGE_LENGTH_OF_SHOWER = 7
        const val AVERAGE_HOUSE_USAGE = 15
        const val AVERAGE_LAUNDRY_DETAILS = 2
        const val AVERAGE_DISHES_WASHED = 6
        const val AVERAGE_EVENT_USAGE = 30
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_recommendations)

        val back = findViewById<ImageButton>(R.id.back)
        back.setOnClickListener {
            startActivity(Intent(this, WasteWaterAdviceActivity::class.java))
        }

        db = FirebaseFirestore.getInstance()

        // Get document ID from SharedPreferences
        val sharedPreferences = getSharedPreferences("AquaFlowPrefs", MODE_PRIVATE)
        val documentId = sharedPreferences.getString("DOCUMENT_ID", null)

        Log.d("RecommendationsActivity", "Retrieved document ID: $documentId") // Debug log

        if (documentId.isNullOrEmpty()) {
            Log.e("RecommendationsActivity", "No document ID found in SharedPreferences")
            Toast.makeText(this, "No document ID found", Toast.LENGTH_SHORT).show()
            return
        } else {
            Log.d("RecommendationsActivity", "Document ID from SharedPreferences: $documentId")
            fetchWaterFootprintData(documentId)
        }

        // Initialize recommendation TextViews and ImageButtons using lists
        recommendationTexts = listOf(
            findViewById(R.id.recommendationText1),
            findViewById(R.id.recommendationText2),
            findViewById(R.id.recommendationText3),
            findViewById(R.id.recommendationText4),
            findViewById(R.id.recommendationText5),
            findViewById(R.id.recommendationText6),
            findViewById(R.id.recommendationText7),
            findViewById(R.id.recommendationText8)
        )

        recommendationButtons = listOf(
            findViewById(R.id.recommendationButton1),
            findViewById(R.id.recommendationButton2),
            findViewById(R.id.recommendationButton3),
            findViewById(R.id.recommendationButton4),
            findViewById(R.id.recommendationButton5),
            findViewById(R.id.recommendationButton6),
            findViewById(R.id.recommendationButton7),
            findViewById(R.id.recommendationButton8)
        )

        recommendationLayouts= listOf(
            findViewById(R.id.recommendationLayout1),
            findViewById(R.id.recommendationLayout2),
            findViewById(R.id.recommendationLayout3),
            findViewById(R.id.recommendationLayout4),
            findViewById(R.id.recommendationLayout5),
            findViewById(R.id.recommendationLayout6),
            findViewById(R.id.recommendationLayout7),
            findViewById(R.id.recommendationLayout8)

        )
        remindercheckBox= listOf(
            findViewById(R.id.reminderCheckbox1),
            findViewById(R.id.reminderCheckbox2),
            findViewById(R.id.reminderCheckbox3),
            findViewById(R.id.reminderCheckbox4),
            findViewById(R.id.reminderCheckbox5),
            findViewById(R.id.reminderCheckbox6),
            findViewById(R.id.reminderCheckbox7),
            findViewById(R.id.reminderCheckbox8)
        )
    }


    private fun fetchWaterFootprintData(documentId: String) {
        db.collection("waterFootprint").document(documentId)
            .get()
            .addOnSuccessListener { document ->
                if (document != null) {
                    val footprint = document.toObject(WaterFootprintActivity.WaterFootprint::class.java)
                    Log.d("RecommendationsActivity", "Fetched footprint: $footprint")
                    footprint?.let { generateRecommendations(it) }
                } else {
                    Log.e("RecommendationsActivity", "No such document")
                }
            }
            .addOnFailureListener { e ->
                Log.e("RecommendationsActivity", "Failed to load data: ${e.message}")
                Toast.makeText(this, "Failed to load data: ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }

    private fun generateRecommendations(footprint: WaterFootprintActivity.WaterFootprint) {
        val recommendations = listOf(
            Triple(footprint.showersPerDay, AVERAGE_SHOWERS_PER_DAY, "Install a water-efficient showerhead for strong flow with less water waste"),
            Triple(footprint.lengthOfShower, AVERAGE_LENGTH_OF_SHOWER, "Shorten your showers to conserve water and energy"),
            Triple(footprint.houseUsage, AVERAGE_HOUSE_USAGE, "Be mindful of your water use at home—small changes can save big."),
            Triple(footprint.laundryDetails, AVERAGE_LAUNDRY_DETAILS, "Running full laundry loads only will reduce water use."),
            Triple(footprint.dishesWashed, AVERAGE_DISHES_WASHED, "Soak pots and pans instead of scrubbing under running water."),
            Triple(footprint.amountUsedForEvent, AVERAGE_EVENT_USAGE, "Set up a refillable water station at events to reduce bottled waste.")
        )

        var recommendationsVisible = false

        // Iterate over the recommendation list
        for (i in recommendations.indices) {
            val recommendation = recommendations[i]
            val userValue = recommendation.first
            val averageValue = recommendation.second
            val message = recommendation.third

            if (userValue > averageValue) {
                recommendationTexts[i].text = message
                recommendationTexts[i].visibility = TextView.VISIBLE
                recommendationButtons[i].visibility = ImageButton.VISIBLE
                recommendationLayouts[i].visibility=LinearLayout.VISIBLE
                recommendationsVisible = true
            } else {
                recommendationTexts[i].visibility = TextView.GONE
                recommendationButtons[i].visibility = ImageButton.GONE
                recommendationLayouts[i].visibility=LinearLayout.GONE
            }

        }
// Set checkbox visibility based on specific user inputs
        remindercheckBox[0].visibility = if (footprint.showersPerDay > AVERAGE_SHOWERS_PER_DAY) View.VISIBLE else View.GONE
        remindercheckBox[1].visibility = if (footprint.lengthOfShower > AVERAGE_LENGTH_OF_SHOWER) View.VISIBLE else View.GONE
        remindercheckBox[2].visibility = if (footprint.houseUsage > AVERAGE_HOUSE_USAGE) View.VISIBLE else View.GONE
        remindercheckBox[4].visibility = if (footprint.laundryDetails > AVERAGE_LAUNDRY_DETAILS) View.VISIBLE else View.GONE
        remindercheckBox[5].visibility = if (footprint.dishesWashed > AVERAGE_DISHES_WASHED) View.VISIBLE else View.GONE
        remindercheckBox[7].visibility = if (footprint.amountUsedForEvent > AVERAGE_EVENT_USAGE) View.VISIBLE else View.GONE


        // If no recommendations are visible, show a toast
        if (!recommendationsVisible) {
            Toast.makeText(this, "To get customized recommendations according to your water usage, please fill the water footprint form", Toast.LENGTH_SHORT).show()
        }
    }
}
