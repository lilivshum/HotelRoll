package com.example.hotelroll.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "rooms")

data class Room(
    @PrimaryKey(autoGenerate = true)
    val roomId: Long = 0L,
    val roomNumber: String,
    val capacity: Int
)
