package com.example.hotelroll.ui.roll

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.hotelroll.data.dto.RollItem
import com.example.hotelroll.repository.HotelRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.stateIn
import java.time.LocalDate

@OptIn(ExperimentalCoroutinesApi::class)
class  RollViewModel(
    private val repository: HotelRepository
) : ViewModel() {

    private val _date = MutableStateFlow(LocalDate.now())
    val date: StateFlow<LocalDate> = _date

    val roll: StateFlow<List<RollItem>> =
        _date.flatMapLatest { date ->
            flow { emit(repository.getRoomRoll(date)) }
        }.stateIn(
            viewModelScope,
            SharingStarted.Companion.WhileSubscribed(5000),
            emptyList()
        )

    fun nextDay() { _date.value = _date.value.plusDays(1) }
    fun prevDay() { _date.value = _date.value.minusDays(1) }
}