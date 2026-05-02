package com.slpolice.trafficfineapp.di

import com.slpolice.trafficfineapp.network.ApiService
import com.slpolice.trafficfineapp.network.RetrofitClient
import com.slpolice.trafficfineapp.util.TokenManager
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideApiService(tokenManager: TokenManager): ApiService =
        RetrofitClient.create(tokenManager)
}
