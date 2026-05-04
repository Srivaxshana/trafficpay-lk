package com.slpolice.trafficfineapp.ui.issuefine

import android.annotation.SuppressLint
import android.content.Context
import android.location.LocationManager
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.slpolice.trafficfineapp.model.FineCategory
import com.slpolice.trafficfineapp.model.FineResponse
import com.slpolice.trafficfineapp.model.IssueFineRequest
import com.slpolice.trafficfineapp.network.ApiService
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class IssueFineViewModel @Inject constructor(
    private val api: ApiService,
    @ApplicationContext private val context: Context
) : ViewModel() {

    private val _categories = MutableLiveData<List<FineCategory>>()
    val categories: LiveData<List<FineCategory>> = _categories

    private val _location = MutableLiveData<Pair<Double, Double>>()
    val location: LiveData<Pair<Double, Double>> = _location

    private val _issueState = MutableLiveData<IssueState>()
    val issueState: LiveData<IssueState> = _issueState

    fun loadCategories() {
        viewModelScope.launch {
            try {
                val response = api.getCategories()
                if (response.isSuccessful) {
                    _categories.value = response.body()?.categories ?: emptyList()
                }
            } catch (e: Exception) {
                _issueState.value = IssueState.Error("Failed to load categories")
            }
        }
    }

    @SuppressLint("MissingPermission")
    fun fetchLocation() {
        try {
            val locationManager = context.getSystemService(Context.LOCATION_SERVICE) as LocationManager
            val location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER)
                ?: locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER)
            location?.let { _location.value = Pair(it.latitude, it.longitude) }
        } catch (e: Exception) {
            // Location not available, continue without it
        }
    }

    fun issueFine(
        categoryId: String,
        vehicleNumber: String,
        vehicleType: String,
        driverNic: String,
        driverName: String,
        driverPhone: String?,
        location: String?
    ) {
        if (vehicleNumber.isBlank() || driverNic.isBlank() || driverName.isBlank()) {
            _issueState.value = IssueState.Error("Please fill all required fields")
            return
        }

        _issueState.value = IssueState.Loading
        viewModelScope.launch {
            try {
                val loc = _location.value
                val request = IssueFineRequest(
                    categoryId = categoryId,
                    vehicleNumber = vehicleNumber.uppercase(),
                    vehicleType = vehicleType,
                    driverNicNumber = driverNic,
                    driverName = driverName,
                    driverPhone = driverPhone,
                    location = location,
                    latitude = loc?.first,
                    longitude = loc?.second
                )
                val response = api.issueFine(request)
                if (response.isSuccessful) {
                    _issueState.value = IssueState.Success(response.body()!!)
                } else {
                    _issueState.value = IssueState.Error("Failed to issue fine")
                }
            } catch (e: Exception) {
                _issueState.value = IssueState.Error("Network error: ${e.message}")
            }
        }
    }

    sealed class IssueState {
        object Loading : IssueState()
        data class Success(val fine: FineResponse) : IssueState()
        data class Error(val message: String) : IssueState()
    }
}
