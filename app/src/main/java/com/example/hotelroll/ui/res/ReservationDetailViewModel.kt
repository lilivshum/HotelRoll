package com.example.hotelroll.ui.res

import com.example.hotelroll.data.model.Stay
import com.example.hotelroll.data.dao.StayDao
import androidx.lifecycle.ViewModel
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import androidx.compose.runtime.State
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.SavedStateHandle
import com.example.hotelroll.HotelApplication
import com.example.hotelroll.data.model.Reservation
import com.example.hotelroll.repository.HotelRepository
import com.example.hotelroll.ui.utilities.StayUi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged

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


    init{
        loadReservationInfo()

        viewModelScope.launch {
            notesFlow
                .debounce(500)        // wait for typing to stop
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
        }
    }

    fun onNotesChanged(text: String){
        notesFlow.value = text
    }
}
