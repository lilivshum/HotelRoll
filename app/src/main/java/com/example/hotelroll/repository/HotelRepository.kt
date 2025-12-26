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
        notes: String?,
        checkInDate: LocalDate,
        nights: Int
    ): Long {
        val reservation = manager.createReservation(
            resName,
            noGuests,
            notes,
            checkInDate,
            nights
        )
        return reservationDao.insert(reservation)
    }

    /** Assign a room if available */
    suspend fun assignRoom(
        reservationId: Long,
        roomId: Long,
        peopleInRoom: Int,
        checkInDate: LocalDate,
        nights: Int
    ): Long {

        // checks if nights amount is valid (checkOutDate Validation)
        val reservation = reservationDao.getById(reservationId)
            ?: throw IllegalArgumentException("Reservation not found")

        if(nights > reservation.nights || nights < 1){
            throw IllegalStateException("Invalid value for room nights stay")
        }

        val checkOutDate = checkInDate.plusDays(nights.toLong())

        // checks if room exists & capacity is allowed
        val room = roomDao.getById(roomId)
            ?: throw IllegalStateException("Room not found")

        // although this I can change, sometimes room capacity changes are not met
        if(peopleInRoom > room.capacity){
            throw IllegalStateException("Room capacity exceeded")
        }

        //checks if room is available
        val overlaps = stayDao.getOverlapping(
            roomId,
            checkInDate,
            checkOutDate
        )
        if (overlaps.isNotEmpty()) {
            throw IllegalStateException("Room not available")
        }

        val stay = manager.createStay(
            reservationId,
            peopleInRoom = peopleInRoom,
            roomId = roomId,
            checkInDate = checkInDate,
            checkOutDate = checkOutDate
        )

        return stayDao.insert(stay)
    }

    /** Atomic create + assign */
    suspend fun createReservationWithStay(
        resName: String,
        noGuests: Int,
        notes: String?,
        checkInDate: LocalDate,
        nights: Int,
        roomId: Long,
        peopleInRoom: Int
    ) {
        db.withTransaction {
            val resId = createReservation(
                resName,
                noGuests,
                notes,
                checkInDate,
                nights
            )
            assignRoom(
                resId,
                roomId,
                peopleInRoom,
                checkInDate,
                nights
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

}

