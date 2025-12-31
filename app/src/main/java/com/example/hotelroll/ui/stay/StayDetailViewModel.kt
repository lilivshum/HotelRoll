package com.example.hotelroll.ui.stay

import androidx.compose.runtime.collectAsState
import com.example.hotelroll.repository.HotelRepository
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import com.example.hotelroll.data.model.Stay
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collect

@OptIn(ExperimentalCoroutinesApi::class)
class StayDetailViewModel (
    private val repository: HotelRepository

): ViewModel() {

    private val _stay = MutableStateFlow<Stay?>(null)
    val stay: StateFlow<Stay?> = _stay

    fun loadStay(stayId: Long) {
        viewModelScope.launch {
            _stay.value = repository.getStayById(stayId)
        }
    }
}