package com.example.hotelroll.data.dto

import com.example.hotelroll.data.model.Stay

data class GanttRoomRow(
    val roomId: Long,
    val roomNumber: String,
    val stays: List<Stay>,
    val stayDisplayNames: Map<Long, String>  // stayId → stayName ?: reservationName
)
