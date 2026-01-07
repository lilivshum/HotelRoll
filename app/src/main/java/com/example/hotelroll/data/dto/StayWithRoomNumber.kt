package com.example.hotelroll.data.dto

import java.time.LocalDate

// for query performance enhancement
data class StayWithRoomNumber(
    val stayId: Long,
    val roomNumber: String,
    val peopleInRoom: Int,
    val checkInDate: LocalDate,
    val checkOutDate: LocalDate
)