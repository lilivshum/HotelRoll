package com.example.hotelroll.data.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

enum class HistoryEventType {
    RESERVATION_CREATED,
    STAY_CREATED,
    STAY_CONFIRMED,
    STAY_EDITED,
    ROOM_MOVED,
    STAY_DELETED,
    RESERVATION_CLOSED
}

@Entity(
    tableName = "history_entries",
    foreignKeys = [ForeignKey(
        entity = Reservation::class,
        parentColumns = ["id"],
        childColumns = ["reservationId"],
        onDelete = ForeignKey.CASCADE
    )]
)
data class HistoryEntry(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    @ColumnInfo(index = true) val reservationId: Long,
    val eventType: HistoryEventType,
    val userId: Long,
    val userName: String,
    val timestamp: Long,
    val note: String? = null
)
