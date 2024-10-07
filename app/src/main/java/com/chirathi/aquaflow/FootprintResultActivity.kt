package com.chirathi.aquaflow

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.github.mikephil.charting.charts.BarChart
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter
import com.github.mikephil.charting.formatter.ValueFormatter
import com.google.firebase.firestore.FirebaseFirestore

class FootprintResultActivity : AppCompatActivity() {

    private lateinit var db: FirebaseFirestore
    private lateinit var barChart: BarChart

    companion object {
        const val AVERAGE_SHOWERS_PER_DAY = 2
        const val AVERAGE_LENGTH_OF_SHOWER = 7
        const val AVERAGE_HOUSE_USAGE = 15
        const val AVERAGE_DETAILS =3
        const val AVERAGE_LAUNDRY_DETAILS = 2
        const val AVERAGE_DISHES_WASHED = 6
        const val AVERAGE_WASH_DURATION = 5
        const val AVERAGE_EVENT_USAGE= 30
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_footprint_result)
        barChart = findViewById(R.id.barChart)

        db = FirebaseFirestore.getInstance()


        // Get document ID from intent
        val documentId = intent.getStringExtra("DOCUMENT_ID") ?: return
        fetchWaterFootprintData(documentId)

        val backToHomeButton = findViewById<Button>(R.id.backToHomeButton)
        backToHomeButton.setOnClickListener {
            startActivity(Intent(this, HomeActivity::class.java))
        }

        val recommendationButtons = listOf<Button>(
            findViewById(R.id.q1),
            findViewById(R.id.q2),
            findViewById(R.id.q3),
            findViewById(R.id.q4),
            findViewById(R.id.q5),
            findViewById(R.id.q6),
            findViewById(R.id.q7),
            findViewById(R.id.q8),
            findViewById(R.id.q9)
        )

        recommendationButtons.forEach { button ->
            button.setOnClickListener {
                startActivity(Intent(this, RecommendationsActivity::class.java))
            }
        }
    }

    private fun fetchWaterFootprintData(documentId: String) {
        db.collection("waterFootprint").document(documentId)
            .get()
            .addOnSuccessListener { document ->
                if (document != null) {
                    val footprint = document.toObject(WaterFootprintActivity.WaterFootprint::class.java)
                    Log.d("FootprintResultActivity", "Fetched footprint: $footprint")

                    // Extract data for the bar chart
                    val userInputs = listOf(
                        footprint?.showersPerDay ?: 0,
                        footprint?.lengthOfShower ?: 0,
                        footprint?.houseUsage ?: 0,
                        footprint?.details ?: 0,
                        footprint?.laundryDetails ?: 0,
                        footprint?.dishesWashed ?: 0,
                        footprint?.washDuration ?: 0,
                        footprint?.amountUsedForEvent ?: 0,

                    )

                    val labels = listOf("Showers", "duration", "Usage","brushing", "Laundry", "Dishes", "Washing", "event")

                    setupBarChart(userInputs, labels)

                    updateTextView(R.id.useranswer1, footprint?.showersPerDay ?: 0)
                    updateAverageTextView(R.id.Avg1, AVERAGE_SHOWERS_PER_DAY)
                    toggleTipVisibility(R.id.q1, footprint?.showersPerDay ?: 0, AVERAGE_SHOWERS_PER_DAY)

                    updateTextView(R.id.useranswer2, footprint?.lengthOfShower ?: 0)
                    updateAverageTextView(R.id.Avg2, AVERAGE_LENGTH_OF_SHOWER)
                    toggleTipVisibility(R.id.q2, footprint?.lengthOfShower ?: 0, AVERAGE_LENGTH_OF_SHOWER)

                    updateTextView(R.id.useranswer3, footprint?.houseUsage?: 0)
                    updateAverageTextView(R.id.Avg3, AVERAGE_HOUSE_USAGE)
                    toggleTipVisibility(R.id.q3, footprint?.houseUsage ?: 0, AVERAGE_HOUSE_USAGE)

                    updateTextView(R.id.useranswer4, footprint?.details ?: 0)
                    updateAverageTextView(R.id.Avg4, AVERAGE_DETAILS)
                    toggleTipVisibility(R.id.q4, footprint?.details ?: 0, AVERAGE_DETAILS)

                    // Handle tap running and event as strings
                    val tapRunningValue = footprint?.tapRunning ?: "N/A"
                    findViewById<TextView>(R.id.useranswer5).text = tapRunningValue
                    findViewById<TextView>(R.id.Avg5).text = "No" // Average value for running tap
                    toggleTipVisibility(R.id.q5, tapRunningValue == "yes", true)

                    updateTextView(R.id.useranswer6, footprint?.laundryDetails?: 0)
                    updateAverageTextView(R.id.Avg6, AVERAGE_LAUNDRY_DETAILS)
                    toggleTipVisibility(R.id.q6, footprint?.laundryDetails ?: 0, AVERAGE_LAUNDRY_DETAILS)

                    updateTextView(R.id.useranswer7, footprint?.amountUsedForEvent?: 0)
                    updateAverageTextView(R.id.Avg7, AVERAGE_EVENT_USAGE)
                    toggleTipVisibility(R.id.q7, footprint?.amountUsedForEvent?: 0, AVERAGE_EVENT_USAGE)

                    updateTextView(R.id.useranswer8, footprint?.dishesWashed?: 0)
                    updateAverageTextView(R.id.Avg8, AVERAGE_DISHES_WASHED)
                    toggleTipVisibility(R.id.q8, footprint?.dishesWashed?: 0, AVERAGE_DISHES_WASHED)

                    updateTextView(R.id.useranswer9, footprint?.washDuration?: 0)
                    updateAverageTextView(R.id.Avg9, AVERAGE_WASH_DURATION)
                    toggleTipVisibility(R.id.q9, footprint?.washDuration?: 0, AVERAGE_WASH_DURATION)



                } else {
                    Log.e("FootprintResultActivity", "No such document")
                }
            }
            .addOnFailureListener { e ->
                Log.e("FootprintResultActivity", "Failed to load data: ${e.message}")
                Toast.makeText(this, "Failed to load data: ${e.message}", Toast.LENGTH_SHORT).show()
            }


    }
    private fun setupBarChart(userInputs: List<Int>, labels: List<String>) {
        val entries = userInputs.mapIndexed { index, value -> BarEntry(index.toFloat(), value.toFloat()) }

        val dataSet = BarDataSet(entries, "Water Footprint Data").apply {
            color = resources.getColor(R.color.blue, null)
            valueTextColor = resources.getColor(R.color.white, null)
            valueTextSize = 16f
        }

        val barData = BarData(dataSet)
        barChart.data = barData
        barChart.setFitBars(true)

        // Set x-axis labels
        barChart.xAxis.valueFormatter = object : ValueFormatter() {
            override fun getAxisLabel(
                value: Float,
                axis: com.github.mikephil.charting.components.AxisBase?
            ): String? {
                return labels[value.toInt()]
            }
        }

        // Customize appearance
        barChart.axisLeft.textColor = resources.getColor(R.color.black, null)
        barChart.axisRight.isEnabled = false // Disable right y-axis
        barChart.xAxis.textColor = resources.getColor(R.color.black, null)
        barChart.xAxis.position = XAxis.XAxisPosition.BOTTOM // Position labels at the bottom
        barChart.xAxis.granularity = 1f // Set granularity to ensure labels show up
        barChart.xAxis.valueFormatter = IndexAxisValueFormatter(labels) // Ensure labels are formatted

        barChart.legend.textColor = resources.getColor(R.color.black, null)

        // Enable touch gestures
        barChart.setTouchEnabled(false)
        barChart.isDragEnabled = true
        barChart.setScaleEnabled(false)

        // Animate chart
        barChart.animateY(2000)

        barChart.invalidate() // Refresh the chart
    }


    private fun updateTextView(textViewId: Int, value: Int) {
        findViewById<TextView>(textViewId).text = value.toString()
    }

    private fun updateAverageTextView(textViewId: Int, average: Int) {
        findViewById<TextView>(textViewId).text = average.toString()
    }
    private fun toggleTipVisibility(buttonId: Int, userValue: Int, averageValue: Int) {
        val button = findViewById<Button>(buttonId)
        if (userValue > averageValue) {
            button.isEnabled = true
            button.setBackgroundColor(resources.getColor(R.color.blue)) // Original color
        } else {
            button.isEnabled = false
            button.setBackgroundColor(resources.getColor(R.color.lightgray)) // Set to grey
        }
    }

    private fun toggleTipVisibility(buttonId: Int, condition: Boolean, isAlwaysVisible: Boolean) {
        val button = findViewById<Button>(buttonId)
        if (condition || isAlwaysVisible) {
            button.isEnabled = true
            button.setBackgroundColor(resources.getColor(R.color.blue)) // Original color
        } else {
            button.isEnabled = false
            button.setBackgroundColor(resources.getColor(R.color.lightgray)) // Set to grey
        }
    }

}
