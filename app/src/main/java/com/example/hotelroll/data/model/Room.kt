package com.example.hotelroll.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.Index

@Entity(
    tableName = "rooms",
    indices = [Index(value = ["number"], unique = true)])

data class Room(
    @PrimaryKey(autoGenerate = true)
    val roomId: Long = 0L,
    val roomNumber: String,
    val capacity: Int
)
