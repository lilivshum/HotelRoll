package com.example.hotelroll.ui.resMenu

import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModel
import com.example.hotelroll.repository.HotelRepository

class ReservationViewModelFactory(
    private val repository: HotelRepository
) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ReservationViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return ReservationViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
