package com.slpolice.trafficfineapp.network

import com.slpolice.trafficfineapp.model.*
import retrofit2.Response
import retrofit2.http.*

interface ApiService {

    @POST("auth/login")
    suspend fun login(@Body request: LoginRequest): Response<AuthResponse>

    @POST("auth/refresh")
    suspend fun refresh(@Body body: Map<String, String>): Response<AuthResponse>

    @GET("categories")
    suspend fun getCategories(): Response<CategoryListResponse>

    @POST("fines")
    suspend fun issueFine(@Body request: IssueFineRequest): Response<FineResponse>

    @GET("fines/my")
    suspend fun getMyFines(@Query("status") status: String? = null): Response<FineListResponse>

    @GET("fines/{fineId}")
    suspend fun getFine(@Path("fineId") fineId: String): Response<FineResponse>

    @GET("fines/lookup")
    suspend fun lookupFine(
        @Query("ref") referenceNumber: String,
        @Query("cat") categoryId: String
    ): Response<FineResponse>

    @POST("payments/initiate")
    suspend fun initiatePayment(@Body request: InitiatePaymentRequest): Response<PaymentResponse>

    @POST("payments/confirm")
    suspend fun confirmPayment(@Body body: Map<String, String>): Response<PaymentResponse>
}
