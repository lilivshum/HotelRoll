package com.example.hotelroll.repository


import android.util.Log.e
import androidx.room.withTransaction
import com.example.hotelroll.data.dao.ReservationDao
import com.example.hotelroll.data.dao.StayDao
import com.example.hotelroll.data.database.HotelDatabase
import com.example.hotelroll.domain.HotelManager
import com.example.hotelroll.data.model.Reservation
import com.example.hotelroll.data.model.Stay
import com.example.hotelroll.data.dao.RoomDao
import com.example.hotelroll.data.model.RoomStatus
import com.example.hotelroll.data.model.StayStatus
import java.time.LocalDate
import com.example.hotelroll.data.dto.RollItem
import com.example.hotelroll.data.model.TariffType
import kotlinx.coroutines.flow.Flow
import com.example.hotelroll.ui.utilities.StayUi

class HotelRepository(
    private val db: HotelDatabase,
    private val reservationDao: ReservationDao,
    private val stayDao: StayDao,
    private val roomDao: RoomDao,
    private val manager: HotelManager
) {
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
        stayName: String?
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
            stayName = stayName
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
        stayName: String?
    ): AssignRoomResult{
        return try {
        db.withTransaction {
            val resId = createReservation(
                resName,
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
            )) {
                is AssignRoomResult.Success -> res
                else -> throw AssignRoomFailure(res)
            }

        }
        } catch (e: AssignRoomFailure){
            e.result
        }

    }

    suspend fun deleteReservation(reservation: Reservation) {
        reservationDao.delete(reservation)
    }

    suspend fun deleteStay(stay: Stay) {
        stayDao.delete(stay)
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
    suspend fun getRoomRoll(date: LocalDate): Flow<List<RollItem>>{
        return roomDao.getRoll(date = date)
    }

    suspend fun getStayById(id: Long): Stay? {
        return stayDao.getById(id)
    }

    fun getAllReservationsSnapshot(): Flow<List<Reservation>> {
        return reservationDao.getAllReservationsSnapshot()
    }

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

        return if (overlapping) {
            StayResult.Overlapping
        } else {
            stayDao.update(stay)
            StayResult.Success
        }
    }



}

