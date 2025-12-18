package com.example.hotelroll.data.model

enum class RoomStatus { AVAILABLE, OCCUPIED }

data class Room(
    val roomId: Long = 0L,
    val roomNumber: String,
    val capacity: Int
)
