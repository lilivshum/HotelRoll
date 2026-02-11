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

    // this now is a state that can either create or update / modify a state
    object CreateStay: HotelRoute(
        "roomId/{roomId}/roomNumber/{roomNumber}/date/{date}/mode/{mode}?stayId={stayId}"
    ) {
        fun createRoute(roomId: Long, roomNumber: String, date: String, mode: StayMode, stayId: Long?): String {
            return if (stayId != null) {
                "roomId/$roomId/roomNumber/$roomNumber/date/$date/mode/$mode?stayId=$stayId"
            } else {
                "roomId/$roomId/roomNumber/$roomNumber/date/$date/mode/$mode"
            }
        }
    }

    object CreateRes: HotelRoute(
        "reservation/create"
    ){
        fun createRoute() = "reservation/create"
    }

}
