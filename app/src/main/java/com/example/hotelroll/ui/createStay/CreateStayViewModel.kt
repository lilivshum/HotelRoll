package com.example.hotelroll.ui.createStay

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.hotelroll.repository.HotelRepository
import kotlinx.coroutines.launch
import java.time.LocalDate
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableDoubleStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.lifecycle.SavedStateHandle
import com.example.hotelroll.data.model.Reservation
import com.example.hotelroll.data.model.Stay
import com.example.hotelroll.data.model.StayStatus
import com.example.hotelroll.data.model.TariffType
import com.example.hotelroll.ui.navigation.StayMode
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.stateIn
import java.time.temporal.ChronoUnit
import com.example.hotelroll.data.model.Currency
import com.example.hotelroll.data.model.RoomEntity
import com.example.hotelroll.data.model.User

class CreateStayViewModel(
    private val repository: HotelRepository,
    savedStateHandle: SavedStateHandle,

): ViewModel() {

    val activeUser: StateFlow<User?> = repository.activeUser
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    var roomId: Long by mutableStateOf(checkNotNull(savedStateHandle["roomId"]))
        private set

    val stayMode: StayMode =
        StayMode.valueOf(checkNotNull(savedStateHandle["mode"]))

    val stayId: Long? = savedStateHandle.get<String>("stayId")?.toLongOrNull()
    private val reservationIdArg: Long? = savedStateHandle.get<String>("reservationId")?.toLongOrNull()

    var reservationIsLocked by mutableStateOf(false)
        private set

    var resName by mutableStateOf("")
        private set

    var roomNumber: String by mutableStateOf(checkNotNull(savedStateHandle["roomNumber"]))
        private set

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

    // for reservation completion
    private val _selectedReservation = MutableStateFlow<Reservation?>(null)
    val selectedReservation: StateFlow<Reservation?> = _selectedReservation

    private val _reservationName = MutableStateFlow("")
    val reservationName: StateFlow<String> = _reservationName

    @OptIn(FlowPreview::class, ExperimentalCoroutinesApi::class)
    val matchingReservations: StateFlow<List<Reservation>> =
        _reservationName
            .debounce(300) // optional but recommended
            .distinctUntilChanged()
            .flatMapLatest { query ->
                if (query.isBlank()) {
                    flowOf(emptyList())
                } else {
                    repository.searchReservations(query)
                }
            }
            .stateIn(
                viewModelScope,
                SharingStarted.WhileSubscribed(5000),
                emptyList()
            )

    // currency change value
    var currency by mutableStateOf(Currency.USD)
        private set

    // auto-saving notes flow for EDIT mode (same debounce pattern as StayDetailViewModel)
    private val _editNotesFlow = MutableStateFlow("")
    val editNotes: StateFlow<String> = _editNotesFlow

    fun onEditNotesChange(v: String) { _editNotesFlow.value = v }

    // originals stored after hydrate — used to detect unsaved changes
    private var origStayName: String? = null
    private var origPeopleInRoom: Int = 1
    private var origKidsInRoom: Int = 0
    private var origCheckInDate: LocalDate = date
    private var origCheckOutDate: LocalDate = date.plusDays(1)
    private var origTariff: Double = 60.0
    private var origTariffType: TariffType = TariffType.NET
    private var origNotes: String? = null
    private var origCurrency: Currency = Currency.USD

    val hasUnsavedChanges: Boolean
        get() = stayMode == StayMode.EDIT && (
            stayName != origStayName ||
            peopleInRoom != origPeopleInRoom ||
            kidsInRoom != origKidsInRoom ||
            checkInDate != origCheckInDate ||
            checkOutDate != origCheckOutDate ||
            tariff != origTariff ||
            tariffType != origTariffType ||
            notes != origNotes ||
            currency != origCurrency
        )


    init {
        if(stayMode == StayMode.EDIT) {
            viewModelScope.launch {
                val stay = repository.getStayById(stayId!!)
                if(stay == null){
                    errorMessage = "Stay not found"
                    return@launch
                }
                val res = repository.getResById(stay.reservationId)
                _reservationName.value = res!!.resName // assumes reservation is non-null
                hydrate(stay)
            }

            viewModelScope.launch {
                _editNotesFlow
                    .debounce(500)
                    .distinctUntilChanged()
                    .collect { newNotes ->
                        stayId?.let { repository.updateStayNotes(it, newNotes) }
                    }
            }
        } else if(stayMode == StayMode.CREATE){
            viewModelScope.launch {
                tariff = repository.getRoomTariff(roomId)
                reservationIdArg?.let { preResId ->
                    val res = repository.getResById(preResId)
                    res?.let {
                        onReservationSelected(it)
                        reservationIsLocked = true
                    }
                }
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
            currency = stay.currency

            // seed auto-save notes flow for EDIT mode
            _editNotesFlow.value = stay.notes.orEmpty()

            // snapshot originals for unsaved-changes detection
            origStayName = stay.stayName
            origPeopleInRoom = stay.peopleInRoom
            origKidsInRoom = stay.kidsInRoom
            origCheckInDate = stay.checkInDate
            origCheckOutDate = stay.checkOutDate
            origTariff = stay.tariff
            origTariffType = stay.tariffType
            origNotes = stay.notes
            origCurrency = stay.currency
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

    // resName Change
    fun onNameChange(newName: String) {
        _reservationName.value = newName
        _selectedReservation.value = null
    }

    fun onReservationSelected(reservation: Reservation) {
        _selectedReservation.value = reservation
        _reservationName.value = reservation.resName
        checkInDate = reservation.checkInDate
        nights = reservation.nights
        checkOutDate = reservation.checkInDate.plusDays(reservation.nights.toLong())
    }

    suspend fun getReservation(): Reservation? {
        val currentName = reservationName.value.trim()

        // If already selected
        selectedReservation.value?.let { return it }

        // Check exact match
        val existing = repository.getExactReservation(currentName)
        if (existing != null) return existing

        // Otherwise return null (no res selected)
        return null
    }

    fun onCurrencyChange(value: Currency) {
        currency = value
    }

    var availableRooms by mutableStateOf<List<RoomEntity>>(emptyList())
        private set

    fun loadAvailableRooms() {
        viewModelScope.launch {
            val allRooms = repository.getAllRooms()
            val blocked = repository.getBlockedRoomIds(
                checkIn = checkInDate,
                checkOut = checkOutDate,
                excludeStayId = stayId ?: -1L
            ).toSet()
            availableRooms = allRooms.filter { it.roomId !in blocked }
        }
    }

    fun changeRoom(room: RoomEntity) {
        viewModelScope.launch {
            roomNumber = room.roomNumber
            roomId = room.roomId
            tariff = repository.getRoomTariff(room.roomId)
            errorMessage = null
        }
    }

    fun reset() {
        _reservationName.value = ""
        peopleInRoom = 1
        kidsInRoom = 0
        nights = 1
        notes = ""
        tariff = tariff
        errorMessage = null
        tariffType = TariffType.NET
        currency = Currency.CRC
    }

    fun deleteStay(onDone: () -> Unit = {}){
        viewModelScope.launch {
            try {
                repository.deleteStay(stayId!!)
                onDone()
            } catch (e: Exception) {
                errorMessage = e.message
            }
        }
    }

    fun confirmStay(onDone: () -> Unit = {}){
        viewModelScope.launch {
            try{
                repository.confirmStay(stayId!!)
                onDone()
            } catch (e: Exception){
                errorMessage = e.message
            }
        }
    }

    fun save(onSuccess: () -> Unit) {
        viewModelScope.launch {
            val reservation = getReservation() // null if no reservation is selected (new one is created)
                when(stayMode) {
                    StayMode.CREATE -> {
                        when(
                            if(reservation == null) {
                                repository.createReservationWithStay(
                                    reservationName.value,
                                    peopleInRoom,
                                    kidsInRoom,
                                    notes,
                                    checkInDate,
                                    nights,
                                    roomNumber,
                                    tariff,
                                    tariffType,
                                    stayName,
                                    currency
                                )
                            } else {
                                repository.assignRoom( // assigns room to existing reservation
                                    reservation.id,
                                    roomNumber,
                                    peopleInRoom,
                                    kidsInRoom,
                                    checkInDate,
                                    nights,
                                    tariff,
                                    tariffType,
                                    notes,
                                    stayName,
                                    currency
                                )
                            }
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
                            status = stayStatus,
                            currency = currency
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