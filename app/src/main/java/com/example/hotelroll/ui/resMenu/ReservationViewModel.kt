package com.example.hotelroll.ui.resMenu

import com.example.hotelroll.data.model.Reservation
import com.example.hotelroll.repository.HotelRepository
import androidx.lifecycle.ViewModel
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import androidx.compose.runtime.State
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn


class ReservationViewModel(
    private val repository: HotelRepository
) : ViewModel() {

    val reservations: StateFlow<List<Reservation>> =
        repository.getAllReservationsSnapshot()
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5_000),
                initialValue = emptyList()
            )
    }
