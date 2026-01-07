package com.example.hotelroll.ui.stay

import androidx.lifecycle.SavedStateHandle
import com.example.hotelroll.repository.HotelRepository
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModel

class StayDetailViewModelFactory(
    private val repository: HotelRepository,
    private val stayId: Long
) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return StayDetailViewModel(
            repository = repository,
            savedStateHandle = SavedStateHandle(
                mapOf("stayId" to stayId)
            )
        ) as T
    }
}

