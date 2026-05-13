package com.example.hotelroll.ui.history

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import com.example.hotelroll.data.model.HistoryEntry
import com.example.hotelroll.repository.HotelRepository
import kotlinx.coroutines.flow.Flow

class HistoryViewModel(
    private val repository: HotelRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val resId: Long = checkNotNull(savedStateHandle["reservationId"])

    val entries: Flow<List<HistoryEntry>> = repository.getHistoryEntries(resId)
}
