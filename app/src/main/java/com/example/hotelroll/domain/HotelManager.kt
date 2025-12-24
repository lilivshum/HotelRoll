package com.example.hotelroll.domain

import com.example.hotelroll.data.model.*
import java.time.LocalDate

class HotelManager {
    fun createReservation(
        resName: String,
        noGuests: Int,
        notes: String?,
        checkInDate: LocalDate,
        nights: Int

    ): Reservation {
        require(nights > 0) {
            "Error: 0 days of stay"
        }

        return Reservation(
            resName = resName,
            noGuests = noGuests,
            checkInDate = checkInDate,
            nights = nights,
            notes = notes
        )
    }

    fun createStay(
        resId: Long,
        peopleInRoom: Int,
        roomId: Long,
        checkInDate: LocalDate,
        checkOutDate: LocalDate
    ): Stay {
        require(checkInDate < checkOutDate) {
            "Error: checkout must be after check-in"
        }
        return Stay(

            reservationId = resId,
            roomId = roomId,
            peopleInRoom = peopleInRoom,
            checkInDate = checkInDate,
            checkOutDate = checkOutDate

        )
    }

    /* fun isRoomAvailable(
        roomId: Long,
        checkInDate: LocalDate,
        checkOutDate: LocalDate,
        existingStays: List<Stay>
    ): Boolean {
        return existingStays.none { stay ->
            stay.roomId == roomId &&
                    checkInDate < stay.checkOutDate &&
                    checkOutDate > stay.checkInDate
        }
    }*/

    // assume there is no collision
    fun assignRoom(
        reservationId: Long,
        roomId: Long,
        peopleInRoom: Int,
        checkInDate: LocalDate,
        nights: Int,
        existingStays: List<Stay>
    ): Stay {
        // calculate checkout date
        val checkOutDate = checkInDate.plusDays(nights.toLong())

        return createStay(
            resId = reservationId,
            peopleInRoom,
            roomId,
            checkInDate,
            checkOutDate
        )
    }
    
}