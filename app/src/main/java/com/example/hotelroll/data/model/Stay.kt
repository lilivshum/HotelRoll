package com.example.hotelroll.data.model
import java.time.LocalDate
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

enum class TariffType {
    NET,
    WITH_TAX
}

enum class Currency {
    USD,
    CRC
}


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
    val stayName: String? = null,
    val reservationId: Long,
    val roomId: Long,
    val peopleInRoom: Int,
    val kidsInRoom: Int,
    val checkInDate: LocalDate,
    val checkOutDate: LocalDate,
    val status: StayStatus = StayStatus.PENDING,// pending whether guests have confirmed / arrived
    val tariff: Double,
    val tariffType: TariffType = TariffType.NET,
    val notes: String? = null,
    val currency: Currency = Currency.CRC

)


