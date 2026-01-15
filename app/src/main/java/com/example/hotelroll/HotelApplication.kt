package com.example.hotelroll


import android.app.Application
import com.example.hotelroll.data.database.HotelDatabase
import com.example.hotelroll.data.model.Reservation
import com.example.hotelroll.data.seed.DEFAULT_ROOMS
import com.example.hotelroll.domain.HotelManager
import com.example.hotelroll.repository.HotelRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.time.LocalDate

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

    override fun onCreate() {
        super.onCreate()

        CoroutineScope(Dispatchers.IO).launch {
            seedDatabase()
        }
    }

    private suspend fun seedDatabase() {
        val roomDao = database.roomDao()
        val reservationDao = database.reservationDao()

        if (roomDao.countRooms() == 0) {
            roomDao.insertAll(DEFAULT_ROOMS)
        }

        if (reservationDao.countReservations() == 0) {
            val resId1 = reservationDao.insert(
                Reservation(
                    resName = "Alice Smith",
                    noGuests = 2,
                    noKids = 2,
                    checkInDate = LocalDate.now(),
                    nights = 2,
                    notes = "Late arrival"
                )
            )

            repository.assignRoom(
                resId1,
                "101",
                2,
                kidsInRoom = 2,
                LocalDate.now(),
                2,
                45.0,
                ""
            )


            val resId2 = reservationDao.insert(
                Reservation(
                    resName = "Bob Johnson",
                    noGuests = 1,
                    checkInDate = LocalDate.now(),
                    noKids = 0,
                    nights = 5,
                    notes = null
                )
            )

            repository.assignRoom(
                resId2,
                "102",
                1,
                kidsInRoom = 0,
                LocalDate.now().plusDays(2),
                3,
                70.0,
                ""
            )
        }
    }
}
