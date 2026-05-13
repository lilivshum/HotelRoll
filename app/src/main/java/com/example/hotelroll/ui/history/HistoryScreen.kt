package com.example.hotelroll.ui.history

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.hotelroll.HotelApplication
import com.example.hotelroll.data.model.HistoryEntry
import com.example.hotelroll.data.model.HistoryEventType
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HistoryScreen(
    reservationId: Long,
    onBackClick: () -> Unit
) {
    val context = LocalContext.current
    val app = context.applicationContext as HotelApplication

    val viewModel: HistoryViewModel = viewModel(
        factory = HistoryViewModelFactory(
            repository = app.repository,
            reservationId = reservationId
        )
    )

    val entries by viewModel.entries.collectAsState(initial = emptyList())

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("History") },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                }
            )
        }
    ) { padding ->
        if (entries.isEmpty()) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .padding(24.dp),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    "No history yet",
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .padding(padding)
                    .padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(0.dp)
            ) {
                items(entries) { entry ->
                    HistoryEntryRow(entry)
                    HorizontalDivider()
                }
            }
        }
    }
}

@Composable
private fun HistoryEntryRow(entry: HistoryEntry) {
    val formatter = SimpleDateFormat("MMM d, yyyy · HH:mm", Locale.getDefault())
    val timeLabel = formatter.format(Date(entry.timestamp))

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 12.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalAlignment = Alignment.Top
    ) {
        Icon(
            imageVector = entry.eventType.icon(),
            contentDescription = null,
            tint = entry.eventType.tint(),
            modifier = Modifier.padding(top = 2.dp)
        )
        Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
            Text(entry.eventType.label(), style = MaterialTheme.typography.bodyMedium)
            entry.note?.let {
                Text(it, style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
            Text(
                "${entry.userName} · $timeLabel",
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

private fun HistoryEventType.label(): String = when (this) {
    HistoryEventType.RESERVATION_CREATED -> "Reservation created"
    HistoryEventType.STAY_CREATED        -> "Stay created"
    HistoryEventType.STAY_CONFIRMED      -> "Stay confirmed"
    HistoryEventType.STAY_EDITED         -> "Stay edited"
    HistoryEventType.ROOM_MOVED          -> "Room moved"
    HistoryEventType.STAY_DELETED        -> "Stay deleted"
    HistoryEventType.RESERVATION_CLOSED  -> "Reservation closed"
}

private fun HistoryEventType.icon(): ImageVector = when (this) {
    HistoryEventType.RESERVATION_CREATED -> Icons.Default.Add
    HistoryEventType.STAY_CREATED        -> Icons.Default.Add
    HistoryEventType.STAY_CONFIRMED      -> Icons.Default.Check
    HistoryEventType.STAY_EDITED         -> Icons.Default.Edit
    HistoryEventType.ROOM_MOVED          -> Icons.AutoMirrored.Filled.ArrowForward
    HistoryEventType.STAY_DELETED        -> Icons.Default.Delete
    HistoryEventType.RESERVATION_CLOSED  -> Icons.Default.Close
}

@Composable
private fun HistoryEventType.tint() = when (this) {
    HistoryEventType.STAY_DELETED,
    HistoryEventType.RESERVATION_CLOSED -> MaterialTheme.colorScheme.error
    HistoryEventType.STAY_CONFIRMED     -> MaterialTheme.colorScheme.primary
    else                                -> MaterialTheme.colorScheme.onSurfaceVariant
}
