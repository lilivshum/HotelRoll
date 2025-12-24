package com.example.hotelroll.data.dao

import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Update
import androidx.room.Query
import androidx.room.Dao

import com.example.hotelroll.data.model.Room

@Dao
interface RoomDao {
    @Insert
    fun insert(room: Room)

    @Update
    fun update(room: Room)

    @Delete
    fun delete(room: Room)

    @Query("SELECT * FROM rooms")
    fun getAll(): List<Room>

    @Query("SELECT * FROM rooms WHERE roomId = :id")
    fun getById(id: Long): Room?

}