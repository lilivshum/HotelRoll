package com.example.hotelroll.data.model

data class Reservation(
    val id: Long = 0L,
    val resName: String,
    val roomNumber: String,
    val checkInDate: Long,
    val checkOutDate: Long,
    val noGuests: Int,
    val notes: String? = null
)

