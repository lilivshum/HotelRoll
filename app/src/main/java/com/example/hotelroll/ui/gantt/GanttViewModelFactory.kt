package com.example.hotelroll.ui.gantt

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.hotelroll.repository.HotelRepository

class GanttViewModelFactory(private val repository: HotelRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        @Suppress("UNCHECKED_CAST")
        return GanttViewModel(repository) as T
    }
}
