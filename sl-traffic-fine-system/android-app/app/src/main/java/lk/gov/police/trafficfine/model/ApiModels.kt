package com.slpolice.trafficfineapp.model

data class LoginRequest(val badgeNumber: String, val password: String)

data class AuthResponse(
    val token: String,
    val refreshToken: String?,
    val expiresIn: Long,
    val user: UserInfo?
)

data class UserInfo(
    val userId: String,
    val fullName: String,
    val role: String,
    val district: String,
    val badgeNumber: String,
    val phoneNumber: String?
)

data class FineCategory(
    val categoryId: String,
    val code: String,
    val description: String,
    val amount: Double,
    val legalReference: String?
)

data class CategoryListResponse(val categories: List<FineCategory>)

data class IssueFineRequest(
    val categoryId: String,
    val vehicleNumber: String,
    val vehicleType: String,
    val driverNicNumber: String,
    val driverName: String,
    val driverPhone: String?,
    val location: String?,
    val latitude: Double?,
    val longitude: Double?
)

data class FineResponse(
    val fineId: String,
    val referenceNumber: String,
    val categoryId: String,
    val categoryCode: String?,
    val categoryDescription: String?,
    val amount: Double,
    val status: String,
    val vehicleNumber: String,
    val vehicleType: String?,
    val driverName: String?,
    val district: String?,
    val station: String?,
    val issuedByName: String?,
    val issuedAt: String?,
    val paidAt: String?
)

data class FineListResponse(val fines: List<FineResponse>)

data class InitiatePaymentRequest(
    val referenceNumber: String,
    val categoryId: String,
    val paymentMethod: String,
    val paymentChannel: String,
    val paidByName: String,
    val paidByNic: String
)

data class PaymentResponse(
    val paymentId: String,
    val fineId: String?,
    val referenceNumber: String?,
    val amount: Double,
    val status: String,
    val paymentGatewayUrl: String?,
    val receiptNumber: String?,
    val smsNotified: Boolean,
    val paidAt: String?
)
