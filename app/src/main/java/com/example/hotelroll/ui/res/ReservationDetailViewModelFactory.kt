package com.example.hotelroll.ui.res

import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModel
import com.example.hotelroll.repository.HotelRepository

class ReservationDetailViewModelFactory(
    private val repository: HotelRepository
) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ReservationDetailViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return ReservationDetailViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
