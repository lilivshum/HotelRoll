package com.example.hotelroll


import android.app.Application
import com.example.hotelroll.data.database.HotelDatabase
import com.example.hotelroll.domain.HotelManager
import com.example.hotelroll.repository.HotelRepository

class HotelApplication : Application() {

    // Database is created ONCE for the whole app
    val database: HotelDatabase by lazy {
        HotelDatabase.getInstance(this)
    }

    val manager = HotelManager()

    // Repository is also SINGLETON per app
    val repository: HotelRepository by lazy {
        HotelRepository(
            db = database,
            reservationDao = database.reservationDao(),
            stayDao = database.stayDao(),
            roomDao = database.roomDao(),
            manager
        )
    }
}
