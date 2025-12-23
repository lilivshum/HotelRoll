package com.example.hotelroll.data.model
import java.time.LocalDate

data class Reservation(
    val id: Long = 0L,
    val resName: String,
    val checkInDate: LocalDate,
    val nights: Int,
    val noGuests: Int,
    val notes: String? = null
)

