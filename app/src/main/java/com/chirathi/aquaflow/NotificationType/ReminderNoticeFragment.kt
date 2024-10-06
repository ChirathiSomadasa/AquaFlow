package com.chirathi.aquaflow.NotificationType

import android.app.DatePickerDialog
import android.app.TimePickerDialog
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
import androidx.core.widget.addTextChangedListener
import com.chirathi.aquaflow.R
//import com.google.firebase.messaging.FirebaseMessaging      ///////// firebase setting
import android.widget.Toast
import androidx.core.app.NotificationCompat
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.messaging.FirebaseMessaging      ///////// firebase setting
import com.onesignal.OneSignal
import com.onesignal.debug.LogLevel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.json.JSONObject
import java.io.DataOutputStream
import java.net.HttpURLConnection
import java.net.URL
import java.text.SimpleDateFormat   // Get current time and format it
import java.util.Date
import java.util.Locale



val dateFormat = SimpleDateFormat("dd/MM/yyyy hh:mm", Locale.getDefault())
val currentTime = dateFormat.format(Date())


class ReminderNoticeFragment : Fragment() {

    private var selectedDate: String = ""
    private var selectedTime: String = ""
    private var selectedLocation: String = ""

    private lateinit var etSetDate: EditText
    private lateinit var etSetTime: EditText
    private lateinit var spinnerLocation: Spinner

    // OneSignal App ID
    private val ONESIGNAL_APP_ID = "ffd8de79-4fa2-4ad7-8a51-93edb3147e1f"
    private val ONESIGNAL_API_KEY = "MDIwNzZhZmYtOTUzZS00ZWU1LWFhNzgtMTM1N2M3NzYyMjQy" // Add your OneSignal REST API Key
    private val FCM_Server_Key = "BKqCP2enSG70Xu25El6oCrrz_0O0OhjZgKiETMPik6haoWEt6jNuVHe7vCXfNCImVfReH3MJsk4NmAw6-BxD-kU"


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

            val datePickerDialog = DatePickerDialog(requireContext(), { _, selectedYear, selectedMonth, selectedDay ->
                selectedDate = "${selectedYear}-${selectedMonth + 1}-${selectedDay}"
                etSetDate.setText(selectedDate)

                // Update reminder text with the selected date and time
                updateReminderText()

            }, year, month, day)

            datePickerDialog.show()
        }

        // Time Picker
        etSetTime.setOnClickListener {
            val calendar = Calendar.getInstance()
            val hour = calendar.get(Calendar.HOUR)
            val minute = calendar.get(Calendar.MINUTE)

            val timePickerDialog = TimePickerDialog(requireContext(), { _, selectedHour, selectedMinute ->
                selectedTime = String.format("%02d:%02d %s",
                    if (selectedHour == 0) 12 else selectedHour,
                    selectedMinute,
                    if (selectedHour < 12) "AM" else "PM")
                etSetTime.setText(selectedTime)

                // Update reminder text with the selected date and time
                updateReminderText()

            }, hour, minute, false)

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
//            sendNotification()
            sendNotification_Fire()
        }



        /*// Find the button and set an OnClickListener
        val nextButton = view.findViewById<Button>(R.id.btnConfirm)
        nextButton.setOnClickListener {
            // Replace with the next fragment on button click
            (activity as? ReminderNoticeActivity)?.replaceFragment(ReminderSendFragment())

        }*/


        return view
    }

    private fun lockInputFields() {
        etSetDate.isEnabled = false
        etSetTime.isEnabled = false
        spinnerLocation.isEnabled = false
    }

/*
    private fun sendNotification() {
        // Implement Firebase notification logic here
        val message = "Reminder set for $selectedDate at $selectedTime in $selectedLocation."

        // Ensure you have a topic for notifications
//        FirebaseMessaging.getInstance().subscribeToTopic("customers")   //////////// firebase setting

        // Assuming you want to send a notification to the topic
        // Your backend should handle sending the actual notification to this topic

        // For demonstration, you can print the message
        println("Notification sent: $message")
    }
*/

/*
    private fun sendNotification_Fire() {
        // Construct the notification message
        val message = """
            🚰 Reminder: Water Supply Today!
            Your scheduled water delivery is set for $selectedDate at $selectedTime to $selectedLocation. 
            Please ensure access to the delivery point is clear.
            Thank you!
        """.trimIndent()

        // Ensure you have a topic for notifications
        // FirebaseMessaging.getInstance().subscribeToTopic("customers")   //////////// firebase setting

        // For demonstration, you can print the message
        println("Notification sent: $message")
    }


        // Fetch consumer FCM tokens from Firestore
        val db = FirebaseFirestore.getInstance()

        db.collection("users")
            .whereEqualTo("isSupplier", false)  // Fetch consumers only
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
                                "body" to message
                            )
                        )

                        // Send the notification
                        CoroutineScope(Dispatchers.IO).launch {
                            sendNotificationToConsumer(notificationPayload)
                        }
                    }
                }
            }
            .addOnFailureListener { e ->
                println("Error fetching consumers' FCM tokens: ${e.message}")
            }
    }
*/


    private fun sendNotification_Fire() {
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
            "timestamp" to currentTime   // For ordering notifications
        )

        val db = FirebaseFirestore.getInstance()

        // Store the notification in Firestore
        db.collection("notifications")
            .add(notificationData)
            .addOnSuccessListener {
                println("Notification stored in Firestore.")
            }
            .addOnFailureListener { e ->
                println("Failed to store notification in Firestore: ${e.message}")
            }

        // Fetch consumer FCM tokens from Firestore
        db.collection("users")
            .whereEqualTo("isSupplier", false)  // Fetch consumers only
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
                                "body" to message
                            )
                        )

                        // Send the notification
                        CoroutineScope(Dispatchers.IO).launch {
                            sendNotificationToConsumer(notificationPayload)
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
        val fcmApiUrl = "https://fcm.googleapis.com/fcm/send"

        try {
            val url = URL(fcmApiUrl)
            val connection = url.openConnection() as HttpURLConnection
            connection.doOutput = true
            connection.requestMethod = "POST"
            connection.setRequestProperty("Content-Type", "application/json")
            connection.setRequestProperty("Authorization", "key=$FCM_Server_Key")  // Add your FCM server key

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



/*
    private fun sendNotification() {
        // Construct the notification message
        val message = """
        🚰 Reminder: Water Supply Today!
        Your scheduled water delivery is set for $selectedDate at $selectedTime to $selectedLocation. 
        Please ensure access to the delivery point is clear.
        Thank you!
    """.trimIndent()

        // Prepare the payload for OneSignal API
        val jsonBody = """
        {
            "app_id": "$ONESIGNAL_APP_ID",
            "headings": {"en": "Water Supply Reminder"},
            "contents": {"en": "$message"},
            "data": {"date": "$selectedDate", "time": "$selectedTime", "location": "$selectedLocation"}
        }
    """.trimIndent()

        // Use a coroutine to make the network call
        CoroutineScope(Dispatchers.IO).launch {
            try {
                // Make the POST request to OneSignal REST API
                val url = URL("https://onesignal.com/api/v1/notifications")
                val connection = url.openConnection() as HttpURLConnection
                connection.requestMethod = "POST"
                connection.setRequestProperty("Content-Type", "application/json; charset=UTF-8")
                connection.setRequestProperty("Authorization", "Basic $ONESIGNAL_API_KEY")
                connection.doOutput = true

                // Send the JSON body
                val outputStream = DataOutputStream(connection.outputStream)
                outputStream.writeBytes(jsonBody)
                outputStream.flush()
                outputStream.close()

                // Read the response
                val responseCode = connection.responseCode
                if (responseCode == HttpURLConnection.HTTP_OK) {
                    println("Notification sent successfully.")
                } else {
                    println("Failed to send notification. Response code: $responseCode")
                }

                connection.disconnect()
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
*/

/*

    private fun sendNotification() {
        val firestore = FirebaseFirestore.getInstance()
        val fcmTokens = mutableListOf<String>()

        // Step 1: Query Firestore for consumers (isSupplier = false)
        firestore.collection("users")
            .whereEqualTo("isSupplier", false)
            .get()
            .addOnSuccessListener { querySnapshot ->
                // Step 2: Collect FCM tokens of all consumers
                for (document in querySnapshot.documents) {
                    val token = document.getString("fcmToken")
                    if (token != null) {
                        fcmTokens.add(token)
                    }
                }

                // Step 3: If tokens are found, send the notification using OneSignal
                if (fcmTokens.isNotEmpty()) {
                    sendNotificationToFCMTokens(fcmTokens)
                } else {
                    println("No consumers found with FCM tokens.")
                }
            }
            .addOnFailureListener { exception ->
                println("Error getting consumers from Firestore: $exception")
            }
    }

    // Step 4: Send the notification to the retrieved FCM tokens
    private fun sendNotificationToFCMTokens(fcmTokens: List<String>) {
        // Construct the notification message
        val message = """
        🚰 Reminder: Water Supply Today!
        Your scheduled water delivery is set for $selectedDate at $selectedTime to $selectedLocation. 
        Please ensure access to the delivery point is clear.
        Thank you!
    """.trimIndent()

        // Prepare the payload for OneSignal API
        val jsonBody = """
        {
            "app_id": "$ONESIGNAL_APP_ID",
            "include_player_ids": ${fcmTokens.map { "\"$it\"" }},
            "headings": {"en": "Water Supply Reminder"},
            "contents": {"en": "$message"},
            "data": {"date": "$selectedDate", "time": "$selectedTime", "location": "$selectedLocation"}
        }
    """.trimIndent()

        // Use a coroutine to make the network call
        CoroutineScope(Dispatchers.IO).launch {
            try {
                // Make the POST request to OneSignal REST API
                val url = URL("https://onesignal.com/api/v1/notifications")
                val connection = url.openConnection() as HttpURLConnection
                connection.requestMethod = "POST"
                connection.setRequestProperty("Content-Type", "application/json; charset=UTF-8")
                connection.setRequestProperty("Authorization", "Basic $ONESIGNAL_API_KEY")
                connection.doOutput = true

                // Send the JSON body
                val outputStream = DataOutputStream(connection.outputStream)
                outputStream.writeBytes(jsonBody)
                outputStream.flush()
                outputStream.close()

                // Read the response
                val responseCode = connection.responseCode
                if (responseCode == HttpURLConnection.HTTP_OK) {
                    println("Notification sent successfully.")
                } else {
                    println("Failed to send notification. Response code: $responseCode")
                }

                connection.disconnect()
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
*/


}