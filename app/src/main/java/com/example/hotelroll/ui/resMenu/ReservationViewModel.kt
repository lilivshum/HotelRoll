package com.example.hotelroll.ui.resMenu

import com.example.hotelroll.data.model.Reservation
import com.example.hotelroll.data.model.User
import com.example.hotelroll.repository.HotelRepository
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.time.LocalDate

class ReservationViewModel(
    private val repository: HotelRepository
) : ViewModel() {

    private val today = LocalDate.now()

    val activeReservations: StateFlow<List<Reservation>> =
        repository.getActiveReservations(today)
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5_000),
                initialValue = emptyList()
            )

    val pastReservations: StateFlow<List<Reservation>> =
        repository.getPastReservations(today)
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5_000),
                initialValue = emptyList()
            )

    val allUsers: StateFlow<List<User>> =
        repository.allUsers
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5_000),
                initialValue = emptyList()
            )

    val activeUser: StateFlow<User?> =
        combine(repository.activeUserId, repository.allUsers) { id, users ->
            users.find { it.id == id }
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = null
        )

    fun switchUser(user: User) {
        repository.setActiveUser(user.id)
    }
}
