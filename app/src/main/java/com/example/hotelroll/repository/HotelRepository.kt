package com.example.hotelroll.repository


import android.content.Context
import android.util.Log.e
import androidx.room.withTransaction
import com.example.hotelroll.data.dao.ReservationDao
import com.example.hotelroll.data.dao.StayDao
import com.example.hotelroll.data.dao.UserDao
import com.example.hotelroll.data.database.HotelDatabase
import com.example.hotelroll.domain.HotelManager
import com.example.hotelroll.data.model.Reservation
import com.example.hotelroll.data.model.Stay
import com.example.hotelroll.data.model.User
import com.example.hotelroll.data.dao.RoomDao
import com.example.hotelroll.data.model.RoomStatus
import com.example.hotelroll.data.model.StayStatus
import java.time.LocalDate
import com.example.hotelroll.data.dto.RollItem
import com.example.hotelroll.data.model.Currency
import com.example.hotelroll.data.model.TariffType
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import com.example.hotelroll.ui.utilities.StayUi

private const val PREFS_NAME = "hotel_prefs"
private const val KEY_ACTIVE_USER_ID = "active_user_id"

class HotelRepository(
    private val db: HotelDatabase,
    private val reservationDao: ReservationDao,
    private val stayDao: StayDao,
    private val roomDao: RoomDao,
    private val userDao: UserDao,
    private val manager: HotelManager,
    context: Context
) {
    private val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
    private val _activeUserId = MutableStateFlow(prefs.getLong(KEY_ACTIVE_USER_ID, 1L))
    val activeUserId: StateFlow<Long> = _activeUserId.asStateFlow()

    val allUsers: Flow<List<User>> = userDao.getAll()

    val activeUser: Flow<User?> = combine(_activeUserId, allUsers) { id, users ->
        users.find { it.id == id }
    }

    fun setActiveUser(id: Long) {
        prefs.edit().putLong(KEY_ACTIVE_USER_ID, id).apply()
        _activeUserId.value = id
    }

    suspend fun getUserById(id: Long): User? = userDao.getById(id)
    /** Create reservation and persist */
    suspend fun createReservation(
        resName: String,
        noGuests: Int,
        noKids: Int,
        notes: String?,
        checkInDate: LocalDate,
        nights: Int
    ): Long {
        val reservation = manager.createReservation(
            resName,
            noGuests,
            noKids,
            notes,
            checkInDate,
            nights
        )
        return reservationDao.insert(reservation)
    }

    // assigned room result class
    sealed class AssignRoomResult {
        data class Success(val stayId: Long) : AssignRoomResult()
        object Overlapping: AssignRoomResult()
        object ReservationNotFound: AssignRoomResult() // should never happen for now
        object InvalidNights: AssignRoomResult()
        object RoomNotFound: AssignRoomResult() // should never happen for now
    }

    class AssignRoomFailure(val result: AssignRoomResult) : Exception()


    /** Assign a room if available */
    suspend fun assignRoom(
        reservationId: Long,
        roomNumber: String,
        peopleInRoom: Int,
        kidsInRoom: Int,
        checkInDate: LocalDate,
        nights: Int,
        tariff: Double?,
        tariffType: TariffType,
        notes: String?,
        stayName: String?,
        currency: Currency
    ): AssignRoomResult {

        // checks if nights amount is valid (checkOutDate Validation)
        val reservation = reservationDao.getById(reservationId)
            ?: return AssignRoomResult.ReservationNotFound

        if(nights < 1){
            return AssignRoomResult.InvalidNights
        }

        val checkOutDate = checkInDate.plusDays(nights.toLong())

        // checks if room exists & capacity is allowed
        val room = roomDao.getByRoomNumber(roomNumber)
            ?: return AssignRoomResult.RoomNotFound
        // although this I can change, sometimes room capacity changes are not met
        // lets not add this for now

//        if(peopleInRoom > room.capacity){
//            throw IllegalStateException("Room capacity exceeded")
//        }

        //checks if room is available
        val overlaps = stayDao.getOverlapping(
            room.roomId,
            checkInDate,
            checkOutDate
        )
        if (overlaps.isNotEmpty()) {
           return AssignRoomResult.Overlapping
        }

        // ensures default tariff is used
        val finalTariff = tariff ?: 60.0// add different way for adding tariffs

        val stay = manager.createStay(
            reservationId,
            peopleInRoom = peopleInRoom,
            kidsInRoom = kidsInRoom,
            roomId = room.roomId,
            checkInDate = checkInDate,
            checkOutDate = checkOutDate,
            tariff = finalTariff,
            tariffType = tariffType,
            notes = notes,
            stayName = stayName,
            currency = currency
        )

        stayDao.insert(stay)

        return AssignRoomResult.Success(stay.stayId)
    }

    // creates an a stay immediately with the reservation information
    // cases when only one room is used
    // creates stay name the same as reservation name
    /** Atomic create + assign */
    suspend fun createReservationWithStay(
        resName: String,
        noGuests: Int,
        noKids: Int,
        notes: String?,
        checkInDate: LocalDate,
        nights: Int,
        roomNumber: String,
        tariff: Double?,
        tariffType: TariffType,
        stayName: String?,
        currency: Currency
    ): AssignRoomResult{
        return try {
        db.withTransaction {
            var realResName = "Reservation" // ensures blank res is still visible
            if(resName != ""){
                realResName = resName
            }
            val resId = createReservation(
                realResName,
                noGuests,
                noKids,
                notes,
                checkInDate,
                nights
            )
            when (val res = assignRoom(
                resId,
                roomNumber,
                noGuests,
                noKids,
                checkInDate,
                nights,
                tariff,
                tariffType,
                notes,
                stayName = stayName,
                currency
            )) {
                is AssignRoomResult.Success -> res
                else -> throw AssignRoomFailure(res)
            }

        }
        } catch (e: AssignRoomFailure){
            e.result
        }

    }

    suspend fun updateReservation(reservation: Reservation) {
        reservationDao.update(reservation)
    }

    suspend fun deactivateReservation(resId: Long) {
        reservationDao.deactivate(resId)
    }

    suspend fun deleteReservation(resId: Long) {
        val res = reservationDao.getById(resId)
        res?.let { reservationDao.delete(res) }
    }

    suspend fun deleteStay(stayId: Long) {
        val stay = stayDao.getById(stayId)
        stay?.let { stayDao.delete(stay) }
    }

    suspend fun getStaysPerRoom(
        roomId: Long,
        from: LocalDate,
        to: LocalDate
    ): List<Stay> {
        return stayDao.getOverlapping(roomId, from, to)
    }

    // some functionality functions

    // requires stay Id to exist and be a legal stay Id
    suspend fun confirmStay(
        stayId: Long
    ) {
        val stay = stayDao.getById(stayId) // check added just in case for good practice
            ?: throw IllegalStateException("Stay not found")

        stayDao.updateStatus(stay.stayId, StayStatus.CONFIRMED)

    }

    // seems useful for like a clicking mechanism where it changes the room availability
    suspend fun switchRoomAvailability(
        roomId: Long
    ){
        val room = roomDao.getById(roomId)
            ?: throw IllegalStateException("Room not found")

        if(room.status == RoomStatus.AVAILABLE){
            roomDao.updateStatus(room.roomId, RoomStatus.UNAVAILABLE)
        } else {
            roomDao.updateStatus(room.roomId, RoomStatus.AVAILABLE)
        }

    }

    // for detail viewing in ui
    fun getRoomRoll(date: LocalDate): Flow<List<RollItem>>{
        return roomDao.getRoll(date = date)
    }

    suspend fun getStayById(id: Long): Stay? {
        return stayDao.getById(id)
    }

    fun getAllReservationsSnapshot(): Flow<List<Reservation>> {
        return reservationDao.getAllReservationsSnapshot()
    }

    fun getActiveReservations(today: LocalDate): Flow<List<Reservation>> =
        reservationDao.getActiveReservations(today)

    fun getPastReservations(today: LocalDate): Flow<List<Reservation>> =
        reservationDao.getPastReservations(today)

    suspend fun getStayByResId(resId: Long): List<Stay> {
        return stayDao.getByResId(resId)
    }

    suspend fun getResById(resId: Long): Reservation? {
        return reservationDao.getById(resId)
    }

    suspend fun getRoomNumberById(roomId: Long): String {
        val room = roomDao.getById(roomId)
            ?: return "" // no need to throw more exceptions really but just to handle the reservation? type

        return room.roomNumber
    }

    // for reservation & stay notes update
    suspend fun updateStayNotes(
        stayId: Long,
        notes: String
    ) {
        stayDao.updateStayNotes(stayId, notes)
    }

    suspend fun updateReservationNotes(
        resId: Long,
        notes: String
    ) {
        reservationDao.updateReservationNotes(resId, notes)
    }

    suspend fun getStaysUi(resId: Long): List<StayUi> {
        return stayDao.getStaysWithRoomNumber(resId).map {
            StayUi(
                stayId = it.stayId,
                roomNumber = it.roomNumber,
                adults = it.peopleInRoom,
                checkIn = it.checkInDate,
                checkOut = it.checkOutDate,
                kids = it.kidsInRoom
            )
        }
    }

    suspend fun getRoomTariff(id: Long): Double {
        return roomDao.getRoomTariff(id)
    }

    suspend fun getRoomByNumber(roomNumber: String) = roomDao.getByRoomNumber(roomNumber)

    suspend fun getAllRooms() = roomDao.getAll()

    // class for update stay result
    sealed class StayResult{
        object Success: StayResult()
        object Overlapping: StayResult()
    }


    suspend fun updateStay(stay: Stay): StayResult{
        val overlapping = stayDao.hasOverlap(
            roomId = stay.roomId,
            checkIn = stay.checkInDate,
            checkOut = stay.checkOutDate,
            excludeStayId = stay.stayId // important when editing
        )

        // nights cannot be 0
        // reservation cannot be changed -- only guest names
        return if (overlapping) {
            StayResult.Overlapping
        } else {
            stayDao.update(stay)
            StayResult.Success
        }
    }

    suspend fun searchReservations(query: String): Flow<List<Reservation>>{
        return reservationDao.searchReservations(query)
    }

    suspend fun getExactReservation(name:String): Reservation?{
        return reservationDao.getExactReservation(name)
    }

    suspend fun hasConfirmedStay(resId: Long): Boolean{
        return stayDao.hasConfirmedStay(resId)
    }

    suspend fun getBlockedRoomIds(
        checkIn: LocalDate,
        checkOut: LocalDate,
        excludeStayId: Long
    ): List<Long> = stayDao.getBlockedRoomIds(checkIn, checkOut, excludeStayId)

    // function that moves stay to another room
    suspend fun tryMoveStay(
        stayId: Long,
        checkInDate: LocalDate,
        checkOutDate: LocalDate,
        newRoomId: Long
    ): Boolean {
        val hasOverlap = stayDao.hasOverlap(
            newRoomId,
            checkInDate,
            checkOutDate,
            stayId
        )
        if (hasOverlap) return false
        stayDao.updateRoom(stayId, newRoomId)
        return true
    }

}

