package com.example.hotelroll.data.dao
import androidx.room.*
import com.example.hotelroll.data.model.Reservation
import java.time.LocalDate
import com.example.hotelroll.data.model.Stay
import com.example.hotelroll.data.model.StayStatus


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

    @Query("""
        SELECT * FROM stays
        WHERE stayId = :stayId
    """)
    suspend fun getById(
        stayId: Long
    ): Stay?

    // function that updates status of a certain stay
    @Query("""
        UPDATE stays 
        SET status = :status 
        WHERE stayId = :stayId
    """)
    suspend fun updateStatus(
        stayId: Long,
        status: StayStatus
    )

    @Delete
    suspend fun delete(stay: Stay)

    @Update
    suspend fun update(stay: Stay)

}
