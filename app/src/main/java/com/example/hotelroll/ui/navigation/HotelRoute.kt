package com.example.hotelroll.ui.navigation

import java.time.LocalDate

sealed class HotelRoute(val route: String) {

    object Roll : HotelRoute("roll")

    object ReservationDetail : HotelRoute(
        "reservation/{reservationId}"
    ) {
        fun createRoute(reservationId: Long) =
            "reservation/$reservationId"
    }

    object StayDetail : HotelRoute(
        "stay/{stayId}/roomNumber/{roomNumber}/reservationName/{reservationName}"
    ) {
        fun createRoute(stayId: Long, roomNumber: String, resName: String) =
            "stay/$stayId/roomNumber/$roomNumber/reservationName/$resName"
    }

    object CreateStay: HotelRoute(
        "roomId/{roomId}/roomNumber/{roomNumber}/date/{date}"
    ) {
        fun createRoute(roomId: Long, roomNumber: String, date: String) =
            "roomId/$roomId/roomNumber/$roomNumber/date/$date"
    }

    object CreateRes: HotelRoute(
        "reservation/create"
    ){
        fun createRoute() = "reservation/create"
    }

}
