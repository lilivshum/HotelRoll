package com.example.hotelroll.data.dao
import androidx.room.*
import java.time.LocalDate
import com.example.hotelroll.data.model.Stay

@Dao
interface StayDao {

    @Insert
    fun insert(stay: Stay)

    // function to check if its overlapping
    @Query("""
        SELECT * FROM stays
        WHERE roomId = :roomId
        AND checkInDate < :endDate
        AND checkOutDate > :startDate
    """)
    fun getOverlapping(
        roomId: Long,
        startDate: LocalDate,
        endDate: LocalDate
    ): List<Stay>
}
