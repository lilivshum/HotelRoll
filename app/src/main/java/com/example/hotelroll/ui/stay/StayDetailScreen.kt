package com.example.hotelroll.ui.stay

import androidx.compose.runtime.Composable
import com.example.hotelroll.data.model.Stay
import com.example.hotelroll.ui.roll.RollViewModel
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.hotelroll.HotelApplication


@Composable
fun StayDetailScreen(
    stayId: Long
){
    val context = LocalContext.current
    val app = context.applicationContext as HotelApplication

    val viewModel: StayDetailViewModel = viewModel(
        factory = StayDetailViewModelFactory(repository = app.repository)
    )
    LaunchedEffect(stayId) {
        viewModel.loadStay(stayId)
    }

    val stay by viewModel.stay.collectAsState()

    //UI

    stay?.let { // for testing only ... expand detail screen later
        Text("Reservation Id: ${it.reservationId}")
        Text("Guests: ${it.peopleInRoom}")
    }

}
