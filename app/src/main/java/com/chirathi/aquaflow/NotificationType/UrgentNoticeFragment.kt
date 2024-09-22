package com.chirathi.aquaflow.NotificationType

import android.app.DatePickerDialog
import android.icu.util.Calendar
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.Spinner
import android.widget.TextView
import androidx.core.text.HtmlCompat
import com.chirathi.aquaflow.R


class UrgentNoticeFragment : Fragment() {

    private var selectedDate: String = ""
    private var selectedLocation: String = ""

    private lateinit var etSetDate: EditText
    private lateinit var spinnerLocation: Spinner


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_urgent_notice, container, false)


        // Find the TextView by its ID
        val tvNoticeContent = view.findViewById<TextView>(R.id.tvNoticeContent)
        val reminderText = getString(R.string.urgent_notice_water_supply_disruption)

        etSetDate = view.findViewById(R.id.etSetDate)
        spinnerLocation = view.findViewById(R.id.spinnerLocation)


        // Set initial reminder text
        tvNoticeContent.text = HtmlCompat.fromHtml(reminderText, HtmlCompat.FROM_HTML_MODE_LEGACY)

        // Function to update the reminder text with the selected date and time
        fun updateReminderText() {
            if (selectedDate.isNotEmpty() ) {
                val formattedReminderText = reminderText
                    .replace("[Date]", if (selectedDate.isNotEmpty()) selectedDate else "Not set")
                    .replace("[Location]", if (selectedLocation.isNotEmpty()) selectedLocation else "Not set")
                tvNoticeContent.text = HtmlCompat.fromHtml(formattedReminderText, HtmlCompat.FROM_HTML_MODE_LEGACY)
            }
        }

        // Date Picker Dialog
        etSetDate.setOnClickListener {
            val calendar = Calendar.getInstance()
            val year = calendar.get(Calendar.YEAR)
            val month = calendar.get(Calendar.MONTH)
            val day = calendar.get(Calendar.DAY_OF_MONTH)

            val datePickerDialog = DatePickerDialog(requireContext(), { _, selectedYear, selectedMonth, selectedDay ->
                selectedDate = "${selectedYear}-${selectedMonth + 1}-${selectedDay}"
                etSetDate.setText(selectedDate)

                // Update reminder text with the selected date and time
                updateReminderText()

            }, year, month, day)

            datePickerDialog.show()
        }


        // Set up the spinner with the districts
        val districts = arrayOf(
            "Select Location", "Colombo", "Gampaha", "Kalutara", "Kandy", "Matale", "Nuwara Eliya",
            "Galle", "Hambantota", "Matara", "Kurunegala", "Puttalam", "Anuradhapura",
            "Polonnaruwa", "Badulla", "Monaragala", "Ratnapura", "Kegalle", "Jaffna",
            "Kilinochchi", "Mannar", "Mullaitivu", "Vavuniya", "Trincomalee",
            "Batticaloa", "Ampara"
        )

        // Create an ArrayAdapter using the string array and a default spinner layout
        val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, districts)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerLocation.adapter = adapter

        // Set listener for spinner to update selectedLocation
        spinnerLocation.setOnItemSelectedListener(object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View, position: Int, id: Long) {
                selectedLocation = districts[position]
                updateReminderText()
            }

            override fun onNothingSelected(parent: AdapterView<*>) {
                selectedLocation = ""
            }
        })

        // Find the buttons and set OnClickListeners
        val btnConfirm = view.findViewById<Button>(R.id.btnConfirm)
        btnConfirm.setOnClickListener {
            lockInputFields()  // Lock input fields
        }

        val sendButton = view.findViewById<Button>(R.id.btnSendNotification)
        sendButton.setOnClickListener {
            sendNotification()
        }

        return view
    }

    private fun lockInputFields() {
        etSetDate.isEnabled = false
        spinnerLocation.isEnabled = false
    }

    private fun sendNotification() {
        // Construct the notification message
        val message = """
            Urgent Notice: Water Supply Disruption
            An unexpected disruption in the water supply scheduled for $selectedDate at $selectedLocation. 
            We are working to resolve the issue as quickly as possible. 
            Please conserve any available water. Updates will follow shortly.
        """.trimIndent()

        // Ensure you have a topic for notifications
        // FirebaseMessaging.getInstance().subscribeToTopic("customers")   //////////// firebase setting

        // For demonstration, you can print the message
        println("Notification sent: $message")
    }
}