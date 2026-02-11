package com.example.hotelroll.ui.createStay

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.hotelroll.repository.HotelRepository
import kotlinx.coroutines.launch
import java.time.LocalDate
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableDoubleStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.SavedStateHandle
import com.example.hotelroll.data.model.Stay
import com.example.hotelroll.data.model.StayStatus
import com.example.hotelroll.data.model.TariffType
import com.example.hotelroll.ui.navigation.StayMode
import java.time.temporal.ChronoUnit


class CreateStayViewModel(
    private val repository: HotelRepository,
    savedStateHandle: SavedStateHandle,

): ViewModel() {


    val roomId: Long =
        checkNotNull(savedStateHandle["roomId"])


    val stayMode: StayMode =
        StayMode.valueOf(checkNotNull(savedStateHandle["mode"]))


    val stayId: Long? = savedStateHandle.get<String>("stayId")?.toLongOrNull()



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


    var tariff by mutableDoubleStateOf(60.0)
        private set
    var errorMessage by mutableStateOf<String?>(null)
        private set

    // for tariff choice tax / .net
    var tariffType by mutableStateOf(TariffType.NET)
        private set

    var stayName by mutableStateOf<String?>(null)
        private set

    var checkOutDate by mutableStateOf(checkInDate.plusDays(1))
        private set

    var resId by mutableStateOf<Long>(0)

    var stayStatus by mutableStateOf(StayStatus.PENDING)
        private set

    init {
        if(stayMode == StayMode.EDIT) {
            viewModelScope.launch {
                val stay = repository.getStayById(stayId!!)
                if(stay == null){
                    errorMessage = "Stay not found"
                    return@launch
                }
                val res = repository.getResById(stay.reservationId)
                resName = res!!.resName // assumes reservation is non-null
                hydrate(stay)

            }
        } else if(stayMode == StayMode.CREATE){
            viewModelScope.launch {
                tariff = repository.getRoomTariff(roomId)
            }
        }
    }

    // prefills the values with the existing stay
    private fun hydrate(stay: Stay) {
            stayName = stay.stayName
            peopleInRoom = stay.peopleInRoom
            kidsInRoom = stay.kidsInRoom
            checkInDate = stay.checkInDate
            checkOutDate = stay.checkOutDate
            nights = ChronoUnit.DAYS.between(checkInDate, checkOutDate).toInt()
            notes = stay.notes
            tariff = stay.tariff
            tariffType = stay.tariffType
            resId = stay.reservationId
            stayStatus = stay.status
    }

    fun onResNameChange(v: String) { resName = v }
    fun onKidsInRoomChange(v: Int) { kidsInRoom = v }
    fun onPeopleInRoomChange(v: Int) { peopleInRoom = v }

    fun onCheckInDateChange(v: LocalDate) {
        checkInDate = v
        if(v.isAfter(checkOutDate)){
            checkOutDate = checkInDate.plusDays(1)
        }
    }

    fun onNightsChange(v: Int) {
        if(v >0){
            nights = v
            checkOutDate = checkInDate.plusDays(nights.toLong())

        }
    }
    fun onNotesChange(v: String) { notes = v }
    fun onTariffChange(v: Double) {tariff = v}

    fun onCheckOutDateChange(v: LocalDate) {
        if (v.isAfter(checkInDate)) {
            checkOutDate = v
        }
        nights = ChronoUnit.DAYS.between(checkInDate, checkOutDate).toInt()
    }

    fun onStayNameChange(v: String?){stayName = v}

    fun onStayStatusChange(v: StayStatus){stayStatus = v}

    fun onTariffTypeChange(type: TariffType) {
        tariffType = type
    }


    fun reset() {
        resName = ""
        peopleInRoom = 1
        kidsInRoom = 0
        nights = 1
        notes = ""
        tariff = tariff
        errorMessage = null
        tariffType = TariffType.NET
    }

    fun save(onSuccess: () -> Unit) {
        viewModelScope.launch {
                when(stayMode) {
                    StayMode.CREATE -> {
                        when(
                           repository.createReservationWithStay(
                                resName,
                                peopleInRoom,
                                kidsInRoom,
                                notes,
                                checkInDate,
                                nights,
                                roomNumber,
                                tariff,
                                tariffType,
                                stayName)
                        ){
                            is HotelRepository.AssignRoomResult.Success -> {
                                onSuccess()
                            }

                            HotelRepository.AssignRoomResult.InvalidNights -> {
                                errorMessage = "Invalid amount of nights"
                            }

                            HotelRepository.AssignRoomResult.Overlapping -> {
                                errorMessage = "Room unavailable for dates"
                            }

                            else -> errorMessage = "Unexpected Error"

                        }
                    }
                    StayMode.EDIT -> {
                        val stay = Stay(
                            stayId = stayId!!,
                            reservationId = resId,
                            roomId = roomId,
                            peopleInRoom = peopleInRoom,
                            kidsInRoom = kidsInRoom,
                            checkInDate = checkInDate,
                            checkOutDate = checkOutDate,
                            tariff = tariff,
                            tariffType = tariffType,
                            notes = notes,
                            stayName = stayName,
                            status = stayStatus
                        )
                        when (repository.updateStay(stay)){

                            is HotelRepository.StayResult.Success -> {
                                onSuccess()
                            }

                            HotelRepository.StayResult.Overlapping -> {
                                errorMessage = "Room not available for dates"
                            }
                        }

                    }
                }
        }
    }


}