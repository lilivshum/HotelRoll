package com.example.hotelroll.ui.roll

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp

import com.example.hotelroll.data.dto.RollItem
import androidx.compose.ui.Alignment
import androidx.compose.ui.graphics.Color
import com.example.hotelroll.data.model.TariffType
import java.time.LocalDate


@Composable
fun RollRow(
    item: RollItem,
    onStayClick: (Long, String, String) -> Unit,
    onEmptyClick: (Long, String, String) -> Unit,
    date: LocalDate
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 12.dp)
            .clickable {
                if(item.stayId != null) {
                    onStayClick(item.stayId, item.roomNumber, item.reservationName?:"")

                } else {
                    onEmptyClick(
                        item.roomId,
                        item.roomNumber,
                        date.toString()
                    )
                }
            },
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = item.roomNumber,
            modifier = Modifier.weight(RollColumns.ROOM),
            style = MaterialTheme.typography.bodyLarge
        )

        Text(
            text = item.reservationName ?: "",
            modifier = Modifier.weight(RollColumns.NAME),
            textAlign = TextAlign.Center
            ,
            style = MaterialTheme.typography.bodyLarge
        )

        // tariff is default for each room as well so each room will always have a default
        // pax is treated as a measure to see if a room has been set or not .. good practice? idk
        var pax = ""
        var tariffString = ""
        item.peopleInRoom?.let {
            pax = item.peopleInRoom.toString()
            tariffString = "$${"%.2f".format(item.tariff)}"

           if(item.tariffType == TariffType.NET) {
                tariffString += ".net"
            } else {
                tariffString += "+tax"
            }
            if((item.kidsInRoom?: 0 )> 0 ){
                pax += "+" + item.kidsInRoom.toString()
            }
        }
        Text(
            text = pax,
            modifier = Modifier.weight(RollColumns.PEOPLE),
            textAlign = TextAlign.Center,
            style = MaterialTheme.typography.bodyLarge
        )


        Text(
            text = tariffString,
            modifier = Modifier.weight(RollColumns.TARIFF),
            textAlign = TextAlign.End,
            style = MaterialTheme.typography.bodyLarge

        )
    }
    HorizontalDivider(
        color = Color.Gray,
        thickness = 1.dp,
        modifier = Modifier.padding(horizontal = 16.dp)
    )
}
