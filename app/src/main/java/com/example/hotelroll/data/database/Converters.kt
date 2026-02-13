package com.example.hotelroll.data.database

// file that helps with type conversions from domain to database

import androidx.room.TypeConverter
import com.example.hotelroll.data.model.Currency
import java.time.LocalDate
import com.example.hotelroll.data.model.StayStatus
import com.example.hotelroll.data.model.TariffType

class Converters {
    @TypeConverter
    fun fromLocalDate(date: LocalDate): String = date.toString()

    @TypeConverter
    fun toLocalDate(value: String): LocalDate = LocalDate.parse(value)

    @TypeConverter
    fun fromStayStatus(status: StayStatus?): String? =
        status?.name

    @TypeConverter
    fun toStayStatus(value: String?): StayStatus {
        return value?.let { StayStatus.valueOf(it) }
            ?: StayStatus.PENDING
    }

    @TypeConverter
    fun fromTariffType(value: TariffType?): String? {
        return value?.name
    }

    @TypeConverter
    fun toTariffType(value: String?): TariffType? {
        return value?.let { TariffType.valueOf(it) }
    }

    @TypeConverter
    fun fromCurrency(value: Currency?): String? {
        return value?.name
    }

    @TypeConverter
    fun toCurrency(value: String?): Currency? {
        return value?.let { Currency.valueOf(it) }
    }

}