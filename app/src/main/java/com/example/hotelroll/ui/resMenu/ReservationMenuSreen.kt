package com.example.hotelroll.ui.resMenu

import androidx.compose.runtime.Composable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.clickable
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.hotelroll.data.model.Reservation
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue


@Composable
fun ReservationMenuScreen(
    viewModel: ReservationViewModel,
    onReservationClick: (Long) -> Unit
) {
    val reservations by viewModel.reservations
        .collectAsState()

    Column(
        modifier = Modifier
            .fillMaxHeight()
            .width(280.dp)
            .padding(16.dp)
    ) {
        Text(
            text = "Reservations (${reservations.size})",
            style = MaterialTheme.typography.titleLarge,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        LazyColumn {
            items(reservations) { item ->
                Text(
                    text = item.resName,
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable {
                            onReservationClick(item.id)
                        }
                        .padding(vertical = 12.dp)
                )
            }
        }
    }
}
