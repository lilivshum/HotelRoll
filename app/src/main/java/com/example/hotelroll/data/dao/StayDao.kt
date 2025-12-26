package com.example.hotelroll.data.dao
import androidx.room.*
import com.example.hotelroll.data.model.Reservation
import java.time.LocalDate
import com.example.hotelroll.data.model.Stay

@Dao
interface StayDao {

    @Insert(onConflict = OnConflictStrategy.Companion.ABORT)
    suspend fun insert(stay: Stay): Long

    // function to check if its overlapping
    @Query("""
        SELECT * FROM stays
        WHERE roomId = :roomId
        AND checkInDate < :endDate
        AND checkOutDate > :startDate
    """)
    suspend fun getOverlapping(
        roomId: Long,
        startDate: LocalDate,
        endDate: LocalDate
    ): List<Stay>

    @Delete
    suspend fun delete(stay: Stay)

    @Update
    suspend fun update(stay: Stay)

}
