package com.example.hotelroll.ui.createStay

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuAnchorType
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
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
import com.example.hotelroll.data.model.TariffType
import com.example.hotelroll.ui.createRes.DateField
import com.example.hotelroll.ui.navigation.StayMode
import com.example.hotelroll.ui.utilities.AppDatePickerDialog
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
        onExpandedChange = { expanded = it }
    ) {

        TextField(
            value = name,
            onValueChange = {
                viewModel.onNameChange(it)
                expanded = true
            },
            label = { Text("Reservation Name") },
            modifier = Modifier.menuAnchor(ExposedDropdownMenuAnchorType.PrimaryEditable)

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
    val resName = viewModel.reservationName.value

    var activeDateField by remember { mutableStateOf<DateField?>(null) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text(
            text = "Room $roomNumber",
            style = MaterialTheme.typography.headlineSmall
        )

        if(mode == StayMode.CREATE){
//            OutlinedTextField(
//                value = viewModel.resName,
//                onValueChange = viewModel::onResNameChange,
//                label = { Text("Reservation name") },
//                modifier = Modifier.fillMaxWidth()
//            )
            ReservationField(viewModel)
        } else {
            Text("Reservation Name: $resName",
                style = MaterialTheme.typography.headlineSmall)
        }

        OutlinedTextField(
            value = viewModel.stayName ?: "",
            onValueChange = viewModel::onStayNameChange,
            label = {Text("Guest Name")},
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
            value = viewModel.tariff.toString(),
            onValueChange = { it.toDoubleOrNull()?.let(viewModel::onTariffChange) },
            label = { Text("Rate") },
            modifier = Modifier.fillMaxWidth()
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
    }
}
