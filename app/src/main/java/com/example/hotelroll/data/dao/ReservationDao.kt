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

    @Query("UPDATE reservations SET isActive = 0 WHERE id = :resId")
    suspend fun deactivate(resId: Long)

    @Query("SELECT * FROM reservations WHERE id = :id")
    suspend fun getById(id: Long): Reservation?

    // Active: isActive=1 AND (no stays yet OR has at least one stay with checkOutDate >= today)
    @Query("""
        SELECT * FROM reservations
        WHERE isActive = 1
        AND (
            NOT EXISTS (SELECT 1 FROM stays WHERE stays.reservationId = reservations.id)
            OR EXISTS (SELECT 1 FROM stays WHERE stays.reservationId = reservations.id AND stays.checkOutDate >= :today)
        )
        ORDER BY checkInDate ASC
    """)
    fun getActiveReservations(today: LocalDate): Flow<List<Reservation>>

    // Past: isActive=0 OR (isActive=1 AND has stays AND all stays checked out)
    @Query("""
        SELECT * FROM reservations
        WHERE isActive = 0
        OR (
            isActive = 1
            AND EXISTS (SELECT 1 FROM stays WHERE stays.reservationId = reservations.id)
            AND NOT EXISTS (SELECT 1 FROM stays WHERE stays.reservationId = reservations.id AND stays.checkOutDate >= :today)
        )
        ORDER BY checkInDate DESC
    """)
    fun getPastReservations(today: LocalDate): Flow<List<Reservation>>

    @Query("SELECT * FROM reservations ORDER BY checkInDate ASC")
    fun getAllReservationsSnapshot(): Flow<List<Reservation>>

    @Query("""UPDATE reservations SET notes = :notes WHERE id = :resId""")
    suspend fun updateReservationNotes(resId: Long, notes: String)

    @Query("SELECT COUNT(*) FROM reservations")
    suspend fun countReservations(): Int

    @Query("""
        SELECT * FROM reservations
        WHERE isActive = 1
        AND LOWER(resName) LIKE '%' || LOWER(:query) || '%'
        ORDER BY resName ASC
    """)
    fun searchReservations(query: String): Flow<List<Reservation>>

    @Query("""
        SELECT * FROM reservations
        WHERE isActive = 1
        AND LOWER(resName) = LOWER(:name)
        LIMIT 1
    """)
    suspend fun getExactReservation(name: String): Reservation?

}
