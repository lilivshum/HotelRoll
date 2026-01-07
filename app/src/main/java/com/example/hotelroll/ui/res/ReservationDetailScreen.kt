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
import androidx.compose.material3.HorizontalDivider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.resolveDefaults
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.hotelroll.HotelApplication
import com.example.hotelroll.ui.stay.StayDetailViewModel
import com.example.hotelroll.ui.stay.StayDetailViewModelFactory


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
        factory = ReservationDetailViewModelFactory(repository = app.repository)
    )
    LaunchedEffect(reservationId) {
        viewModel.loadReservationInfo(reservationId)
    }

    val reservation = viewModel.reservation.value
    val stays = viewModel.staysUi.value

    reservation?.let {
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
                ResDetailInfoRow("Guests", reservation.noGuests.toString())
                ResDetailInfoRow("Check-in", reservation.checkInDate.toString())
                ResDetailInfoRow("Nights", reservation.nights.toString())
                Text("Stays")
                ResRoomsSection(stays, reservation.resName, onStayClick)
                ResNotesSection(reservation.notes)

            }
        }
    }
}
