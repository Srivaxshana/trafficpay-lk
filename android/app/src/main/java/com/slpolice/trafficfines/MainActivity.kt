package com.slpolice.trafficfines

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ProgressBar
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.google.android.material.snackbar.Snackbar
import com.slpolice.trafficfines.network.RetrofitClient
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {

    private lateinit var etReferenceNumber: EditText
    private lateinit var etCategoryCode: EditText
    private lateinit var btnSearch: Button
    private lateinit var progressBar: ProgressBar
    private lateinit var tvError: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        etReferenceNumber = findViewById(R.id.etReferenceNumber)
        etCategoryCode = findViewById(R.id.etCategoryCode)
        btnSearch = findViewById(R.id.btnSearch)
        progressBar = findViewById(R.id.progressBar)
        tvError = findViewById(R.id.tvError)

        btnSearch.setOnClickListener { searchFine() }
    }

    private fun searchFine() {
        val ref = etReferenceNumber.text.toString().trim().uppercase()
        val cat = etCategoryCode.text.toString().trim().uppercase()

        if (ref.isEmpty() || cat.isEmpty()) {
            tvError.text = "Please enter both Reference Number and Category Code."
            tvError.visibility = View.VISIBLE
            return
        }

        tvError.visibility = View.GONE
        progressBar.visibility = View.VISIBLE
        btnSearch.isEnabled = false

        lifecycleScope.launch {
            try {
                val response = RetrofitClient.apiService.lookupFine(ref, cat)
                if (response.isSuccessful && response.body() != null) {
                    val fine = response.body()!!
                    val intent = Intent(this@MainActivity, PaymentActivity::class.java).apply {
                        putExtra("FINE_ID", fine.id)
                        putExtra("FINE_REF", fine.referenceNumber)
                        putExtra("FINE_CAT_CODE", fine.categoryCode)
                        putExtra("FINE_CAT_NAME", fine.categoryName)
                        putExtra("FINE_AMOUNT", fine.amount)
                        putExtra("FINE_VEHICLE", fine.vehicleNumber)
                        putExtra("FINE_DRIVER", fine.driverName)
                        putExtra("FINE_DISTRICT", fine.district)
                        putExtra("FINE_OFFICER", fine.officerName)
                        putExtra("FINE_STATUS", fine.status)
                    }
                    startActivity(intent)
                } else {
                    tvError.text = "Fine not found. Please check the reference number and category code."
                    tvError.visibility = View.VISIBLE
                }
            } catch (e: Exception) {
                tvError.text = "Connection error. Make sure the backend server is running."
                tvError.visibility = View.VISIBLE
            } finally {
                progressBar.visibility = View.GONE
                btnSearch.isEnabled = true
            }
        }
    }
}
