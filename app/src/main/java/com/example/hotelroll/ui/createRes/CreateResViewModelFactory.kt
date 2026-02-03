package com.example.hotelroll.ui.createRes

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.createSavedStateHandle
import androidx.lifecycle.viewmodel.CreationExtras
import com.example.hotelroll.repository.HotelRepository
import com.example.hotelroll.ui.createRes.CreateResViewModel

class CreateResViewModelFactory(
    private val repository: HotelRepository,
) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(
        modelClass: Class<T>,
        extras: CreationExtras
    ): T {
        val savedStateHandle = extras.createSavedStateHandle()

        if (modelClass.isAssignableFrom(CreateResViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return CreateResViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
