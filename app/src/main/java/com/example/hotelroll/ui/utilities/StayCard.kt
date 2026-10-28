package com.example.hotelroll.ui.utilities

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.hotelroll.data.dto.RollItem
import com.example.hotelroll.ui.roll.RollViewModel

// visual indicator of an assigned stay inside a RoomRow — interaction is handled by the row
@Composable
fun StayCard(
    item: RollItem,
    viewModel: RollViewModel
) {
    val isSelected = viewModel.selectedItem?.stayId == item.stayId

    val displayName = when {
        item.incomingResName != null -> "${item.reservationName} → ${item.incomingResName}"
        else -> item.reservationName ?: ""
    }
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(0.dp),
        colors = CardDefaults.cardColors(
            containerColor = androidx.compose.ui.graphics.Color.Transparent
        )
    ) {
        Text(
            text = displayName,
            modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp),
            style = MaterialTheme.typography.bodyMedium.copy(
                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                color = MaterialTheme.colorScheme.onSurface
            )
        )
    }
}
