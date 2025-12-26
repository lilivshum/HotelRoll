package com.example.hotelroll.data.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.hotelroll.data.model.Reservation
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

}