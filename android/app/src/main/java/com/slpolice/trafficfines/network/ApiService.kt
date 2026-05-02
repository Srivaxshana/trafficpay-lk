package com.slpolice.trafficfines.network

import retrofit2.Response
import retrofit2.http.*

data class FineResponse(
    val id: Long,
    val referenceNumber: String,
    val categoryCode: String,
    val categoryName: String,
    val amount: Double,
    val vehicleNumber: String,
    val driverName: String,
    val district: String,
    val status: String,
    val officerName: String,
    val officerBadge: String
)

data class PaymentRequest(
    val referenceNumber: String,
    val categoryCode: String,
    val cardHolderName: String,
    val cardNumber: String,
    val expiryDate: String,
    val cvv: String,
    val paymentMethod: String = "CARD"
)

data class PaymentResponse(
    val paymentId: Long,
    val transactionId: String,
    val referenceNumber: String,
    val amount: Double,
    val paymentMethod: String,
    val paidAt: String,
    val message: String
)

interface ApiService {

    @GET("fines/lookup")
    suspend fun lookupFine(
        @Query("referenceNumber") referenceNumber: String,
        @Query("categoryCode") categoryCode: String
    ): Response<FineResponse>

    @POST("payments/pay")
    suspend fun processPayment(@Body request: PaymentRequest): Response<PaymentResponse>
}
