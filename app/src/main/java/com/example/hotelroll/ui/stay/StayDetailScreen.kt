package com.example.hotelroll.ui.stay

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.hotelroll.HotelApplication
import androidx.compose.material3.*
import androidx.compose.ui.unit.dp
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import com.example.hotelroll.ui.utilities.NotesEditor
import androidx.lifecycle.ViewModelProvider



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
        factory = StayDetailViewModelFactory(
            repository = app.repository,
            stayId = stayId
        )
    )

    LaunchedEffect(stayId) {
        viewModel.loadStay()
    }

    val stay by viewModel.stay.collectAsState()

    val notes by viewModel.notes.collectAsState()

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
                //Spacer(modifier = Modifier.height(24.dp))
                NotesEditor(
                    notes = notes,
                    onNotesChanged = viewModel::onNotesChanged
                )
            }
        }

    }
}


    /*stay?.let { // for testing only ... expand detail screen later
        Text("Reservation Id: ${it.reservationId}")
        Text("Guests: ${it.peopleInRoom}")
    }

}*/
