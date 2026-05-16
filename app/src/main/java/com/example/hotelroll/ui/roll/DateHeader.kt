package com.example.hotelroll.ui.roll

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.hotelroll.ui.utilities.AppDatePickerDialog
import java.time.LocalDate
import java.time.YearMonth
import java.time.format.DateTimeFormatter
import java.util.Locale

@Composable
fun DateHeader(
    date: LocalDate,
    onPrevious: () -> Unit,
    onNext: () -> Unit,
    onMenuClick: () -> Unit,
    viewModel: RollViewModel,
    isGanttView: Boolean,
    onToggleView: () -> Unit,
    ganttMonth: YearMonth,
    onPrevMonth: () -> Unit,
    onNextMonth: () -> Unit
) {
    var showDatePicker by remember { mutableStateOf(false) }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        IconButton(
            onClick = onMenuClick,
            modifier = Modifier.size(40.dp)
        ) {
            Icon(imageVector = Icons.Default.Menu, contentDescription = "Menu")
        }

        if (isGanttView) {
            IconButton(onClick = onPrevMonth) {
                Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Previous month")
            }
            Text(
                text = ganttMonth.format(DateTimeFormatter.ofPattern("MMMM yyyy", Locale.getDefault())),
                fontWeight = FontWeight.Bold
            )
            IconButton(onClick = onNextMonth) {
                Icon(Icons.AutoMirrored.Filled.ArrowForward, contentDescription = "Next month")
            }
        } else {
            IconButton(onClick = onPrevious) {
                Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Previous day")
            }
            Text(
                text = date.format(DateTimeFormatter.ofPattern("MMMM d, yyyy", Locale.getDefault())),
                fontWeight = FontWeight.Bold,
                modifier = Modifier.clickable { showDatePicker = true }
            )
            IconButton(onClick = onNext) {
                Icon(Icons.AutoMirrored.Filled.ArrowForward, contentDescription = "Next day")
            }
        }

        IconButton(
            onClick = onToggleView,
            modifier = Modifier.size(40.dp)
        ) {
            Icon(
                imageVector = if (isGanttView) Icons.AutoMirrored.Filled.List else Icons.Default.DateRange,
                contentDescription = if (isGanttView) "Switch to roll view" else "Switch to calendar view"
            )
        }
    }

    if (showDatePicker && !isGanttView) {
        AppDatePickerDialog(
            initialDate = date,
            onDateSelected = { selectedDate ->
                viewModel.onDateChange(selectedDate)
                showDatePicker = false
            },
            onDismiss = { showDatePicker = false }
        )
    }
}
