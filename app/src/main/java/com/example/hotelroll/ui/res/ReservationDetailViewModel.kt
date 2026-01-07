package com.example.hotelroll.ui.res

import com.example.hotelroll.data.model.Stay
import com.example.hotelroll.data.dao.StayDao
import androidx.lifecycle.ViewModel
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import androidx.compose.runtime.State
import com.example.hotelroll.data.model.Reservation
import com.example.hotelroll.repository.HotelRepository
import java.time.LocalDate

data class StayUi(
    val roomNumber: String,
    val people: Int,
    val checkIn: LocalDate,
    val checkOut: LocalDate,
    val stayId: Long
)
class ReservationDetailViewModel(
    private val repository: HotelRepository
) : ViewModel() {

    private val _stays = mutableStateOf<List<Stay>>(emptyList())
    val stays: State<List<Stay>> = _stays

    private val _reservation = mutableStateOf<Reservation?>(null)
    val reservation: State<Reservation?> = _reservation

    private val _staysUi = mutableStateOf<List<StayUi>>(emptyList())
    val staysUi: State<List<StayUi>> = _staysUi

    fun loadReservationInfo(reservationId: Long) {
        viewModelScope.launch {
            _stays.value = repository.getStayByResId(reservationId)
            _reservation.value = repository.getResById(reservationId)

            val uilist = stays.value.map {stay ->
                StayUi (
                    roomNumber = repository.getRoomNumberById(stay.roomId),
                    people = stay.peopleInRoom,
                    checkIn = stay.checkInDate,
                    checkOut = stay.checkOutDate,
                    stayId = stay.stayId
                )

            }

            _staysUi.value = uilist
        }
    }
}
