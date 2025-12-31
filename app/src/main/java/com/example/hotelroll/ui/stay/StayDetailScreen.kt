package com.example.hotelroll.ui.stay

import androidx.compose.runtime.Composable
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.hotelroll.HotelApplication
import androidx.compose.material3.*
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.material.icons.Icons
import com.example.hotelroll.data.model.Stay
import androidx.compose.foundation.layout.Column




@Composable
fun StayDetailScreen(
    stayId: Long,
    roomNumber: String,
    reservationName: String,
    onBack: () -> Unit = {}
) {
    // this could be optimized better I guess or made more consistent like with the
    // Room Roll Screen constructor

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
    Scaffold(
        topBar = {
            StayDetailsTopBar(
                title = roomNumber,
                onBack = onBack
            )
        }
    ) { padding ->
        stay?.let {
            StayDetailContent(
                stay = it,
                modifier = Modifier.padding(padding),
                reservationName
            )
        }
    }
}


    /*stay?.let { // for testing only ... expand detail screen later
        Text("Reservation Id: ${it.reservationId}")
        Text("Guests: ${it.peopleInRoom}")
    }

}*/
