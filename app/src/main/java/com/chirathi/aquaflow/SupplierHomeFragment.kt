package com.chirathi.aquaflow

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button

class SupplierHomeFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_supplier_home, container, false)



        // Find the button using the inflated view
        val paymentProcessView = view.findViewById<Button>(R.id.button4)
        paymentProcessView.setOnClickListener {
            // Start PaymentProcessActivity when button is clicked
            val intent = Intent(context, PaymentProcessActivity::class.java)
            startActivity(intent)
        }

        // Return the inflated view
        return view
    }
}
