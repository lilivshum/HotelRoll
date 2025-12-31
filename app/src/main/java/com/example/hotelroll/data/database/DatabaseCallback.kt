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
    private val context: Context
) : RoomDatabase.Callback() {

    override fun onCreate(db: SupportSQLiteDatabase) {
        super.onCreate(db)
        Log.d("DB_INIT", "Database onCreate() called")

        CoroutineScope(Dispatchers.IO).launch {
            val db = HotelDatabase.getInstance(context)
            db.roomDao().insertAll(DEFAULT_ROOMS)

            // fake data for testing
            val reservationDao = db.reservationDao()
            val stayDao = db.stayDao()
            val roomDao = db.roomDao()

            val manager = HotelManager()
            val repo = HotelRepository(
                db,
                reservationDao,
                stayDao,
                roomDao,
                manager
            )

                val resId1 = reservationDao.insert(
                Reservation(
                    resName = "Alice Smith",
                    noGuests = 2,
                    checkInDate = LocalDate.now(),
                    nights = 2,
                    notes = "Late arrival"
                )
            )

            repo.assignRoom( resId1,
                "101",
                2,
                LocalDate.now(),
                2,
                 45.0
            )


            val resId2 = reservationDao.insert(
                Reservation(
                    resName = "Bob Johnson",
                    noGuests = 1,
                    checkInDate = LocalDate.now(),
                    nights = 5,
                    notes = null
                )
            )

            repo.assignRoom(resId2,
                "102",
                1,
                LocalDate.now().plusDays(2),
                3,
                70.0
            )
        }


    }
}
