package com.example.hotelroll.ui.createStay

import android.util.Log
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.AddTask
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.animation.animateColorAsState
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuAnchorType
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.key.type
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.hotelroll.HotelApplication
import com.example.hotelroll.data.model.Currency
import com.example.hotelroll.data.model.Stay
import com.example.hotelroll.data.model.StayStatus
import com.example.hotelroll.data.model.TariffType
import com.example.hotelroll.ui.createRes.DateField
import com.example.hotelroll.ui.utilities.ConfirmActionDialog
import com.example.hotelroll.ui.utilities.NotesEditor
import com.example.hotelroll.ui.navigation.StayMode
import com.example.hotelroll.ui.utilities.AppDatePickerDialog
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.ui.text.input.KeyboardType
import java.time.format.DateTimeFormatter

private val dateFormatter =
    DateTimeFormatter.ofPattern("MMM dd, yyyy")

@Composable
private fun TariffButton(
    text: String,
    selected: Boolean,
    onClick: () -> Unit
) {
    OutlinedButton(
        onClick = onClick,
        colors = ButtonDefaults.outlinedButtonColors(
            containerColor = if (selected) MaterialTheme.colorScheme.primary else Color.Transparent,
            contentColor = if (selected) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurface
        ),
        border = if (selected) null else ButtonDefaults.outlinedButtonBorder()
    ) {
        Text(text)
    }
}


@Composable
fun TariffTypeSelector(
    selected: TariffType,
    onSelect: (TariffType) -> Unit
) {
    Row(
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        TariffButton(
            text = "Net",
            selected = selected == TariffType.NET,
            onClick = { onSelect(TariffType.NET) }
        )

        TariffButton(
            text = "With tax",
            selected = selected == TariffType.WITH_TAX,
            onClick = { onSelect(TariffType.WITH_TAX) }
        )
    }
}

// for reservation drop down matching
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReservationField(viewModel: CreateStayViewModel) {

    val name by viewModel.reservationName.collectAsState()
    val matches by viewModel.matchingReservations.collectAsState()

    var expanded by remember { mutableStateOf(false) }

    ExposedDropdownMenuBox(
        expanded = expanded && matches.isNotEmpty(),
        onExpandedChange = { expanded = it },
        modifier = Modifier.fillMaxWidth()
    ) {

        TextField(
            value = name,
            onValueChange = {
                viewModel.onNameChange(it)
                expanded = true
            },
            label = { Text("Reservation Name") },
            modifier = Modifier
                .fillMaxWidth()
                .menuAnchor(ExposedDropdownMenuAnchorType.PrimaryEditable)
        )

        ExposedDropdownMenu(
            expanded = expanded && matches.isNotEmpty(),
            onDismissRequest = { expanded = false }
        ) {
            matches.forEach { reservation ->
                DropdownMenuItem(
                    text = { Text(reservation.resName) },
                    onClick = {
                        viewModel.onReservationSelected(reservation)
                        expanded = false
                    }
                )
            }
        }
    }
}

// for currency button toggle
@Composable
fun CurrencySelector(
    selected: Currency,
    onSelect: (Currency) -> Unit
) {
    Row(
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        TariffButton(
            text = "USD",
            selected = selected == Currency.USD,
            onClick = { onSelect(Currency.USD) }
        )

        TariffButton(
            text = "CRC",
            selected = selected == Currency.CRC,
            onClick = { onSelect(Currency.CRC) }
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateStayScreen(
    onCancel: () -> Unit,
    onSaved: () -> Unit
) {
    val context = LocalContext.current
    val app = context.applicationContext as HotelApplication


    val viewModel: CreateStayViewModel = viewModel(
        factory = CreateStayViewModelFactory(
            repository = app.repository,

        )
    )

    val roomNumber = viewModel.roomNumber
    val mode = viewModel.stayMode // observation mode
    val editNotes by viewModel.editNotes.collectAsState()

    // Local string state for numeric fields — avoids snap-back when deleting digits
    var adultsText by remember(viewModel.peopleInRoom) { mutableStateOf(viewModel.peopleInRoom.toString()) }
    var kidsText by remember(viewModel.kidsInRoom) { mutableStateOf(viewModel.kidsInRoom.toString()) }
    var tariffText by remember(viewModel.tariff) {
        mutableStateOf(
            if (viewModel.tariff % 1.0 == 0.0) viewModel.tariff.toInt().toString()
            else viewModel.tariff.toString()
        )
    }

    val activeUser by viewModel.activeUser.collectAsState()

    var activeDateField by remember { mutableStateOf<DateField?>(null) }
    var showRoomPickerDialog by remember { mutableStateOf(false) }
    var roomSearchText by remember { mutableStateOf("") }
    var roomDropdownExpanded by remember { mutableStateOf(false) }

    var showConfirmCreateDialog by remember { mutableStateOf(false) }
    var showConfirmEditDialog by remember { mutableStateOf(false) }
    var showConfirmConfirmDialog by remember { mutableStateOf(false) }
    var showConfirmDeleteDialog by remember { mutableStateOf(false) }


    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    if (mode == StayMode.CREATE) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.clickable {
                                roomSearchText = ""
                                roomDropdownExpanded = false
                                viewModel.loadAvailableRooms()
                                showRoomPickerDialog = true
                            }
                        ) {
                            Text(
                                text = "Room $roomNumber",
                                style = MaterialTheme.typography.headlineSmall
                            )
                            Icon(
                                imageVector = Icons.Default.Edit,
                                contentDescription = "Change room",
                                modifier = Modifier.padding(start = 6.dp),
                                tint = MaterialTheme.colorScheme.primary
                            )
                        }
                    } else {
                        Text(
                            text = "Room $roomNumber",
                            style = MaterialTheme.typography.headlineSmall
                        )
                    }
                },
                navigationIcon = {
                    IconButton(onClick = {
                        viewModel.reset()
                        onCancel()
                    }) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                },

                actions = {
                    if (mode == StayMode.EDIT) {
                        IconButton(
                            onClick = {
                                if (viewModel.stayStatus == StayStatus.CONFIRMED) {
                                    showConfirmDeleteDialog = true
                                } else {
                                    viewModel.deleteStay { onSaved() }
                                }
                            }
                        ) {
                            Icon(
                                Icons.Default.Delete,
                                contentDescription = "Delete",
                                tint = MaterialTheme.colorScheme.primary
                            )
                        }

                        if (viewModel.stayStatus == StayStatus.PENDING) {
                            IconButton(
                                onClick = { showConfirmConfirmDialog = true }
                            ) {
                                Icon(
                                    Icons.Default.AddTask,
                                    contentDescription = "Confirm",
                                    tint = MaterialTheme.colorScheme.primary
                                )
                            }
                        }
                    }
                }
            )

        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 16.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            if (mode == StayMode.CREATE && !viewModel.reservationIsLocked) {
                ReservationField(viewModel)
            } else {
                OutlinedTextField(
                    value = viewModel.reservationName.value,
                    onValueChange = {},
                    readOnly = true,
                    enabled = false,
                    label = { Text("Reservation") },
                    modifier = Modifier.fillMaxWidth()
                )
            }

            OutlinedTextField(
                value = viewModel.stayName ?: "",
                onValueChange = viewModel::onStayNameChange,
                label = { Text("Guest Name") },
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = adultsText,
                onValueChange = { newVal ->
                    adultsText = newVal
                    newVal.toIntOrNull()?.let(viewModel::onPeopleInRoomChange)
                },
                label = { Text("Adults") },
                modifier = Modifier.fillMaxWidth(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                singleLine = true
            )

            OutlinedTextField(
                value = kidsText,
                onValueChange = { newVal ->
                    kidsText = newVal
                    newVal.toIntOrNull()?.let(viewModel::onKidsInRoomChange)
                },
                label = { Text("Kids") },
                modifier = Modifier.fillMaxWidth(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                singleLine = true
            )

            // Check-in date field
            OutlinedTextField(
                value = viewModel.checkInDate.format(dateFormatter),
                onValueChange = {},
                readOnly = true,
                enabled = false,
                label = { Text("Check-in date") },
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { activeDateField = DateField.CHECK_IN }
            )

            // Checkout
            OutlinedTextField(
                value = viewModel.checkOutDate.format(dateFormatter),
                onValueChange = {},
                readOnly = true,
                enabled = false,
                label = { Text("Check-out date") },
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { activeDateField = DateField.CHECK_OUT }
            )

            Text(text = "Nights: ${viewModel.nights}")
//        OutlinedTextField(
//            value = viewModel.nights.toString(),
//            onValueChange = { it.toIntOrNull()?.let(viewModel::onNightsChange) },
//            label = { Text("Nights") },
//            modifier = Modifier.fillMaxWidth()
//        )

            OutlinedTextField(
                value = tariffText,
                onValueChange = { newVal ->
                    tariffText = newVal
                    newVal.toDoubleOrNull()?.let(viewModel::onTariffChange)
                },
                label = { Text("Rate") },
                modifier = Modifier.fillMaxWidth(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                singleLine = true
            )

            Row(
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                TariffTypeSelector(
                    selected = viewModel.tariffType,
                    onSelect = viewModel::onTariffTypeChange
                )

                CurrencySelector(
                    selected = viewModel.currency,
                    onSelect = viewModel::onCurrencyChange
                )
            }


            if (mode == StayMode.CREATE) {
                OutlinedTextField(
                    value = viewModel.notes ?: "",
                    onValueChange = viewModel::onNotesChange,
                    label = { Text("Notes") },
                    modifier = Modifier.fillMaxWidth()
                )
            }

            viewModel.errorMessage?.let {
                Text(it, color = MaterialTheme.colorScheme.error)
            }

            val saveIsDirty = mode == StayMode.EDIT && viewModel.hasUnsavedChanges
            val saveContainerColor by animateColorAsState(
                targetValue = if (saveIsDirty) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surface,
                label = "saveContainerColor"
            )
            val saveContentColor by animateColorAsState(
                targetValue = if (saveIsDirty) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurface,
                label = "saveContentColor"
            )
            val saveBorderColor by animateColorAsState(
                targetValue = if (saveIsDirty) Color.Transparent else MaterialTheme.colorScheme.outline,
                label = "saveBorderColor"
            )

            OutlinedButton(
                onClick = {
                    if (mode == StayMode.CREATE) {
                        showConfirmCreateDialog = true
                    } else if (viewModel.stayStatus == StayStatus.CONFIRMED) {
                        showConfirmEditDialog = true
                    } else {
                        viewModel.save {
                            viewModel.reset()
                            onSaved()
                        }
                    }
                },
                colors = ButtonDefaults.outlinedButtonColors(
                    containerColor = saveContainerColor,
                    contentColor = saveContentColor
                ),
                border = BorderStroke(1.dp, saveBorderColor)
            ) {
                Text(
                    if (mode == StayMode.CREATE)
                        "Create Stay"
                    else
                        "Save Changes"
                )
            }

            if (mode == StayMode.EDIT) {
                NotesEditor(
                    notes = editNotes,
                    onNotesChanged = viewModel::onEditNotesChange
                )
            }

//            if (mode == StayMode.EDIT) {
//
//                Spacer(modifier = Modifier.height(8.dp))
//
//                Row(
//                    modifier = Modifier.fillMaxWidth(),
//                    horizontalArrangement = Arrangement.spacedBy(12.dp)
//                ) {
//
//                    OutlinedButton(
//                        onClick = { showDeleteDialog = true },
//                        colors = ButtonDefaults.outlinedButtonColors(
//                            contentColor = MaterialTheme.colorScheme.error
//                        ),
//                        border = BorderStroke(
//                            1.dp,
//                            MaterialTheme.colorScheme.error
//                        ),
//                        modifier = Modifier.weight(1f)
//                    ) {
//                        Text("Delete")
//                    }
//
//                    Button(
//                        onClick = {
//                            if (viewModel.stayStatus == StayStatus.CONFIRMED) {
//                                // have to add later
//                                Log.d("CreateScreen", "Modify mode on")
//                            } else {
//                                viewModel.confirmStay {
//                                    onSaved()
//                                }
//                            }
//                        },
//                        modifier = Modifier.weight(1f)
//                    ) {
//                        Text(
//                            if (viewModel.stayStatus == StayStatus.CONFIRMED)
//                                "Modify"
//                            else
//                                "Confirm"
//                        )
//                    }
//                }
//            }


            activeDateField?.let { field ->
                val initialDate = when (field) {
                    DateField.CHECK_IN -> viewModel.checkInDate
                    DateField.CHECK_OUT -> viewModel.checkOutDate
                }

                AppDatePickerDialog(
                    initialDate = initialDate,
                    onDateSelected = { selectedDate ->
                        when (field) {
                            DateField.CHECK_IN ->
                                viewModel.onCheckInDateChange(selectedDate)

                            DateField.CHECK_OUT ->
                                viewModel.onCheckOutDateChange(selectedDate)
                        }
                    },
                    onDismiss = { activeDateField = null }
                )
            }

            if (showRoomPickerDialog) {
                val filteredRooms = viewModel.availableRooms.filter {
                    it.roomNumber.contains(roomSearchText, ignoreCase = true)
                }
                AlertDialog(
                    onDismissRequest = { showRoomPickerDialog = false },
                    title = { Text("Change room") },
                    text = {
                        ExposedDropdownMenuBox(
                            expanded = roomDropdownExpanded && filteredRooms.isNotEmpty(),
                            onExpandedChange = { roomDropdownExpanded = it }
                        ) {
                            OutlinedTextField(
                                value = roomSearchText,
                                onValueChange = {
                                    roomSearchText = it
                                    roomDropdownExpanded = true
                                },
                                label = { Text("Room number") },
                                singleLine = true,
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .menuAnchor(ExposedDropdownMenuAnchorType.PrimaryEditable)
                            )
                            ExposedDropdownMenu(
                                expanded = roomDropdownExpanded && filteredRooms.isNotEmpty(),
                                onDismissRequest = { roomDropdownExpanded = false }
                            ) {
                                filteredRooms.forEach { room ->
                                    DropdownMenuItem(
                                        text = { Text("Room ${room.roomNumber}") },
                                        onClick = {
                                            viewModel.changeRoom(room)
                                            showRoomPickerDialog = false
                                            roomDropdownExpanded = false
                                        }
                                    )
                                }
                            }
                        }
                    },
                    confirmButton = {},
                    dismissButton = {
                        TextButton(onClick = { showRoomPickerDialog = false }) {
                            Text("Cancel")
                        }
                    }
                )
            }

            if (showConfirmCreateDialog) {
                ConfirmActionDialog(
                    title = "Create stay?",
                    body = "A new stay will be created in Room $roomNumber.",
                    activeUserName = activeUser?.name ?: "Unknown",
                    onConfirm = {
                        showConfirmCreateDialog = false
                        viewModel.save { onSaved() }
                    },
                    onDismiss = { showConfirmCreateDialog = false }
                )
            }

            if (showConfirmEditDialog) {
                ConfirmActionDialog(
                    title = "Save changes?",
                    body = "Changes to the confirmed stay in Room $roomNumber will be saved.",
                    activeUserName = activeUser?.name ?: "Unknown",
                    onConfirm = {
                        showConfirmEditDialog = false
                        viewModel.save {
                            viewModel.reset()
                            onSaved()
                        }
                    },
                    onDismiss = { showConfirmEditDialog = false }
                )
            }

            if (showConfirmConfirmDialog) {
                ConfirmActionDialog(
                    title = "Confirm stay?",
                    body = "Stay in Room $roomNumber will be marked as confirmed.",
                    activeUserName = activeUser?.name ?: "Unknown",
                    onConfirm = {
                        showConfirmConfirmDialog = false
                        viewModel.confirmStay {
                            viewModel.reset()
                            onSaved()
                        }
                    },
                    onDismiss = { showConfirmConfirmDialog = false }
                )
            }

            if (showConfirmDeleteDialog) {
                ConfirmActionDialog(
                    title = "Delete stay?",
                    body = "The confirmed stay in Room $roomNumber will be permanently deleted.",
                    activeUserName = activeUser?.name ?: "Unknown",
                    isDestructive = true,
                    confirmLabel = "Delete",
                    onConfirm = {
                        showConfirmDeleteDialog = false
                        viewModel.deleteStay { onSaved() }
                    },
                    onDismiss = { showConfirmDeleteDialog = false }
                )
            }

        }
    }
}
