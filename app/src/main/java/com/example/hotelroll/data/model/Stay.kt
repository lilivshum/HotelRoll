package com.example.hotelroll.data.model
import java.time.LocalDate
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "stays",
    foreignKeys = [
        ForeignKey(
            entity = Reservation::class,
            parentColumns = ["id"],
            childColumns = ["reservationId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index("reservationId"), Index("roomId")]
)

data class Stay(

    @PrimaryKey(autoGenerate = true)
    val stayId: Long = 0L,
    val reservationId: Long,
    val roomId: Long,
    val peopleInRoom: Int,
    val checkInDate: LocalDate,
    val checkOutDate: LocalDate,
    val status: StayStatus = StayStatus.PENDING

)
