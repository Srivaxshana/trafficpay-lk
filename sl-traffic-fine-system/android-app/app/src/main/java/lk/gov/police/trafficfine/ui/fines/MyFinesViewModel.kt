package com.slpolice.trafficfineapp.ui.fines

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.slpolice.trafficfineapp.model.FineResponse
import com.slpolice.trafficfineapp.network.ApiService
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MyFinesViewModel @Inject constructor(private val api: ApiService) : ViewModel() {

    private val _fines = MutableLiveData<List<FineResponse>>()
    val fines: LiveData<List<FineResponse>> = _fines

    private val _loading = MutableLiveData<Boolean>()
    val loading: LiveData<Boolean> = _loading

    fun loadFines(status: String) {
        _loading.value = true
        viewModelScope.launch {
            try {
                val response = api.getMyFines(status)
                if (response.isSuccessful) {
                    _fines.value = response.body()?.fines ?: emptyList()
                }
            } catch (e: Exception) {
                _fines.value = emptyList()
            } finally {
                _loading.value = false
            }
        }
    }
}
