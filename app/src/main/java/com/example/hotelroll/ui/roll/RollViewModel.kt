package com.example.hotelroll.ui.roll

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.hotelroll.data.dto.RollItem
import com.example.hotelroll.repository.HotelRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.time.LocalDate

@OptIn(ExperimentalCoroutinesApi::class)
class RollViewModel(
    private val repository: HotelRepository
) : ViewModel() {

    private val _date = MutableStateFlow(LocalDate.now())
    val date: StateFlow<LocalDate> = _date

    val roll: StateFlow<List<RollItem>> =
        _date.flatMapLatest { date ->
            repository.getRoomRoll(date)
        }.stateIn(
            viewModelScope,
            SharingStarted.Companion.WhileSubscribed(5000),
            emptyList()
        )

    fun onDateChange(newDate: LocalDate) {
        _date.value = newDate
    }

    fun nextDay() { _date.value = _date.value.plusDays(1) }
    fun prevDay() { _date.value = _date.value.minusDays(1) }

    // tap-to-select, tap-to-place room move
    var selectedItem by mutableStateOf<RollItem?>(null)
        private set

    fun selectItem(item: RollItem) {
        selectedItem = item
    }

    fun clearSelection() {
        selectedItem = null
    }

    fun moveStayToRoom(
        item: RollItem,
        newRoomId: Long,
        onResult: (Boolean) -> Unit
    ) {
        viewModelScope.launch {
            val success = repository.tryMoveStay(
                stayId = item.stayId!!,
                checkInDate = item.checkInDate!!,
                checkOutDate = item.checkOutDate!!,
                newRoomId = newRoomId
            )
            onResult(success)
        }
    }
}
