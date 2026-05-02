package com.slpolice.trafficfineapp.ui.login

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.slpolice.trafficfineapp.model.LoginRequest
import com.slpolice.trafficfineapp.network.ApiService
import com.slpolice.trafficfineapp.util.TokenManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val api: ApiService,
    private val tokenManager: TokenManager
) : ViewModel() {

    private val _loginState = MutableLiveData<LoginState>()
    val loginState: LiveData<LoginState> = _loginState

    fun login(badgeNumber: String, password: String) {
        _loginState.value = LoginState.Loading
        viewModelScope.launch {
            try {
                val response = api.login(LoginRequest(badgeNumber, password))
                if (response.isSuccessful) {
                    val auth = response.body()!!
                    tokenManager.saveToken(auth.token)
                    auth.refreshToken?.let { tokenManager.saveRefreshToken(it) }
                    auth.user?.let { user ->
                        tokenManager.saveBadgeNumber(user.badgeNumber)
                        tokenManager.saveRole(user.role)
                        tokenManager.saveDistrict(user.district)
                        tokenManager.saveFullName(user.fullName)
                    }
                    _loginState.value = LoginState.Success
                } else {
                    _loginState.value = LoginState.Error("Invalid credentials")
                }
            } catch (e: Exception) {
                _loginState.value = LoginState.Error("Network error: ${e.message}")
            }
        }
    }

    sealed class LoginState {
        object Loading : LoginState()
        object Success : LoginState()
        data class Error(val message: String) : LoginState()
    }
}
