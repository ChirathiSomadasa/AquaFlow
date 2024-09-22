package com.chirathi.aquaflow.NotificationType

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.core.text.HtmlCompat
import com.chirathi.aquaflow.R


class ReminderNoticeFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
//        // Inflate the layout for this fragment
//        return inflater.inflate(R.layout.fragment_reminder_notice, container, false)


        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_reminder_notice, container, false)
//


        // Find the TextView by its ID
        val tvNoticeContent = view.findViewById<TextView>(R.id.tvNoticeContent)
        val reminderText = getString(R.string.reminder_water_supply)

        // Get date and time inputs (or you can hardcode values for now)
        val date = "2024-09-30" // Replace with actual value
        val time = "10:00 AM"   // Replace with actual value

        // Replace placeholders in the string with the actual date and time
        val formattedReminderText = reminderText
            .replace("[Date]", date)
            .replace("[Time]", time)

        // Set the formatted string to the TextView
        tvNoticeContent.text = HtmlCompat.fromHtml(formattedReminderText, HtmlCompat.FROM_HTML_MODE_LEGACY)


        // Find the button and set an OnClickListener
//        val nextButton = view.findViewById<Button>(R.id.btnConfirm)
//        nextButton.setOnClickListener {
//            // Replace with the next fragment on button click
//            (activity as? ReminderNoticeActivity)?.replaceFragment(NextFragment())
//        }



        return view
    }


}