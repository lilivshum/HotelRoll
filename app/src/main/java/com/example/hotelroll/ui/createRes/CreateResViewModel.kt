package com.example.hotelroll.ui.createRes

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.hotelroll.data.model.User
import com.example.hotelroll.repository.HotelRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.temporal.ChronoUnit



class CreateResViewModel (
    private val repository: HotelRepository
): ViewModel() {

    val activeUser: StateFlow<User?> = repository.activeUser
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    var resName by mutableStateOf("")
        private set

    var checkInDate by mutableStateOf(LocalDate.now())
        private set

    var checkOutDate by mutableStateOf(checkInDate.plusDays(1))
        private set

    var nights by mutableStateOf(1)
        private set

    var noGuests by mutableStateOf(1)
        private set

    var noKids by mutableStateOf(0)
        private set

    var notes by mutableStateOf("")
        private set

    var errorMessage by mutableStateOf<String?>(null)
        private set

    fun onResNameChange(v: String) { resName = v }
    fun onCheckInDateChange(v: LocalDate ) {
        checkInDate = v
        if (!checkOutDate.isAfter(v)) {
            checkOutDate = v.plusDays(1)
        }
        nights = ChronoUnit.DAYS.between(checkInDate, checkOutDate).toInt()
    }

    fun onCheckOutDateChange(v: LocalDate) {
        if(v.isAfter(checkInDate)) {
            checkOutDate = v

            nights = ChronoUnit.DAYS.between(checkInDate, checkOutDate).toInt()
        }
    }
    fun onGuestsChange(v: Int) {noGuests = v}
    fun onKidsChange(v: Int){noKids = v}
    fun onNotesChange(v: String) {notes = v}

    fun reset(){
        resName = ""
        checkInDate = LocalDate.now()
        checkOutDate = checkInDate.plusDays(1)
        nights = 1
        noGuests = 1
        noKids = 0
        notes = ""
        errorMessage = null
    }

    fun save(onSuccess: () -> Unit) {
        viewModelScope.launch {
            try {
                repository.createReservation(
                    resName,
                    noGuests,
                    noKids,
                    notes,
                    checkInDate,
                    nights
                )
                onSuccess()
            } catch (e: Exception) {
                errorMessage = e.message
            }
        }
    }

}