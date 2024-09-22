package com.chirathi.aquaflow

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.RelativeLayout

class SupplierHomeFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view =  inflater.inflate(R.layout.fragment_supplier_home, container, false)

        val scanqrcodeView = view.findViewById<Button>(R.id.Scan_QR_code_btn)
        scanqrcodeView.setOnClickListener {
            val intent = Intent(context, ScanQRCodeActivity::class.java)
            startActivity(intent)
        }

        return view
    }

}