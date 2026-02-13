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
import androidx.compose.foundation.layout.Column
import androidx.compose.ui.Modifier
import androidx.compose.foundation.layout.padding
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.TextButton
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.resolveDefaults
import androidx.lifecycle.viewmodel.compose.LocalViewModelStoreOwner
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.hotelroll.HotelApplication
import com.example.hotelroll.ui.navigation.StayMode
import com.example.hotelroll.ui.stay.StayDetailViewModel
import com.example.hotelroll.ui.stay.StayDetailViewModelFactory
import com.example.hotelroll.ui.utilities.NotesEditor


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReservationDetailScreen(
    reservationId: Long,
    onBackClick: () -> Unit,
    onStayClick: (Long, String, String) -> Unit
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

    val reservation = viewModel.reservation.value
    val stays = viewModel.staysUi.value
    val notes by viewModel.notes.collectAsState()

    var showDeleteDialog by remember { mutableStateOf(false) }

    reservation?.let {
        // function that displays kids and adults in a adults + kids way
        var pax = reservation.noGuests.toString()
        if(reservation.noKids > 0){
            pax = pax + "+" + reservation.noKids.toString()
        }
        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text(reservation.resName) },
                    navigationIcon = {
                        IconButton(onClick = onBackClick) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                contentDescription = "Back"
                            )
                        }
                    },
                    actions = {

                        if(!viewModel.hasConfirmedStay) {
                            IconButton(
                                onClick = { showDeleteDialog = true }
                            ) {
                                Icon(
                                    Icons.Default.Delete,
                                    contentDescription = "Delete",
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
                ResDetailInfoRow("Guests", pax)
                ResDetailInfoRow("Check-in", reservation.checkInDate.toString())
                ResDetailInfoRow("Nights", reservation.nights.toString())
                Text("Stays")
                ResRoomsSection(stays, reservation.resName, onStayClick)

                viewModel.errorMessage?.let {
                    Text(it, color = MaterialTheme.colorScheme.error)
                }

                NotesEditor(
                    notes = notes,
                    onNotesChanged = viewModel::onNotesChanged
                )

            }
        }
        if (showDeleteDialog) {
            AlertDialog(
                onDismissRequest = { showDeleteDialog = false },
                confirmButton = {
                    TextButton(
                        onClick = {
                            viewModel.deleteRes { onBackClick() }
                            showDeleteDialog = false
                        }
                    ) { Text("Delete") }
                },
                dismissButton = {
                    TextButton(onClick = { showDeleteDialog = false }) {
                        Text("Cancel")
                    }
                },
                title = { Text("Delete Reservation ${reservation.resName} ?") },
                text = { Text("This action cannot be undone.") }
            )
        }
    }
}
