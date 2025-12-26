package com.example.hotelroll.data.dto

// Hotel Roll Item, depends on date, used for display
class RollItem (
    val roomId: Long,
    val roomNumber: String,
    val reservationName: String?,
    val peopleInRoom: Int?
)