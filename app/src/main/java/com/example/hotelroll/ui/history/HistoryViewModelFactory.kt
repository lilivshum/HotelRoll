package com.example.hotelroll.ui.history

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.hotelroll.repository.HotelRepository

class HistoryViewModelFactory(
    private val repository: HotelRepository,
    private val reservationId: Long
) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(HistoryViewModel::class.java)) {
            return HistoryViewModel(
                repository = repository,
                savedStateHandle = SavedStateHandle(mapOf("reservationId" to reservationId))
            ) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
