package com.example.hotelroll.data.database

import android.content.Context
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import com.example.hotelroll.data.seed.DEFAULT_ROOMS
import android.util.Log

class DatabaseCallback(
    private val context: Context
) : RoomDatabase.Callback() {

    override fun onCreate(db: SupportSQLiteDatabase) {
        super.onCreate(db)
        Log.d("DB_INIT", "Database onCreate() called")

        CoroutineScope(Dispatchers.IO).launch {
            val db = HotelDatabase.getInstance(context)
            db.roomDao().insertAll(DEFAULT_ROOMS)
        }
    }
}
