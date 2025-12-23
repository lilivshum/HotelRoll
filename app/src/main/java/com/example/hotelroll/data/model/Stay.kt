package com.example.hotelroll.data.model
import java.time.LocalDate
data class Stay(
    val stayId: Long = 0L,
    val reservationId: Long,
    val roomId: Long,
    val peopleInRoom: Int,
    val checkInDate: LocalDate,
    val checkOutDate: LocalDate
)
