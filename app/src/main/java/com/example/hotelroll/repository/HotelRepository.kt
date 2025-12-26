package com.example.hotelroll.repository


import androidx.room.withTransaction
import com.example.hotelroll.data.dao.ReservationDao
import com.example.hotelroll.data.dao.StayDao
import com.example.hotelroll.data.database.HotelDatabase
import com.example.hotelroll.domain.HotelManager
import com.example.hotelroll.data.model.Reservation
import com.example.hotelroll.data.model.Stay
import com.example.hotelroll.data.dao.RoomDao
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
}

