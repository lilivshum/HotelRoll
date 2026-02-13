package com.example.hotelroll.data.dto

import com.example.hotelroll.data.model.Currency
import com.example.hotelroll.data.model.StayStatus
import com.example.hotelroll.data.model.TariffType

// Hotel Roll Item, depends on date, used for display
class RollItem (
    val roomId: Long,
    val roomNumber: String,
    val stayId: Long?, // only used for convenience to access reservation information
    val reservationName: String?, // this can now also be the stay names (guest in stay)
    val peopleInRoom: Int?,
    val kidsInRoom: Int?,
    val tariff: Double,
    val tariffType: TariffType?,
    val stayStatus: StayStatus?,
    val currency: Currency?
)