package com.example.hotelroll.ui.roll

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.hotelroll.data.dto.RollItem
import com.example.hotelroll.data.model.User
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

    val activeUser: StateFlow<User?> = repository.activeUser
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

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

    var showGantt by mutableStateOf(false)
        private set

    fun toggleGantt() {
        showGantt = !showGantt
        if (showGantt) clearSelection()
    }

    // action picker state — shown when long-pressing a stay mid-stay
    var pendingActionItem by mutableStateOf<RollItem?>(null)
        private set
    var pendingActionDate by mutableStateOf<LocalDate?>(null)
        private set

    fun onLongPress(item: RollItem, date: LocalDate) {
        if (date == item.checkInDate) {
            // at check-in date: skip dialog, full move directly
            selectItem(item, date)
        } else {
            pendingActionItem = item
            pendingActionDate = date
        }
    }

    fun confirmAction(splitDate: LocalDate?) {
        val item = pendingActionItem ?: return
        val date = splitDate ?: item.checkInDate!!
        pendingActionItem = null
        pendingActionDate = null
        selectItem(item, date)
    }

    fun dismissActionPicker() {
        pendingActionItem = null
        pendingActionDate = null
    }

    // tap-to-select, tap-to-place room move
    var selectedItem by mutableStateOf<RollItem?>(null)
        private set

    var splitDate by mutableStateOf<LocalDate?>(null)
        private set

    var validTargetRoomIds by mutableStateOf<Set<Long>>(emptySet())
        private set

    fun selectItem(item: RollItem, date: LocalDate) {
        selectedItem = item
        splitDate = date
        validTargetRoomIds = emptySet()
        viewModelScope.launch {
            val blocked = repository.getBlockedRoomIds(
                checkIn = date,
                checkOut = item.checkOutDate!!,
                excludeStayId = item.stayId!!
            ).toSet()
            val allRoomIds = roll.value.map { it.roomId }.toSet()
            validTargetRoomIds = allRoomIds - blocked - item.roomId
        }
    }

    var pendingMoveTarget by mutableStateOf<RollItem?>(null)
        private set

    fun requestMoveTarget(item: RollItem) {
        pendingMoveTarget = item
    }

    fun clearPendingMove() {
        pendingMoveTarget = null
    }

    fun clearSelection() {
        selectedItem = null
        splitDate = null
        validTargetRoomIds = emptySet()
        pendingMoveTarget = null
    }

    fun moveStayToRoom(
        item: RollItem,
        newRoomId: Long,
        onResult: (Boolean) -> Unit
    ) {
        viewModelScope.launch {
            val split = splitDate
            val success = if (split == null || split == item.checkInDate) {
                repository.tryMoveStay(
                    stayId = item.stayId!!,
                    checkInDate = item.checkInDate!!,
                    checkOutDate = item.checkOutDate!!,
                    newRoomId = newRoomId
                )
            } else {
                repository.splitAndMoveStay(
                    stayId = item.stayId!!,
                    splitDate = split,
                    newRoomId = newRoomId
                )
            }
            onResult(success)
        }
    }
}
