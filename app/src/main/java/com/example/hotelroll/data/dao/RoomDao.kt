package com.example.hotelroll.data.dao

import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Update
import androidx.room.Query
import androidx.room.Dao
import androidx.room.OnConflictStrategy

import com.example.hotelroll.data.model.RoomEntity
import com.example.hotelroll.data.model.RoomStatus
import java.time.LocalDate
import com.example.hotelroll.data.dto.RollItem

@Dao
interface RoomDao {
    @Insert
    suspend fun insert(roomEntity: RoomEntity)

    @Update
    suspend fun update(roomEntity: RoomEntity)

    @Delete
    suspend fun delete(roomEntity: RoomEntity)

    @Query("SELECT * FROM rooms")
    suspend fun getAll(): List<RoomEntity>

    @Query("SELECT * FROM rooms WHERE roomId = :id")
    suspend fun getById(id: Long): RoomEntity?

    @Query("SELECT EXISTS(SELECT 1 FROM rooms WHERE roomId = :roomId)")
    suspend fun exists(roomId: Long): Boolean

    @Query("""
        UPDATE rooms
        SET status = :status 
        WHERE roomId = :roomId
    """)
    suspend fun updateStatus(
        roomId: Long,
        status: RoomStatus
    )

    // used for inserting initial Rooms
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertAll(roomEntities: List<RoomEntity>)

    @Query("""
        SELECT r.roomId AS roomId,
               r.roomNumber AS roomNumber,
               res.resName AS reservationName,
               s.peopleInRoom AS peopleInRoom
        FROM rooms r
        LEFT JOIN stays s ON s.roomId = r.roomId
            AND :date >= s.checkInDate
            AND :date < s.checkOutDate
        LEFT JOIN reservations res ON res.id = s.reservationId
        ORDER BY r.roomNumber
    """)
    suspend fun getRoll(date: LocalDate): List<RollItem>


}