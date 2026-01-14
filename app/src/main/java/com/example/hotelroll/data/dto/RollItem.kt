package com.example.hotelroll.data.dto

// Hotel Roll Item, depends on date, used for display
class RollItem (
    val roomId: Long,
    val roomNumber: String,
    val stayId: Long?, // only used for convenience to access reservation information
    val reservationName: String?,
    val peopleInRoom: Int?,
    val kidsInRoom: Int?,
    val tariff: Double?
)