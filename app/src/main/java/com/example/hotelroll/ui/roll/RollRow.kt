package com.example.hotelroll.ui.roll

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp

import com.example.hotelroll.data.dto.RollItem
import androidx.compose.ui.Alignment

@Composable
fun RoomRollRow(
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
            modifier = Modifier.weight(1f),
            style = MaterialTheme.typography.bodyLarge
        )

        Text(
            text = item.reservationName ?: "— Empty —",
            modifier = Modifier.weight(2f),
            style = MaterialTheme.typography.bodyMedium
        )

        Text(
            text = item.peopleInRoom?.toString() ?: "",
            modifier = Modifier.weight(1f),
            textAlign = TextAlign.End,
            style = MaterialTheme.typography.bodyMedium
        )
    }
}
