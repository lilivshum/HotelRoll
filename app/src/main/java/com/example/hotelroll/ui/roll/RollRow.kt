package com.example.hotelroll.ui.roll

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp

import com.example.hotelroll.data.dto.RollItem
import androidx.compose.ui.Alignment
import androidx.compose.ui.graphics.Color
import com.example.hotelroll.ui.roll.RollColumns

@Composable
fun RollRow(
    item: RollItem
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = item.roomNumber,
            modifier = Modifier.weight(RollColumns.ROOM),
            style = MaterialTheme.typography.bodyLarge
        )

        Text(
            text = item.reservationName ?: "— Empty —",
            modifier = Modifier.weight(RollColumns.NAME),
            style = MaterialTheme.typography.bodyMedium
        )

        Text(
            text = item.peopleInRoom?.toString() ?: " -- ",
            modifier = Modifier.weight(RollColumns.PEOPLE),
            textAlign = TextAlign.Center,
            style = MaterialTheme.typography.bodyMedium
        )

        Text(
            text = item.tariff?.let { "$${"%.2f".format(it)}" } ?: "-",
            modifier = Modifier.weight(RollColumns.TARIFF),
            textAlign = TextAlign.End,
            style = MaterialTheme.typography.bodyMedium

        )
    }
    HorizontalDivider(
        color = Color.Gray,
        thickness = 1.dp,
        modifier = Modifier.padding(horizontal = 16.dp)
    )
}
