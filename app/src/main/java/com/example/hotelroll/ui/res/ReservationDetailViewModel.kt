package com.example.hotelroll.ui.res

import com.example.hotelroll.data.model.Stay
import com.example.hotelroll.data.dao.StayDao
import androidx.lifecycle.ViewModel
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import androidx.compose.runtime.State
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.SavedStateHandle
import com.example.hotelroll.HotelApplication
import com.example.hotelroll.data.model.Reservation
import com.example.hotelroll.repository.HotelRepository
import com.example.hotelroll.ui.utilities.StayUi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
import java.time.LocalDate

@OptIn(FlowPreview::class)
class ReservationDetailViewModel(
    private val repository: HotelRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val resId: Long =
        checkNotNull(savedStateHandle["reservationId"]) {
            "reservation Id is required"
        }
    private val _stays = mutableStateOf<List<Stay>>(emptyList())
    val stays: State<List<Stay>> = _stays

    private val _reservation = mutableStateOf<Reservation?>(null)
    val reservation: State<Reservation?> = _reservation

    private val _staysUi = mutableStateOf<List<StayUi>>(emptyList())
    val staysUi: State<List<StayUi>> = _staysUi

    // For saveable notes on the spot
    private val notesFlow = MutableStateFlow("")
    val notes = notesFlow.asStateFlow()

    var errorMessage by mutableStateOf<String?>(null)
        private set

    var hasConfirmedStay by mutableStateOf(false)
        private set

    // Edit mode fields
    var resName by mutableStateOf("")
    var noGuests by mutableStateOf(1)
    var noKids by mutableStateOf(0)
    var checkInDate by mutableStateOf(LocalDate.now())
    var nights by mutableStateOf(1)
    val checkOutDate: LocalDate get() = checkInDate.plusDays(nights.toLong())

    fun onCheckOutDateChange(date: LocalDate) {
        val n = java.time.temporal.ChronoUnit.DAYS.between(checkInDate, date).toInt()
        if (n > 0) nights = n
    }

    init{
        loadReservationInfo()

        viewModelScope.launch {
            notesFlow
                .debounce(500)
                .distinctUntilChanged()
                .collect { newNotes ->
                    val current = reservation.value ?: return@collect
                    repository.updateReservationNotes(current.id, newNotes)
                }
        }
    }

    fun loadReservationInfo() {
        viewModelScope.launch {
            _stays.value = repository.getStayByResId(resId)
            _reservation.value = repository.getResById(resId)
            _staysUi.value = repository.getStaysUi(resId)
            notesFlow.value = _reservation.value?.notes.orEmpty()
            hasConfirmedStay = repository.hasConfirmedStay(resId)
            // seed edit fields from loaded reservation
            _reservation.value?.let { res ->
                resName = res.resName
                noGuests = res.noGuests
                noKids = res.noKids
                checkInDate = res.checkInDate
                nights = res.nights
            }
        }
    }

    fun onNotesChanged(text: String){
        notesFlow.value = text
    }

    fun saveEdits(onDone: () -> Unit = {}) {
        val current = _reservation.value ?: return
        viewModelScope.launch {
            try {
                repository.updateReservation(
                    current.copy(
                        resName = resName,
                        noGuests = noGuests,
                        noKids = noKids,
                        checkInDate = checkInDate,
                        nights = nights
                    )
                )
                _reservation.value = repository.getResById(resId)
                onDone()
            } catch (e: Exception) {
                errorMessage = e.message
            }
        }
    }

    fun closeReservation(onDone: () -> Unit = {}) {
        viewModelScope.launch {
            try {
                repository.deactivateReservation(resId)
                onDone()
            } catch (e: Exception) {
                errorMessage = e.message
            }
        }
    }

}
