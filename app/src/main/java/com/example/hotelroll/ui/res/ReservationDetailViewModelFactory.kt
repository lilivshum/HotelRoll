package com.example.hotelroll.ui.res

import androidx.lifecycle.AbstractSavedStateViewModelFactory
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModel
import androidx.savedstate.SavedStateRegistryOwner
import com.example.hotelroll.repository.HotelRepository

class ReservationDetailViewModelFactory(
    private val repository: HotelRepository,
    private val reservationId: Long
) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ReservationDetailViewModel::class.java)) {
            return ReservationDetailViewModel(
                repository = repository,
                savedStateHandle = SavedStateHandle(
                    mapOf("reservationId" to reservationId)
                )
            ) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
