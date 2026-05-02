package com.slpolice.trafficfines

import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class SuccessActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_success)

        val txnId = intent.getStringExtra("TXN_ID") ?: ""
        val ref = intent.getStringExtra("FINE_REF") ?: ""
        val catName = intent.getStringExtra("CAT_NAME") ?: ""
        val amount = intent.getDoubleExtra("AMOUNT", 0.0)
        val paidAt = intent.getStringExtra("PAID_AT") ?: ""

        findViewById<TextView>(R.id.tvTxnId).text = txnId
        findViewById<TextView>(R.id.tvSuccessRef).text = ref
        findViewById<TextView>(R.id.tvSuccessViolation).text = catName
        findViewById<TextView>(R.id.tvSuccessAmount).text = "LKR ${String.format("%,.0f", amount)}.00"
        findViewById<TextView>(R.id.tvSuccessPaidAt).text = paidAt

        findViewById<Button>(R.id.btnDone).setOnClickListener {
            finishAffinity()
        }
    }
}
