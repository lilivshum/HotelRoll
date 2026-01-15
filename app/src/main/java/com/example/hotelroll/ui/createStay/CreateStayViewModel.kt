package com.example.hotelroll.ui.createStay

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.hotelroll.repository.HotelRepository
import kotlinx.coroutines.launch
import java.time.LocalDate
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.lifecycle.SavedStateHandle


class CreateStayViewModel(
    private val repository: HotelRepository,
    savedStateHandle: SavedStateHandle,

): ViewModel() {

    val roomId: Long =
        checkNotNull(savedStateHandle["roomId"])


    var resName by mutableStateOf("")
        private set

    val roomNumber: String =
        checkNotNull(savedStateHandle["roomNumber"])

    val date: LocalDate =
        LocalDate.parse(checkNotNull(savedStateHandle["date"]))

    // in this case we assume same as noGuests (Stay + Res done together)
    var peopleInRoom by mutableStateOf(1)
        private set

    var kidsInRoom by mutableStateOf(0)
        private set

    var checkInDate by mutableStateOf(date)
        private set
    var nights by mutableStateOf(1)
        private set

    var notes by mutableStateOf<String?>(null)
        private set

    var tariff by mutableStateOf(60.0)
        private set
    var errorMessage by mutableStateOf<String?>(null)
        private set

    fun onResNameChange(v: String) { resName = v }
    fun onKidsInRoomChange(v: Int) { kidsInRoom = v }
    fun onPeopleInRoomChange(v: Int) { peopleInRoom = v }
    fun checkInDateChange(v: LocalDate) { checkInDate = v}
    fun onNightsChange(v: Int) { nights = v }
    fun onNotesChange(v: String) { notes = v }
    fun onTariffChange(v: Double) {tariff = v}

    fun reset() {
        resName = ""
        peopleInRoom = 1
        kidsInRoom = 0
        nights = 1
        notes = ""
        tariff = 60.0
        errorMessage = null
    }

    fun save(onSuccess: () -> Unit) {
        viewModelScope.launch {
            try {
                repository.createReservationWithStay(
                    resName,
                    peopleInRoom,
                    kidsInRoom,
                    notes,
                    checkInDate,
                    nights,
                    roomNumber,
                    tariff,
                )
                onSuccess()
            } catch (e: Exception) {
                errorMessage = e.message
            }
        }
    }

}