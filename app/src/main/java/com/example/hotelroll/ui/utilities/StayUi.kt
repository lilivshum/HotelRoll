package com.example.hotelroll.ui.utilities

import java.time.LocalDate

data class StayUi(
    val roomNumber: String,
    val people: Int,
    val checkIn: LocalDate,
    val checkOut: LocalDate,
    val stayId: Long
)