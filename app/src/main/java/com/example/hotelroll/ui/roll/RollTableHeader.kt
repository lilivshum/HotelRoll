package com.example.hotelroll.ui.roll

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp


@Composable
fun RollTableHeader() {
    Row(
        modifier = Modifier
            .background(MaterialTheme.colorScheme.surfaceVariant)
            .padding(horizontal = 16.dp, vertical = 12.dp)
    ) {
        Text(
            text = "Room",
            modifier = Modifier.weight(RollColumns.ROOM),
            fontWeight = FontWeight.Bold,
            style = MaterialTheme.typography.bodyMedium
        )
        Text(
            text = "Reservation",
            modifier = Modifier.weight(RollColumns.NAME),
            fontWeight = FontWeight.Bold
        )
        Text(
            text = "Pax",
            modifier = Modifier.weight(RollColumns.PEOPLE),
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center
        )
        Text(
            text = "Tariff",
            modifier = Modifier.weight(RollColumns.TARIFF),
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center
        )
    }
    HorizontalDivider(thickness = 2.dp)
}
