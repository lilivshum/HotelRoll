package com.example.hotelroll.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.example.hotelroll.data.model.HistoryEntry
import kotlinx.coroutines.flow.Flow

@Dao
interface HistoryEntryDao {
    @Insert
    suspend fun insert(entry: HistoryEntry): Long

    @Query("SELECT * FROM history_entries WHERE reservationId = :resId ORDER BY timestamp DESC")
    fun getByReservationId(resId: Long): Flow<List<HistoryEntry>>

    @Query("SELECT COUNT(*) FROM history_entries WHERE reservationId = :resId")
    suspend fun countByReservationId(resId: Long): Int
}
