package com.example.hotelroll.ui.stay

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.example.hotelroll.data.model.Stay
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.layout.Arrangement
import com.example.hotelroll.data.model.TariffType

@Composable
fun StayDetailContent(
    stay: Stay,
    modifier: Modifier = Modifier,
    resName: String,
    resNameChanged: Boolean,
    onResNameChange: (String) -> Unit,
    onResSave: () -> Unit
) {
    var pax = stay.peopleInRoom.toString()
    var rate_string = stay.tariff.toString()
    if(stay.tariffType == TariffType.NET) {
        rate_string += ".net"
    } else {
        rate_string += "+tax"
    }
    if(stay.kidsInRoom>0){
        pax = pax + "+" + stay.kidsInRoom.toString()
    }
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        OutlinedTextField(
            value = resName,
            onValueChange = onResNameChange,
            label = { Text("Reservation") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            trailingIcon = {
                if (resNameChanged) {
                    IconButton(onClick = onResSave) {
                        Icon(Icons.Default.Check, contentDescription = "Save name")
                    }
                }
            }
        )
        StayDetailsInfoRow("Guests", pax)
        StayDetailsInfoRow("Check-in", stay.checkInDate.toString())
        StayDetailsInfoRow("Check-out", stay.checkOutDate.toString())
        StayDetailsInfoRow("Rate", rate_string)
        StayDetailsInfoRow("Status", when (stay.status) {
            com.example.hotelroll.data.model.StayStatus.PENDING          -> "Pending"
            com.example.hotelroll.data.model.StayStatus.CONFIRMED        -> "Checked In"
            com.example.hotelroll.data.model.StayStatus.PENDING_CHECKOUT -> "Pending Checkout"
            com.example.hotelroll.data.model.StayStatus.CHECKED_OUT      -> "Checked Out"
            com.example.hotelroll.data.model.StayStatus.ROOM_READY       -> "Room Ready"
        })
    }
}
