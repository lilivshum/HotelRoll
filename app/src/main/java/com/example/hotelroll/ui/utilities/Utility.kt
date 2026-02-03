package com.example.hotelroll.ui.utilities

import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId

fun millisToLocalDate(millis: Long): LocalDate =
    Instant.ofEpochMilli(millis)
        .atZone(ZoneId.of("UTC"))
        .toLocalDate()

