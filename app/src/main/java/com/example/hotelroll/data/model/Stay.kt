package com.example.hotelroll.data.model

data class Stay(
    val stayId: Long = 0L,
    val reservationId: Long,
    val roomId: Long,
    val peopleInRoom: Int,
    val checkInDate: Long,
    val checkOutDate: Long
)
