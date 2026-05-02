package com.slpolice.trafficfines

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.slpolice.trafficfines.network.PaymentRequest
import com.slpolice.trafficfines.network.RetrofitClient
import kotlinx.coroutines.launch

class PaymentActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_payment)

        val ref = intent.getStringExtra("FINE_REF") ?: ""
        val catCode = intent.getStringExtra("FINE_CAT_CODE") ?: ""
        val catName = intent.getStringExtra("FINE_CAT_NAME") ?: ""
        val amount = intent.getDoubleExtra("FINE_AMOUNT", 0.0)
        val vehicle = intent.getStringExtra("FINE_VEHICLE") ?: ""
        val driver = intent.getStringExtra("FINE_DRIVER") ?: ""
        val district = intent.getStringExtra("FINE_DISTRICT") ?: ""
        val officer = intent.getStringExtra("FINE_OFFICER") ?: ""
        val status = intent.getStringExtra("FINE_STATUS") ?: ""

        // Populate fine details
        findViewById<TextView>(R.id.tvRef).text = ref
        findViewById<TextView>(R.id.tvViolation).text = catName
        findViewById<TextView>(R.id.tvVehicle).text = vehicle
        findViewById<TextView>(R.id.tvDriver).text = driver
        findViewById<TextView>(R.id.tvDistrict).text = district
        findViewById<TextView>(R.id.tvOfficer).text = officer
        findViewById<TextView>(R.id.tvAmount).text = "LKR ${String.format("%,.0f", amount)}.00"

        val tvError = findViewById<TextView>(R.id.tvPayError)
        val progressBar = findViewById<ProgressBar>(R.id.progressBarPay)
        val btnPay = findViewById<Button>(R.id.btnPay)
        val etCardHolder = findViewById<EditText>(R.id.etCardHolder)
        val etCardNumber = findViewById<EditText>(R.id.etCardNumber)
        val etExpiry = findViewById<EditText>(R.id.etExpiry)
        val etCvv = findViewById<EditText>(R.id.etCvv)

        if (status == "PAID") {
            btnPay.isEnabled = false
            tvError.text = "This fine has already been paid."
            tvError.visibility = View.VISIBLE
        }

        btnPay.setOnClickListener {
            val cardHolder = etCardHolder.text.toString().trim()
            val cardNumber = etCardNumber.text.toString().trim()
            val expiry = etExpiry.text.toString().trim()
            val cvv = etCvv.text.toString().trim()

            if (cardHolder.isEmpty() || cardNumber.isEmpty() || expiry.isEmpty() || cvv.isEmpty()) {
                tvError.text = "Please fill all card details."
                tvError.visibility = View.VISIBLE
                return@setOnClickListener
            }

            tvError.visibility = View.GONE
            progressBar.visibility = View.VISIBLE
            btnPay.isEnabled = false

            lifecycleScope.launch {
                try {
                    val request = PaymentRequest(
                        referenceNumber = ref,
                        categoryCode = catCode,
                        cardHolderName = cardHolder,
                        cardNumber = cardNumber,
                        expiryDate = expiry,
                        cvv = cvv
                    )
                    val response = RetrofitClient.apiService.processPayment(request)
                    if (response.isSuccessful && response.body() != null) {
                        val payment = response.body()!!
                        val intent = Intent(this@PaymentActivity, SuccessActivity::class.java).apply {
                            putExtra("TXN_ID", payment.transactionId)
                            putExtra("FINE_REF", payment.referenceNumber)
                            putExtra("CAT_NAME", catName)
                            putExtra("AMOUNT", payment.amount)
                            putExtra("PAID_AT", payment.paidAt)
                        }
                        startActivity(intent)
                        finish()
                    } else {
                        tvError.text = "Payment failed. The fine may already be paid."
                        tvError.visibility = View.VISIBLE
                        btnPay.isEnabled = true
                    }
                } catch (e: Exception) {
                    tvError.text = "Connection error. Please try again."
                    tvError.visibility = View.VISIBLE
                    btnPay.isEnabled = true
                } finally {
                    progressBar.visibility = View.GONE
                }
            }
        }
    }
}
