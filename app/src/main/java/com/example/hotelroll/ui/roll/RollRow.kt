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
import com.example.hotelroll.data.model.Currency
import com.example.hotelroll.data.model.StayStatus
import com.example.hotelroll.data.model.TariffType
import com.example.hotelroll.ui.navigation.StayMode
import java.time.LocalDate


@Composable
fun RollRow(
    item: RollItem,
    onStayClick: (Long, String, String) -> Unit,
    onEmptyClick: (Long, String, String, StayMode, Long?) -> Unit,
    date: LocalDate
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 12.dp)
            .clickable {
                if(item.stayId != null) {
                    if(item.stayStatus == StayStatus.CONFIRMED){
                        onStayClick(item.stayId, item.roomNumber, item.reservationName?:"")
                    } else {
                        onEmptyClick(item.roomId, item.roomNumber, date.toString(), StayMode.EDIT, item.stayId)
                    }

                } else {
                    onEmptyClick(
                        item.roomId,
                        item.roomNumber,
                        date.toString(),
                        StayMode.CREATE,
                        null
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
        val pax = item.peopleInRoom?.let {
            if ((item.kidsInRoom ?: 0) > 0) {
                "${item.peopleInRoom}+${item.kidsInRoom}"
            } else {
                item.peopleInRoom.toString()
            }
        } ?: ""


        val tariffString = item.peopleInRoom?.let {
            val currencystring = when(item.currency){
                Currency.USD -> "$"
                Currency.CRC -> "₡"
                else -> "$"
            }

            val base = "$currencystring${"%.2f".format(item.tariff)}"
            if (item.tariffType == TariffType.NET) {
                "$base.net"
            } else {
                "$base+tax"
            }
        } ?: ""

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
