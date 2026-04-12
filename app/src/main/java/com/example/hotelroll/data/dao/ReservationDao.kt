package com.example.hotelroll.data.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.hotelroll.data.model.Reservation
import kotlinx.coroutines.flow.Flow
import java.time.LocalDate

@Dao
interface ReservationDao {

    @Insert(onConflict = OnConflictStrategy.Companion.ABORT)
    suspend fun insert(reservation: Reservation): Long

    @Update
    suspend fun update(reservation: Reservation)

    @Delete
    suspend fun delete(reservation: Reservation)

    @Query("""
        SELECT * FROM reservations
        WHERE id = :id
    """)
    suspend fun getById(id: Long): Reservation?

    @Query("SELECT * FROM reservations ORDER BY checkInDate ASC")
    fun getAllReservationsSnapshot(): Flow<List<Reservation>>

    @Query("""UPDATE reservations
                SET notes = :notes
                WHERE id = :resId""")
    suspend fun updateReservationNotes(
        resId: Long,
        notes: String
    )

    @Query("SELECT COUNT(*) FROM reservations")
    suspend fun countReservations(): Int

    @Query("""
        SELECT * from reservations
        WHERE   LOWER(resName) LIKE '%' || LOWER(:query) || '%'
        ORDER BY resName ASC
    """
    )
    fun searchReservations(query: String): Flow<List<Reservation>>

    @Query("""
    SELECT * FROM reservations
    WHERE LOWER(resName) = LOWER(:name)
    LIMIT 1
""")
    suspend fun getExactReservation(name: String): Reservation?

}