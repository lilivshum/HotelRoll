package com.example.hotelroll.data.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverter
import androidx.room.TypeConverters
import com.example.hotelroll.data.dao.ReservationDao
import com.example.hotelroll.data.model.Reservation
import com.example.hotelroll.data.model.Stay
import com.example.hotelroll.data.dao.StayDao
import com.example.hotelroll.data.model.Room
import com.example.hotelroll.data.dao.RoomDao

@Database(
    entities = [Reservation::class, Stay::class, Room::class],
    version = 1
)

@TypeConverters(Converters::class)

abstract class HotelDatabase : RoomDatabase(){
    abstract fun reservationDao(): ReservationDao
    abstract fun stayDao(): StayDao
    abstract fun RoomDao(): RoomDao
}

