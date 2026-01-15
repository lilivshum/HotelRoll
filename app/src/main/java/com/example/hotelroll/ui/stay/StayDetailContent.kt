package com.example.hotelroll.ui.stay

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.example.hotelroll.data.model.Stay
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.layout.Arrangement

@Composable
fun StayDetailContent(
    stay: Stay,
    modifier: Modifier = Modifier,
    reservationName: String
) {
    var pax = stay.peopleInRoom.toString()
    if(stay.kidsInRoom>0){
        pax = pax + "+" + stay.kidsInRoom.toString()
    }
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        StayDetailsInfoRow("Reservation", reservationName)
        StayDetailsInfoRow("Guests", pax)
        StayDetailsInfoRow("Check-in", stay.checkInDate.toString())
        StayDetailsInfoRow("Check-out", stay.checkOutDate.toString())
        StayDetailsInfoRow("Status", stay.status.name)
    }
}
