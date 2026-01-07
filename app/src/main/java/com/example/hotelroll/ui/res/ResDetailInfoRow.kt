package com.example.hotelroll.ui.res

import androidx.compose.foundation.clickable
import androidx.compose.runtime.Composable
import androidx.compose.foundation.layout.Row
import androidx.compose.ui.Modifier
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.ui.unit.dp
import androidx.compose.material3.MaterialTheme
import com.example.hotelroll.data.model.Stay
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.HorizontalDivider
import androidx.compose.ui.Alignment
import androidx.compose.ui.text.style.TextAlign
import java.time.LocalDate
import androidx.compose.foundation.lazy.items
import com.example.hotelroll.repository.HotelRepository
import com.example.hotelroll.ui.navigation.HotelNavGraph
import com.example.hotelroll.ui.utilities.StayUi

@Composable
fun ResDetailInfoRow(
    label: String,
    value: String
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodyLarge
        )
        Text(
            text = value,
            style = MaterialTheme.typography.bodyLarge
        )
    }
}

@Composable
fun ResNotesSection(notes: String?) {
    TextField(
        value = notes ?: "",
        onValueChange = {},          // no-op (read-only)
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        label = { Text("Notes") },
        readOnly = true,
        singleLine = false,
        minLines = 3
    )
}

// row that displays compacted Stay info
@Composable
fun StayRow(
    roomNumber: String,
    people: Int,
    checkIn: LocalDate,
    checkOut: LocalDate,
    onClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(horizontal = 16.dp, vertical = 12.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Room $roomNumber",
                modifier = Modifier.weight(1f),
                style = MaterialTheme.typography.bodyLarge
            )

            Text(
                text = "$people pax",
                modifier = Modifier.weight(1f),
                textAlign = TextAlign.Center,
                style = MaterialTheme.typography.bodyMedium
            )

            Text(
                text = "$checkIn → $checkOut",
                modifier = Modifier.weight(2f),
                textAlign = TextAlign.End,
                style = MaterialTheme.typography.bodySmall
            )
        }

        HorizontalDivider(
            modifier = Modifier.padding(top = 8.dp)
        )
    }
}

@Composable
fun ResRoomsSection(stays: List<StayUi>,
                    resName: String,
                    onStayClick: (Long, String, String) -> Unit){
    LazyColumn {
        items(stays) { stay ->
            StayRow(
                stay.roomNumber,
                stay.people,
                stay.checkIn,
                stay.checkOut,
                onClick = { onStayClick(stay.stayId, stay.roomNumber, resName)}
                )
        }
    }
}
