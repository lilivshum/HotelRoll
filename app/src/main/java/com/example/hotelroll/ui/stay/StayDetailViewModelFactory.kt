package com.example.hotelroll.ui.stay

import com.example.hotelroll.repository.HotelRepository
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModel

class StayDetailViewModelFactory(
    private val repository: HotelRepository,
    ) : ViewModelProvider.Factory {

        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(StayDetailViewModel::class.java)) {
                @Suppress("UNCHECKED_CAST")
                return StayDetailViewModel(repository) as T
            }
            throw IllegalArgumentException("Unknown ViewModel class")
        }
    }
