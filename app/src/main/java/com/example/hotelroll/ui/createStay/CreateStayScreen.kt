package com.example.hotelroll.ui.createStay

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.hotelroll.HotelApplication
import com.example.hotelroll.ui.res.ReservationDetailViewModel
import com.example.hotelroll.ui.res.ReservationDetailViewModelFactory
import java.time.LocalDate

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

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text(
            text = "Create Stay – Room $roomNumber",
            style = MaterialTheme.typography.headlineSmall
        )

        OutlinedTextField(
            value = viewModel.resName,
            onValueChange = viewModel::onResNameChange,
            label = { Text("Reservation name") },
            modifier = Modifier.fillMaxWidth()
        )

        OutlinedTextField(
            value = viewModel.peopleInRoom.toString(),
            onValueChange = { it.toIntOrNull()?.let(viewModel::onPeopleInRoomChange) },
            label = { Text("Adults") },
            modifier = Modifier.fillMaxWidth()
        )

        OutlinedTextField(
            value = viewModel.kidsInRoom.toString(),
            onValueChange = { it.toIntOrNull()?.let(viewModel::onKidsInRoomChange) },
            label = { Text("Kids") },
            modifier = Modifier.fillMaxWidth()
        )

        OutlinedTextField(
            value = viewModel.nights.toString(),
            onValueChange = { it.toIntOrNull()?.let(viewModel::onNightsChange) },
            label = { Text("Nights") },
            modifier = Modifier.fillMaxWidth()
        )

        OutlinedTextField(
            value = viewModel.notes?: "",
            onValueChange = viewModel::onNotesChange,
            label = { Text("Notes") },
            modifier = Modifier.fillMaxWidth()
        )

        viewModel.errorMessage?.let {
            Text(it, color = MaterialTheme.colorScheme.error)
        }

        Row(
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Button(onClick = {
                viewModel.reset()
                onCancel()
            }) {
                Text("Cancel")
            }

            Button(onClick = { viewModel.save{
                viewModel.reset()
                onSaved()} }) {
                Text("Save")
            }
        }
    }
}
