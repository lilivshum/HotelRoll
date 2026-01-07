package com.example.hotelroll.data.database

import android.content.Context
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import com.example.hotelroll.data.seed.DEFAULT_ROOMS
import android.util.Log
import com.example.hotelroll.data.model.Reservation
import com.example.hotelroll.data.model.Stay
import java.time.LocalDate
import com.example.hotelroll.data.database.HotelDatabase
import com.example.hotelroll.domain.HotelManager
import com.example.hotelroll.repository.HotelRepository

class DatabaseCallback(
) : RoomDatabase.Callback() {

    override fun onCreate(db: SupportSQLiteDatabase) {
        super.onCreate(db)
        Log.d("DB_INIT", "Database onCreate() called")

    }

    override fun onOpen(db: SupportSQLiteDatabase) {
        super.onOpen(db)
    }
}
