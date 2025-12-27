package com.example.hotelroll.ui.roll

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel

import com.example.hotelroll.ui.room.RollViewModel

@Composable
fun RoomScreen(
    viewModel: RollViewModel = viewModel()
) {
    val roomRoll by viewModel.roll.collectAsState()
    val selectedDate by viewModel.date.collectAsState()

    Column(
        modifier = Modifier.fillMaxSize()
    ) {
        DateHeader(
            date = selectedDate,
            onPrevious = viewModel::prevDay,
            onNext = viewModel::nextDay
        )

        HorizontalDivider()

        LazyColumn(
            modifier = Modifier.fillMaxSize()
        ) {
            items(
                items = roomRoll,
                key = { it.roomId }   // IMPORTANT for stable scrolling
            ) { item ->
                RoomRollRow(item)
            }
        }
    }
}
