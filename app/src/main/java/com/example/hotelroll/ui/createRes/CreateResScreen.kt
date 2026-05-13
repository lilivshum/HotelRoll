package com.example.hotelroll.ui.createRes

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.hotelroll.HotelApplication
import com.example.hotelroll.ui.utilities.AppDatePickerDialog
import com.example.hotelroll.ui.utilities.ConfirmActionDialog
import java.time.format.DateTimeFormatter

private val dateFormatter =
    DateTimeFormatter.ofPattern("MMM dd, yyyy")

// for date picker only
enum class DateField {
    CHECK_IN,
    CHECK_OUT
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateResScreen (
    onBack: () -> Unit,
    onSave: () -> Unit,
    onCancel: () -> Unit,

) {
    val context = LocalContext.current
    val app = context.applicationContext as HotelApplication

    val viewModel: CreateResViewModel = viewModel(
        factory = CreateResViewModelFactory(repository = app.repository)
    )


    val activeUser by viewModel.activeUser.collectAsState()
    var showConfirmDialog by remember { mutableStateOf(false) }

    // Local string state for numeric fields — avoids snap-back when deleting digits
    var guestsText by remember(viewModel.noGuests) { mutableStateOf(viewModel.noGuests.toString()) }
    var kidsText by remember(viewModel.noKids) { mutableStateOf(viewModel.noKids.toString()) }

    var activeDateField by remember { mutableStateOf<DateField?>(null) }

    // For date display only
    val checkInDate = viewModel.checkInDate
    val checkOutDate = viewModel.checkOutDate

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Create Reservation") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
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
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {

            OutlinedTextField(
                value = viewModel.resName,
                onValueChange = viewModel::onResNameChange,
                label = { Text("Reservation name") },
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = guestsText,
                onValueChange = { newVal ->
                    guestsText = newVal
                    newVal.toIntOrNull()?.let(viewModel::onGuestsChange)
                },
                label = { Text("Number of Guests") },
                modifier = Modifier.fillMaxWidth(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                singleLine = true
            )

            OutlinedTextField(
                value = kidsText,
                onValueChange = { newVal ->
                    kidsText = newVal
                    newVal.toIntOrNull()?.let(viewModel::onKidsChange)
                },
                label = { Text("Number of Kids") },
                modifier = Modifier.fillMaxWidth(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                singleLine = true
            )

            // Check-in date field
            OutlinedTextField(
                value = checkInDate.format(dateFormatter),
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
                value = checkOutDate.format(dateFormatter),
                onValueChange = {},
                readOnly = true,
                enabled = false,
                label = { Text("Check-out date") },
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { activeDateField = DateField.CHECK_OUT }
            )

            Text(text = "Nights: ${viewModel.nights}")

            OutlinedTextField(
                value = viewModel.notes,
                onValueChange = viewModel::onNotesChange,
                label = { Text("Notes") },
                modifier = Modifier.fillMaxWidth()
            )

            Row(
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Button(onClick = {
                    viewModel.reset()
                    onCancel()
                }) {
                    Text("Cancel")
                }

                Button(onClick = { showConfirmDialog = true }) {
                    Text("Save")
                }

            }

        }

    }

    if (showConfirmDialog) {
        ConfirmActionDialog(
            title = "Create reservation?",
            body = "A new reservation will be created for \"${viewModel.resName}\".",
            activeUserName = activeUser?.name ?: "Unknown",
            onConfirm = {
                showConfirmDialog = false
                viewModel.save {
                    viewModel.reset()
                    onSave()
                }
            },
            onDismiss = { showConfirmDialog = false }
        )
    }

    // Date picker code -- chatgpt generated dont ask me anything about it

    activeDateField?.let { field ->
        val initialDate = when (field) {
            DateField.CHECK_IN -> checkInDate
            DateField.CHECK_OUT -> checkOutDate
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

}