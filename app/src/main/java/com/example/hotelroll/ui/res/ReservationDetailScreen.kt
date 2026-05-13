package com.example.hotelroll.ui.res

import com.example.hotelroll.data.model.Reservation
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.compose.material3.Scaffold
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.Text
import androidx.compose.material3.IconButton
import androidx.compose.material3.Icon
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.History
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import com.example.hotelroll.ui.utilities.ConfirmActionDialog
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.hotelroll.HotelApplication
import com.example.hotelroll.ui.createRes.DateField
import com.example.hotelroll.ui.navigation.HotelRoute
import com.example.hotelroll.ui.navigation.StayMode
import com.example.hotelroll.ui.utilities.AppDatePickerDialog
import com.example.hotelroll.ui.utilities.NotesEditor


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReservationDetailScreen(
    reservationId: Long,
    onBackClick: () -> Unit,
    onStayClick: (Long, String, String) -> Unit,
    onAddStay: (roomId: Long, roomNumber: String, date: String, mode: StayMode, stayId: Long?, reservationId: Long?) -> Unit,
    onHistoryClick: (Long) -> Unit
) {

    val context = LocalContext.current
    val app = context.applicationContext as HotelApplication

    val viewModel: ReservationDetailViewModel = viewModel(
        factory = ReservationDetailViewModelFactory(
            repository = app.repository,
            reservationId = reservationId
        )
    )

    LaunchedEffect(reservationId) {
        viewModel.loadReservationInfo()
    }

    val activeUser by viewModel.activeUser.collectAsState()
    val reservation = viewModel.reservation.value
    val stays = viewModel.staysUi.value
    val notes by viewModel.notes.collectAsState()

    var showCloseDialog by remember { mutableStateOf(false) }
    var showDeleteDialog by remember { mutableStateOf(false) }
    var isEditing by remember { mutableStateOf(false) }

    // Local string state for numeric fields to avoid snap-back
    var guestsText by remember(viewModel.noGuests) { mutableStateOf(viewModel.noGuests.toString()) }
    var kidsText by remember(viewModel.noKids) { mutableStateOf(viewModel.noKids.toString()) }

    var activeDateField by remember { mutableStateOf<DateField?>(null) }

    reservation?.let {
        var pax = reservation.noGuests.toString()
        if (reservation.noKids > 0) pax = "$pax+${reservation.noKids}"

        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text(if (isEditing) viewModel.resName else reservation.resName) },
                    navigationIcon = {
                        IconButton(onClick = {
                            if (isEditing) {
                                isEditing = false
                                // reset edit fields back to current reservation values
                                viewModel.loadReservationInfo()
                            } else {
                                onBackClick()
                            }
                        }) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                contentDescription = "Back"
                            )
                        }
                    },
                    actions = {
                        if (isEditing) {
                            IconButton(onClick = {
                                viewModel.saveEdits { isEditing = false }
                            }) {
                                Icon(
                                    Icons.Default.Check,
                                    contentDescription = "Save",
                                    tint = MaterialTheme.colorScheme.primary
                                )
                            }
                        } else if (reservation.isActive) {
                            IconButton(onClick = { onHistoryClick(reservationId) }) {
                                Icon(
                                    Icons.Default.History,
                                    contentDescription = "History",
                                    tint = MaterialTheme.colorScheme.primary
                                )
                            }
                            IconButton(onClick = { isEditing = true }) {
                                Icon(
                                    Icons.Default.Edit,
                                    contentDescription = "Edit",
                                    tint = MaterialTheme.colorScheme.primary
                                )
                            }
                            IconButton(onClick = {
                                if (viewModel.historyEntryCount <= 1) showDeleteDialog = true
                                else showCloseDialog = true
                            }) {
                                Icon(
                                    Icons.Default.Delete,
                                    contentDescription = "Close reservation",
                                    tint = MaterialTheme.colorScheme.primary
                                )
                            }
                        }
                    }
                )
            }
        ) { padding ->
            Column(
                modifier = Modifier
                    .padding(padding)
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                if (isEditing) {
                    OutlinedTextField(
                        value = viewModel.resName,
                        onValueChange = { viewModel.resName = it },
                        label = { Text("Reservation Name") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )
                    OutlinedTextField(
                        value = guestsText,
                        onValueChange = { v ->
                            guestsText = v
                            v.toIntOrNull()?.let { viewModel.noGuests = it }
                        },
                        label = { Text("Adults") },
                        modifier = Modifier.fillMaxWidth(),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        singleLine = true
                    )
                    OutlinedTextField(
                        value = kidsText,
                        onValueChange = { v ->
                            kidsText = v
                            v.toIntOrNull()?.let { viewModel.noKids = it }
                        },
                        label = { Text("Kids") },
                        modifier = Modifier.fillMaxWidth(),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        singleLine = true
                    )
                    OutlinedTextField(
                        value = viewModel.checkInDate.toString(),
                        onValueChange = {},
                        readOnly = true,
                        enabled = false,
                        label = { Text("Check-in date") },
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { activeDateField = DateField.CHECK_IN }
                    )
                    OutlinedTextField(
                        value = viewModel.checkOutDate.toString(),
                        onValueChange = {},
                        readOnly = true,
                        enabled = false,
                        label = { Text("Check-out date") },
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { activeDateField = DateField.CHECK_OUT }
                    )
                    Text("Nights: ${viewModel.nights}")
                } else {
                    ResDetailInfoRow("Guests", pax)
                    ResDetailInfoRow("Check-in", reservation.checkInDate.toString())
                    ResDetailInfoRow("Check-out", reservation.checkInDate.plusDays(reservation.nights.toLong()).toString())
                    ResDetailInfoRow("Nights", reservation.nights.toString())
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("Stays", style = MaterialTheme.typography.titleMedium)
                        if (reservation.isActive) {
                            IconButton(onClick = {
                                viewModel.navigateToAddStay { roomId, roomNumber, date, reservationId ->
                                    onAddStay(roomId, roomNumber, date, StayMode.CREATE, null, reservationId)
                                }
                            }) {
                                Icon(
                                    Icons.Default.Add,
                                    contentDescription = "Add stay",
                                    tint = MaterialTheme.colorScheme.primary
                                )
                            }
                        }
                    }
                    ResRoomsSection(stays, reservation.resName, onStayClick)
                }

                viewModel.errorMessage?.let {
                    Text(it, color = MaterialTheme.colorScheme.error)
                }

                NotesEditor(
                    notes = notes,
                    onNotesChanged = viewModel::onNotesChanged
                )
            }
        }

        if (showCloseDialog) {
            ConfirmActionDialog(
                title = "Close reservation?",
                body = "\"${reservation.resName}\" will be moved to Past. It can still be viewed but not edited.",
                activeUserName = activeUser?.name ?: "Unknown",
                isDestructive = true,
                confirmLabel = "Close",
                onConfirm = {
                    showCloseDialog = false
                    viewModel.closeReservation { onBackClick() }
                },
                onDismiss = { showCloseDialog = false }
            )
        }

        if (showDeleteDialog) {
            ConfirmActionDialog(
                title = "Delete reservation?",
                body = "\"${reservation.resName}\" will be permanently deleted. This cannot be undone.",
                activeUserName = activeUser?.name ?: "Unknown",
                isDestructive = true,
                confirmLabel = "Delete",
                onConfirm = {
                    showDeleteDialog = false
                    viewModel.hardDeleteReservation { onBackClick() }
                },
                onDismiss = { showDeleteDialog = false }
            )
        }

        activeDateField?.let { field ->
            val initialDate = when (field) {
                DateField.CHECK_IN -> viewModel.checkInDate
                DateField.CHECK_OUT -> viewModel.checkOutDate
            }
            AppDatePickerDialog(
                initialDate = initialDate,
                onDateSelected = { selectedDate ->
                    when (field) {
                        DateField.CHECK_IN -> viewModel.checkInDate = selectedDate
                        DateField.CHECK_OUT -> viewModel.onCheckOutDateChange(selectedDate)
                    }
                    activeDateField = null
                },
                onDismiss = { activeDateField = null }
            )
        }
    }
}
