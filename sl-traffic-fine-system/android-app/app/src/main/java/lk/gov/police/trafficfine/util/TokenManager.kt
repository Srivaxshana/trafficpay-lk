package com.slpolice.trafficfineapp.util

import android.content.Context
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class TokenManager @Inject constructor(@ApplicationContext context: Context) {

    private val masterKey = MasterKey.Builder(context)
        .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
        .build()

    private val prefs = EncryptedSharedPreferences.create(
        context,
        "traffic_fine_prefs",
        masterKey,
        EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
        EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
    )

    fun saveToken(token: String) = prefs.edit().putString(KEY_TOKEN, token).apply()
    fun saveRefreshToken(token: String) = prefs.edit().putString(KEY_REFRESH, token).apply()
    fun saveBadgeNumber(badge: String) = prefs.edit().putString(KEY_BADGE, badge).apply()
    fun saveRole(role: String) = prefs.edit().putString(KEY_ROLE, role).apply()
    fun saveDistrict(district: String) = prefs.edit().putString(KEY_DISTRICT, district).apply()
    fun saveFullName(name: String) = prefs.edit().putString(KEY_NAME, name).apply()

    fun getToken(): String? = prefs.getString(KEY_TOKEN, null)
    fun getRefreshToken(): String? = prefs.getString(KEY_REFRESH, null)
    fun getBadgeNumber(): String? = prefs.getString(KEY_BADGE, null)
    fun getRole(): String? = prefs.getString(KEY_ROLE, null)
    fun getDistrict(): String? = prefs.getString(KEY_DISTRICT, null)
    fun getFullName(): String? = prefs.getString(KEY_NAME, null)

    fun isLoggedIn(): Boolean = getToken() != null

    fun clear() = prefs.edit().clear().apply()

    companion object {
        private const val KEY_TOKEN = "jwt_token"
        private const val KEY_REFRESH = "refresh_token"
        private const val KEY_BADGE = "badge_number"
        private const val KEY_ROLE = "role"
        private const val KEY_DISTRICT = "district"
        private const val KEY_NAME = "full_name"
    }
}
