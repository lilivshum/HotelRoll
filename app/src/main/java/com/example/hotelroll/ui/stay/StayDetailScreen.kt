package com.example.hotelroll.ui.stay

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.History
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import com.example.hotelroll.data.model.RoomStatus
import com.example.hotelroll.data.model.StayStatus
import com.example.hotelroll.ui.utilities.ConfirmActionDialog
import com.example.hotelroll.ui.utilities.NotesEditor
import java.time.LocalDate



@Composable
fun StayDetailScreen(
    viewModel: StayDetailViewModel,
    roomNumber: String,
    onBack: () -> Unit = {},
    onHistoryClick: ((reservationId: Long) -> Unit)? = null,
    onEdit: ((roomId: Long, roomNumber: String, date: String, stayId: Long) -> Unit)? = null,
    onDeleted: (() -> Unit)? = null
) {
    val stay by viewModel.stay.collectAsState()
    val notes by viewModel.notes.collectAsState()
    val activeUser by viewModel.activeUser.collectAsState()

    var showDeleteDialog by remember { mutableStateOf(false) }
    var showCheckInDialog by remember { mutableStateOf(false) }
    var showUndoCheckInDialog by remember { mutableStateOf(false) }
    var showCheckOutDialog by remember { mutableStateOf(false) }
    var showRoomReadyDialog by remember { mutableStateOf(false) }

    val lifecycleOwner = LocalLifecycleOwner.current
    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_RESUME) viewModel.loadStay()
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose { lifecycleOwner.lifecycle.removeObserver(observer) }
    }

    val isPast = stay?.checkOutDate?.isBefore(java.time.LocalDate.now()) == true

    Scaffold(
        topBar = {
            StayDetailsTopBar(
                title = roomNumber,
                onBack = onBack,
                onCheckIn = if (stay?.status == StayStatus.PENDING && !isPast)
                    { { showCheckInDialog = true } } else null,
                onUndoCheckIn = if (stay?.status == StayStatus.CONFIRMED &&
                    stay?.checkInDate?.isEqual(LocalDate.now()) == true)
                    { { showUndoCheckInDialog = true } } else null,
                onCheckOut = if (stay?.status == StayStatus.CONFIRMED && stay?.checkOutDate == LocalDate.now())
                    { { showCheckOutDialog = true } } else null,
                onRoomReady = if (viewModel.roomStatus.collectAsState().value == RoomStatus.DIRTY)
                    { { showRoomReadyDialog = true } } else null,
                onEdit = if (onEdit != null && !isPast) {
                    {
                        stay?.let { s ->
                            onEdit(s.roomId, roomNumber, s.checkInDate.toString(), s.stayId)
                        }
                    }
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
                    resName = viewModel.resName,
                    resNameChanged = viewModel.resNameChanged,
                    onResNameChange = viewModel::onResNameChange,
                    onResSave = viewModel::saveResName
                )
                NotesEditor(
                    notes = notes,
                    onNotesChanged = viewModel::onNotesChanged
                )
                Spacer(modifier = Modifier.weight(1f))
                HorizontalDivider()
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 8.dp, vertical = 4.dp),
                    horizontalArrangement = Arrangement.End,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    val resId = viewModel.reservationId
                    if (onHistoryClick != null && resId != null) {
                        IconButton(onClick = { onHistoryClick(resId) }) {
                            Icon(Icons.Default.History, contentDescription = "History",
                                tint = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                    }
                    if (onDeleted != null && !isPast) {
                        IconButton(onClick = { showDeleteDialog = true }) {
                            Icon(Icons.Default.Delete, contentDescription = "Delete",
                                tint = MaterialTheme.colorScheme.error)
                        }
                    }
                }
            }
        }
    }

    if (showCheckInDialog) {
        ConfirmActionDialog(
            title = "Check in guest?",
            body = "Mark the guest in Room $roomNumber as arrived.",
            activeUserName = activeUser?.name ?: "Unknown",
            confirmLabel = "Check in",
            onConfirm = { showCheckInDialog = false; viewModel.checkIn() },
            onDismiss = { showCheckInDialog = false }
        )
    }

    if (showUndoCheckInDialog) {
        ConfirmActionDialog(
            title = "Undo check-in?",
            body = "Room $roomNumber will return to pending status.",
            activeUserName = activeUser?.name ?: "Unknown",
            confirmLabel = "Undo",
            onConfirm = { showUndoCheckInDialog = false; viewModel.undoCheckIn() },
            onDismiss = { showUndoCheckInDialog = false }
        )
    }

    if (showCheckOutDialog) {
        ConfirmActionDialog(
            title = "Check out guest?",
            body = "Mark the guest in Room $roomNumber as checked out. The room will need cleaning.",
            activeUserName = activeUser?.name ?: "Unknown",
            confirmLabel = "Check out",
            onConfirm = { showCheckOutDialog = false; viewModel.checkOut() },
            onDismiss = { showCheckOutDialog = false }
        )
    }

    if (showRoomReadyDialog) {
        ConfirmActionDialog(
            title = "Room ready?",
            body = "Mark Room $roomNumber as clean and available.",
            activeUserName = activeUser?.name ?: "Unknown",
            confirmLabel = "Room ready",
            onConfirm = { showRoomReadyDialog = false; viewModel.markRoomReady() },
            onDismiss = { showRoomReadyDialog = false }
        )
    }

    if (showDeleteDialog) {
        val stayLabel = when (stay?.status) {
            StayStatus.CONFIRMED -> "checked-in stay"
            StayStatus.CHECKED_OUT -> "checked-out stay"
            else -> "pending stay"
        }
        ConfirmActionDialog(
            title = "Delete stay?",
            body = "The $stayLabel in Room $roomNumber will be permanently deleted.",
            activeUserName = activeUser?.name ?: "Unknown",
            isDestructive = true,
            confirmLabel = "Delete",
            onConfirm = {
                showDeleteDialog = false
                viewModel.deleteStay { onDeleted?.invoke() }
            },
            onDismiss = { showDeleteDialog = false }
        )
    }
}


    /*stay?.let { // for testing only ... expand detail screen later
        Text("Reservation Id: ${it.reservationId}")
        Text("Guests: ${it.peopleInRoom}")
    }

}*/
