package com.example.hotelroll.ui.resMenu

import com.example.hotelroll.data.model.Reservation
import com.example.hotelroll.repository.HotelRepository
import androidx.lifecycle.ViewModel
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import androidx.compose.runtime.State
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow


class ReservationViewModel(
    private val repository: HotelRepository
) : ViewModel() {

    private val _reservations = mutableStateOf<List<Reservation>>(emptyList())
    val reservations: State<List<Reservation>> = _reservations

    init {
        loadReservations()
    }

    private fun loadReservations() {
        viewModelScope.launch {
            _reservations.value = repository.getAllReservationsSnapshot()
        }
    }
}
