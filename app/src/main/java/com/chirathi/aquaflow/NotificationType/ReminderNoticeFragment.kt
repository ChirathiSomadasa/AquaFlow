package com.chirathi.aquaflow.NotificationType

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.DialogInterface
import android.icu.util.Calendar
import android.os.Build
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
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.core.content.ContextCompat
import androidx.core.text.HtmlCompat
import com.chirathi.aquaflow.NotificationService.FirebaseUtils
import com.chirathi.aquaflow.R
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.json.JSONObject
import java.io.DataOutputStream
import java.net.HttpURLConnection
import java.net.URL
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter



class ReminderNoticeFragment : Fragment() {

    private var selectedDate: String = ""
    private var selectedTime: String = ""
    private var selectedLocation: String = ""

    private lateinit var etSetDate: EditText
    private lateinit var etSetTime: EditText
    private lateinit var spinnerLocation: Spinner



    private val FCM_Server_Key = "BKqCP2enSG70Xu25El6oCrrz_0O0OhjZgKiETMPik6haoWEt6jNuVHe7vCXfNCImVfReH3MJsk4NmAw6-BxD-kU"





    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_reminder_notice, container, false)


        // Find the TextView by its ID
        val tvNoticeContent = view.findViewById<TextView>(R.id.tvNoticeContent)
        val reminderText = getString(R.string.reminder_water_supply)

        etSetDate = view.findViewById(R.id.etSetDate)
        etSetTime = view.findViewById(R.id.etSetTime)
        spinnerLocation = view.findViewById(R.id.spinnerLocation)


        // Set initial reminder text
        tvNoticeContent.text = HtmlCompat.fromHtml(reminderText, HtmlCompat.FROM_HTML_MODE_LEGACY)

        // Function to update the reminder text with the selected date and time
        fun updateReminderText() {
            if (selectedDate.isNotEmpty() || selectedTime.isNotEmpty()) {
                val formattedReminderText = reminderText
                    .replace("[Date]", if (selectedDate.isNotEmpty()) selectedDate else "Not set")
                    .replace("[Time]", if (selectedTime.isNotEmpty()) selectedTime else "Not set")
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

            val datePickerDialog = DatePickerDialog(requireContext(), R.style.CustomDatePickerDialog,
                { _, selectedYear, selectedMonth, selectedDay ->
                selectedDate = "${selectedYear}-${selectedMonth + 1}-${selectedDay}"
                etSetDate.setText(selectedDate)

                // Update reminder text with the selected date and time
                updateReminderText()

            }, year, month, day)

            datePickerDialog.setOnShowListener {
                val positiveButton = datePickerDialog.getButton(DialogInterface.BUTTON_POSITIVE)
                positiveButton.setTextColor(ContextCompat.getColor(requireContext(), R.color.blueee))

                val negativeButton = datePickerDialog.getButton(DialogInterface.BUTTON_NEGATIVE)
                negativeButton.setTextColor(ContextCompat.getColor(requireContext(), R.color.blueee))
            }
            datePickerDialog.show()
        }

        // Time Picker
        etSetTime.setOnClickListener {
            val calendar = Calendar.getInstance()
            val hour = calendar.get(Calendar.HOUR)
            val minute = calendar.get(Calendar.MINUTE)

            val timePickerDialog = TimePickerDialog(requireContext(), R.style.CustomDatePickerDialog,
                { _, selectedHour, selectedMinute ->
                selectedTime = String.format("%02d:%02d %s",
                    if (selectedHour == 0) 12 else selectedHour,
                    selectedMinute,
                    if (selectedHour < 12) "AM" else "PM")
                etSetTime.setText(selectedTime)

                // Update reminder text with the selected date and time
                updateReminderText()

            }, hour, minute, false)

            timePickerDialog.setOnShowListener {
                val positiveButton = timePickerDialog.getButton(DialogInterface.BUTTON_POSITIVE)
                positiveButton.setTextColor(ContextCompat.getColor(requireContext(), R.color.blueee))

                val negativeButton = timePickerDialog.getButton(DialogInterface.BUTTON_NEGATIVE)
                negativeButton.setTextColor(ContextCompat.getColor(requireContext(), R.color.blueee))
            }
            timePickerDialog.show()
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
            lockInputFields()
            sendNotification_Fire()
        }

        return view
    }

    private fun lockInputFields() {
        etSetDate.isEnabled = false
        etSetTime.isEnabled = false
        spinnerLocation.isEnabled = false
    }


    @RequiresApi(Build.VERSION_CODES.O)
    private fun sendNotification_Fire() {

        val currentTime = LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy - hh:mm"))

        // Construct the notification message
        val message = """
        🚰 Reminder: Water Supply Today!
        Your scheduled water delivery is set for $selectedDate at $selectedTime to $selectedLocation. 
        Please ensure access to the delivery point is clear.
        Thank you!
    """.trimIndent()

        // Create a notification object to store in Firestore
        val notificationData = hashMapOf(
            "title" to "Water Supply Reminder",
            "body" to message,
            "date" to selectedDate,
            "time" to selectedTime,
            "location" to selectedLocation,
            "timestamp" to currentTime,  // For ordering notifications
            "isRead" to false  // Default to unread

        )

        val db = FirebaseFirestore.getInstance()

        // Store the notification in Firestore => notifications collection
        db.collection("notifications")
            .add(notificationData)
            .addOnSuccessListener {
                println("Notification stored in Firestore.")
                Toast.makeText(requireContext(), "Notification send successfully!", Toast.LENGTH_SHORT).show()

            }
            .addOnFailureListener { e ->
                println("Failed to store notification in Firestore: ${e.message}")
            }

        // Store the notification in Firestore => user collection(consumer only base on location)
        db.collection("users")
            .whereEqualTo("isSupplier", false)  // Target consumers only
            .whereEqualTo("address", selectedLocation)  // Match the user's address with the selected location
            .get()
            .addOnSuccessListener { querySnapshot ->
                for (document in querySnapshot.documents) {
                    val userId = document.id  // Get the consumer's user ID

                    // Prepare the notification data to store in the consumer's document
                    val consumerNotificationData = hashMapOf(
                        "title" to "Water Supply Reminder",
                        "body" to message,
                        "date" to selectedDate,
                        "time" to selectedTime,
                        "location" to selectedLocation,
                        "timestamp" to currentTime,
                        "isRead" to false  // Default to unread

                    )

                    // Update or add the notification for each consumer under their user document
                    db.collection("users").document(userId)
                        .collection("notifications")  // Create a subcollection for notifications
                        .add(consumerNotificationData)
                        .addOnSuccessListener {
                            println("Notification stored for user: $userId")
                        }
                        .addOnFailureListener { e ->
                            println("Failed to store notification for user $userId: ${e.message}")
                        }
                }
            }
            .addOnFailureListener { e ->
                println("Error fetching consumers: ${e.message}")
            }


        // Fetch consumer FCM tokens from Firestore
        db.collection("users")
            .whereEqualTo("isSupplier", false)  // Fetch consumers only
            .whereEqualTo("address", selectedLocation)  // Match the user's address with the selected location
            .get()
            .addOnSuccessListener { querySnapshot ->
                for (document in querySnapshot.documents) {
                    val fcmToken = document.getString("fcmToken")

                    if (!fcmToken.isNullOrEmpty()) {
                        // Prepare the notification payload
                        val notificationPayload = mapOf(
                            "to" to fcmToken,
                            "notification" to mapOf(
                                "title" to "Water Supply Reminder",
                                "body" to message,
                                "timestamp" to currentTime
                            )
                        )

                        // Send the notification
                        CoroutineScope(Dispatchers.IO).launch {
//                            sendNotificationToConsumer(notificationPayload)
                        }
                    }
                }
            }
            .addOnFailureListener { e ->
                println("Error fetching consumers' FCM tokens: ${e.message}")
            }
    }


    // Send notification to consumer using the FCM token
    private fun sendNotificationToConsumer(payload: Map<String, Any>) {
        val fcmApiUrl = "https://fcm.googleapis.com/v1/projects/aquaflow-e93c7/messages:send"
        val accessToken = FirebaseUtils.getAccessToken() // Get the access token


        try {
            val url = URL(fcmApiUrl)
            val connection = url.openConnection() as HttpURLConnection
            connection.doOutput = true
            connection.requestMethod = "POST"
            connection.setRequestProperty("Content-Type", "application/json")
            connection.setRequestProperty("Authorization", "Bearer $accessToken")  // Replace with your FCM server key

            val dataOutputStream = DataOutputStream(connection.outputStream)
            val jsonPayload = JSONObject(payload).toString()
            dataOutputStream.writeBytes(jsonPayload)
            dataOutputStream.flush()
            dataOutputStream.close()

            val responseCode = connection.responseCode
            if (responseCode == HttpURLConnection.HTTP_OK) {
                println("Notification sent successfully.")
            } else {
                println("Failed to send notification. Response code: $responseCode")
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }






}