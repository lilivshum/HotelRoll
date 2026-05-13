package com.example.hotelroll.ui.stay

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import com.example.hotelroll.ui.utilities.NotesEditor



@Composable
fun StayDetailScreen(
    viewModel: StayDetailViewModel,
    roomNumber: String,
    reservationName: String,
    onBack: () -> Unit = {},
    onEdit: ((roomId: Long, roomNumber: String, date: String, stayId: Long) -> Unit)? = null,
    onDeleted: (() -> Unit)? = null
) {
    val stay by viewModel.stay.collectAsState()
    val notes by viewModel.notes.collectAsState()

    var showDeleteDialog by remember { mutableStateOf(false) }

    val lifecycleOwner = LocalLifecycleOwner.current
    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_RESUME) viewModel.loadStay()
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose { lifecycleOwner.lifecycle.removeObserver(observer) }
    }

    Scaffold(
        topBar = {
            StayDetailsTopBar(
                title = roomNumber,
                onBack = onBack,
                onEdit = if (onEdit != null) {
                    {
                        stay?.let { s ->
                            onEdit(s.roomId, roomNumber, s.checkInDate.toString(), s.stayId)
                        }
                    }
                } else null,
                onDelete = if (onDeleted != null) {
                    { showDeleteDialog = true }
                } else null
            )
        }
    ) { padding ->
        stay?.let {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
            ) {
                StayDetailContent(
                    stay = it,
                    modifier = Modifier.padding(padding),
                    reservationName
                )
                NotesEditor(
                    notes = notes,
                    onNotesChanged = viewModel::onNotesChanged
                )
            }
        }
    }

    if (showDeleteDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            confirmButton = {
                TextButton(
                    onClick = {
                        viewModel.deleteStay { onDeleted?.invoke() }
                        showDeleteDialog = false
                    }
                ) { Text("Delete") }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteDialog = false }) {
                    Text("Cancel")
                }
            },
            title = { Text("Delete Stay?") },
            text = { Text("This action cannot be undone.") }
        )
    }
}


    /*stay?.let { // for testing only ... expand detail screen later
        Text("Reservation Id: ${it.reservationId}")
        Text("Guests: ${it.peopleInRoom}")
    }

}*/
