package com.example.hotelroll.ui.roll

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.hotelroll.data.database.HotelDatabase
import com.example.hotelroll.domain.HotelManager
import com.example.hotelroll.repository.HotelRepository

class RollViewModelFactory(
    private val context: Context
) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(RollViewModel::class.java)) {

            val db = HotelDatabase.Companion.getInstance(context)

            val manager = HotelManager()

            val repository = HotelRepository(
                db = db,
                reservationDao = db.reservationDao(),
                stayDao = db.stayDao(),
                roomDao = db.roomDao(),
                manager = manager
            )

            @Suppress("UNCHECKED_CAST")
            return RollViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}