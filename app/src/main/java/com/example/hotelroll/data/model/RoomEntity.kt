package com.example.hotelroll.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.Index


@Entity(
    tableName = "rooms",
    indices = [Index(value = ["roomNumber"], unique = true)])

data class RoomEntity(
    @PrimaryKey(autoGenerate = true)
    val roomId: Long = 0L,
    val roomNumber: String,
    val capacity: Int,
    val status: RoomStatus = RoomStatus.AVAILABLE,
    val tariff: Double = 60.0 // Default tariff for a room
)
