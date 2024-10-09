package com.chirathi.aquaflow

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.ImageButton
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.github.mikephil.charting.charts.BarChart
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter
import com.google.firebase.firestore.FirebaseFirestore

class UsageGraphsActivity : AppCompatActivity() {
    private lateinit var db: FirebaseFirestore
    private lateinit var barChartDaily: BarChart
    private lateinit var barChartWeekly: BarChart

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_usage_graphs)

        db = FirebaseFirestore.getInstance()
        barChartDaily = findViewById(R.id.barChartDaily)
        barChartWeekly = findViewById(R.id.barChartWeekly)

        // Retrieve document ID from SharedPreferences
        val sharedPreferences = getSharedPreferences("AquaFlowPrefs", MODE_PRIVATE)
        val documentId = sharedPreferences.getString("DOCUMENT_ID", null)

        // Check if document ID is valid
        if (documentId != null) {
            fetchWaterFootprintData(documentId)
        } else {
            Toast.makeText(this, "Document ID not found", Toast.LENGTH_SHORT).show()
            Log.e("UsageGraphsActivity", "Document ID not found")
            finish() // Close activity if no ID
        }

        val back = findViewById<ImageButton>(R.id.back1)
        back.setOnClickListener {
            startActivity(Intent(this, WasteWaterAdviceActivity::class.java))
        }
    }

    private fun fetchWaterFootprintData(documentId: String) {
        // Fetch daily data
        db.collection("waterFootprint").document(documentId)
            .get()
            .addOnSuccessListener { document ->
                if (document != null) {
                    val footprint = document.toObject(WaterFootprintActivity.WaterFootprint::class.java)
                    Log.d("UsageGraphsActivity", "Document ID: $documentId")

                    val dailyInputs = listOf(
                        footprint?.showersPerDay ?: 0,
                        footprint?.lengthOfShower ?: 0,
                        footprint?.houseUsage ?: 0,
                        footprint?.details ?: 0,
                        footprint?.laundryDetails ?: 0,
                        footprint?.dishesWashed ?: 0,
                        footprint?.washDuration ?: 0,
                        footprint?.amountUsedForEvent ?: 0
                    )
                    val dailyLabels = listOf("Showers", "Length", "Usage", "Details", "Laundry", "Dishes", "Washing", "Event")

                    setupBarChartD(barChartDaily, dailyInputs, dailyLabels)

                    fetchWeeklyData()
                } else {
                    Log.e("UsageGraphsActivity", "No such document")
                }
            }
            .addOnFailureListener { e ->
                Log.e("UsageGraphsActivity", "Failed to load data: ${e.message}")
                Toast.makeText(this, "Failed to load data: ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }

    private fun fetchWeeklyData() {
        // Constants for weekly water usage
        val weeklyData = listOf(15, 10, 20, 5, 25, 30, 15) // Example constant values for each day
        val labels = listOf("Sunday", "Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday") // Day labels

        // Set up the weekly graph with constants
        setupBarChartW(barChartWeekly, weeklyData, labels)
    }

    private fun setupBarChartD(barChart: BarChart, data: List<Int>, labels: List<String>) {
        val entries = data.mapIndexed { index, value -> BarEntry(index.toFloat(), value.toFloat()) }

        val dataSet = BarDataSet(entries, "Daily Water Usage").apply {
            color = ContextCompat.getColor(this@UsageGraphsActivity, R.color.blue)
            valueTextColor = ContextCompat.getColor(this@UsageGraphsActivity, R.color.white)
            valueTextSize = 16f
        }

        val barData = BarData(dataSet)
        barChart.data = barData
        barChart.setFitBars(true)
        barChart.xAxis.valueFormatter = IndexAxisValueFormatter(labels)

        // Customize appearance
        barChart.axisLeft.textColor = ContextCompat.getColor(this, R.color.black)
        barChart.axisRight.isEnabled = false
        barChart.xAxis.textColor = ContextCompat.getColor(this, R.color.black)
        barChart.xAxis.position = com.github.mikephil.charting.components.XAxis.XAxisPosition.BOTTOM
        barChart.xAxis.granularity = 1f

        // Enable touch gestures
        barChart.setTouchEnabled(true)
        barChart.isDragEnabled = false
        barChart.setScaleEnabled(false)

        // Animate chart
        barChart.animateY(2000)
        barChart.invalidate() // Refresh the chart
    }

    private fun setupBarChartW(barChart: BarChart, data: List<Int>, labels: List<String>) {
        val entries = data.mapIndexed { index, value -> BarEntry(index.toFloat(), value.toFloat()) }

        val dataSet = BarDataSet(entries, "Weekly Water Usage").apply {
            color = ContextCompat.getColor(this@UsageGraphsActivity, R.color.blue)
            valueTextColor = ContextCompat.getColor(this@UsageGraphsActivity, R.color.white)
            valueTextSize = 16f
        }

        val barData = BarData(dataSet)
        barChart.data = barData
        barChart.setFitBars(true)
        barChart.xAxis.valueFormatter = IndexAxisValueFormatter(labels)

        // Customize appearance
        barChart.axisLeft.textColor = ContextCompat.getColor(this, R.color.black)
        barChart.axisRight.isEnabled = false
        barChart.xAxis.textColor = ContextCompat.getColor(this, R.color.black)
        barChart.xAxis.position = com.github.mikephil.charting.components.XAxis.XAxisPosition.BOTTOM
        barChart.xAxis.granularity = 1f

        // Enable touch gestures
        barChart.setTouchEnabled(true)
        barChart.isDragEnabled = false
        barChart.setScaleEnabled(false)

        // Animate chart
        barChart.animateY(2000)
        barChart.invalidate() // Refresh the chart
    }
}
