package com.example.hotelroll.repository


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

    /** Assign a room if available */
    suspend fun assignRoom(
        reservationId: Long,
        roomNumber: String,
        peopleInRoom: Int,
        kidsInRoom: Int,
        checkInDate: LocalDate,
        nights: Int,
        tariff: Double?
    ): Long {

        // checks if nights amount is valid (checkOutDate Validation)
        val reservation = reservationDao.getById(reservationId)
            ?: throw IllegalArgumentException("Reservation not found")

        if(nights > reservation.nights || nights < 1){
            throw IllegalStateException("Invalid value for room nights stay")
        }

        val checkOutDate = checkInDate.plusDays(nights.toLong())

        // checks if room exists & capacity is allowed
        val room = roomDao.getByRoomNumber(roomNumber)
            ?: throw IllegalStateException("Room $roomNumber not found")

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
            throw IllegalStateException("Room not available")
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
            tariff = finalTariff

        )

        val res = reservationDao.getById(reservationId)


        return stayDao.insert(stay)
    }

    /** Atomic create + assign */
    suspend fun createReservationWithStay(
        resName: String,
        noGuests: Int,
        noKids: Int,
        notes: String?,
        checkInDate: LocalDate,
        nights: Int,
        roomNumber: String,
        peopleInRoom: Int,
        kidsInRoom: Int,
        tariff: Double?
    ) {
        db.withTransaction {
            val resId = createReservation(
                resName,
                noGuests,
                noKids,
                notes,
                checkInDate,
                nights
            )
            assignRoom(
                resId,
                roomNumber,
                peopleInRoom,
                kidsInRoom,
                checkInDate,
                nights,
                tariff
            )
        }
    }

    suspend fun deleteReservation(reservation: Reservation) {
        reservationDao.delete(reservation)
    }

    suspend fun deleteStay(stay: Stay) {
        stayDao.delete(stay)
    }

    suspend fun getStaysForRoom(
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
    suspend fun getRoomRoll(date: LocalDate): List<RollItem>{
        return roomDao.getRoll(date = date)
    }

    suspend fun getStayById(id: Long): Stay? {
        return stayDao.getById(id)
    }

    suspend fun getAllReservationsSnapshot(): List<Reservation> {
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


}

