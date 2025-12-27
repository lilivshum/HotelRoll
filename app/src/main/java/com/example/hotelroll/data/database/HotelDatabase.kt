package com.example.hotelroll.data.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.hotelroll.data.dao.ReservationDao
import com.example.hotelroll.data.model.Reservation
import com.example.hotelroll.data.model.Stay
import com.example.hotelroll.data.dao.StayDao
import com.example.hotelroll.data.model.RoomEntity
import com.example.hotelroll.data.dao.RoomDao

import android.content.Context
import androidx.room.Room
import androidx.sqlite.db.SupportSQLiteDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import com.example.hotelroll.data.seed.DEFAULT_ROOMS


@Database(
    entities = [Reservation::class, Stay::class, RoomEntity::class],
    version = 2,
    exportSchema = false
)

@TypeConverters(Converters::class)


abstract class HotelDatabase : RoomDatabase() {
    abstract fun reservationDao(): ReservationDao
    abstract fun stayDao(): StayDao
    abstract fun roomDao(): RoomDao

    companion object {

        @Volatile
        private var INSTANCE: HotelDatabase? = null

        // for multi=thread implementation -- creates database once
        // only one thread can initialize the DB -- prevents race conditions

        fun getInstance(context: Context): HotelDatabase {
            return INSTANCE ?: synchronized(this) {
                INSTANCE ?: Room.databaseBuilder(
                    context.applicationContext,
                    HotelDatabase::class.java,
                    "hotel.db"
                )
                    .fallbackToDestructiveMigration()
                    .addCallback(DatabaseCallback(context.applicationContext))
                    .build()
                    .also { INSTANCE = it }
            }
        }

    }

}

