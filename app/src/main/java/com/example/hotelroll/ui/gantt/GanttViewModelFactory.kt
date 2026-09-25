package com.example.hotelroll.ui.gantt

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.hotelroll.repository.HotelRepository
import com.example.hotelroll.ui.settings.PREFS_NAME

class GanttViewModelFactory(
    private val repository: HotelRepository,
    private val context: Context
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        @Suppress("UNCHECKED_CAST")
        return GanttViewModel(repository, prefs) as T
    }
}
