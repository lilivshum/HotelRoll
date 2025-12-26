package com.example.hotelroll.data.dao

import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Update
import androidx.room.Query
import androidx.room.Dao
import androidx.room.OnConflictStrategy

import com.example.hotelroll.data.model.Room

@Dao
interface RoomDao {
    @Insert
    suspend fun insert(room: Room)

    @Update
    suspend fun update(room: Room)

    @Delete
    suspend fun delete(room: Room)

    @Query("SELECT * FROM rooms")
    suspend fun getAll(): List<Room>

    @Query("SELECT * FROM rooms WHERE roomId = :id")
    suspend fun getById(id: Long): Room?

    @Query("SELECT EXISTS(SELECT 1 FROM rooms WHERE roomId = :roomId)")
    suspend fun exists(roomId: Long): Boolean

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertAll(rooms: List<Room>)

}