package com.example.hotelroll.data.database

// file that helps with type conversions from domain to database

import androidx.room.TypeConverter
import com.example.hotelroll.data.model.Currency
import com.example.hotelroll.data.model.HistoryEventType
import java.time.LocalDate
import com.example.hotelroll.data.model.StayStatus
import com.example.hotelroll.data.model.TariffType

class Converters {
    @TypeConverter
    fun fromLocalDate(date: LocalDate?): String? = date?.toString()

    @TypeConverter
    fun toLocalDate(value: String?): LocalDate? = value?.let { LocalDate.parse(it) }

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

    @TypeConverter
    fun fromHistoryEventType(value: HistoryEventType?): String? = value?.name

    @TypeConverter
    fun toHistoryEventType(value: String?): HistoryEventType? =
        value?.let { HistoryEventType.valueOf(it) }

}