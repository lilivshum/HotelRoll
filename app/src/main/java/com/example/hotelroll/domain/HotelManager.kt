package com.example.hotelroll.domain

import com.example.hotelroll.data.model.*
import java.time.LocalDate

class HotelManager {
    fun createReservation(
        resName: String,
        noGuests: Int,
        noKids: Int,
        notes: String?,
        checkInDate: LocalDate,
        nights: Int

    ): Reservation {
        require(nights > 0) {
            "Error: 0 days of stay"
        }

        require(noGuests > 0 ){
            "Error: 0 guests assigned"
        }

        require(noKids >= 0) {
            "Error: invalid number of kids"
        }

        return Reservation(
            resName = resName,
            noGuests = noGuests,
            noKids = noKids,
            checkInDate = checkInDate,
            nights = nights,
            notes = notes
        )
    }

    fun createStay(
        resId: Long,
        peopleInRoom: Int,
        kidsInRoom: Int,
        roomId: Long,
        checkInDate: LocalDate,
        checkOutDate: LocalDate,
        tariff: Double
    ): Stay {
        require(checkInDate < checkOutDate) {
            "Error: checkout must be after check-in"
        }

        require(peopleInRoom > 0){
            "Error: invalid amount of guests"
        }

        require(kidsInRoom >= 0){
            "Error: invalid amount of kids "
        }

        return Stay(

            reservationId = resId,
            roomId = roomId,
            peopleInRoom = peopleInRoom,
            kidsInRoom = kidsInRoom,
            checkInDate = checkInDate,
            checkOutDate = checkOutDate,
            tariff = tariff

        )
    }

}