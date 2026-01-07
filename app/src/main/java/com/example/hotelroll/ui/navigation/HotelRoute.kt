package com.example.hotelroll.ui.navigation

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
}
