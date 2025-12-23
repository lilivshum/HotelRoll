package com.example.hotelroll.data.model
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.time.LocalDate

@Entity(tableName = "reservations")
data class Reservation(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0L,
    val resName: String,
    val checkInDate: LocalDate,
    val nights: Int,
    val noGuests: Int,
    val notes: String? = null
)

