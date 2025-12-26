package com.example.hotelroll.data.database

// file that helps with type conversions from domain to database

import androidx.room.TypeConverter
import java.time.LocalDate
import com.example.hotelroll.data.model.StayStatus

class Converters {
    @TypeConverter
    fun fromLocalDate(date: LocalDate): String = date.toString()

    @TypeConverter
    fun toLocalDate(value: String): LocalDate = LocalDate.parse(value)

    @TypeConverter
    fun fromStayStatus(status: StayStatus): String =
        status.name

    @TypeConverter
    fun toStayStatus(value: String): StayStatus =
        StayStatus.valueOf(value)
}