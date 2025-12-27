package com.example.hotelroll.ui.roll

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.hotelroll.ui.roll.DateHeader

@Composable
fun RollScreen(viewModel: RollViewModel) {
    // Collect the list of RollItems from the ViewModel
    val rollItems by viewModel.roll.collectAsState(initial = emptyList())

    // Scaffold is optional, but useful for padding/top bars
    Scaffold { paddingValues ->
        Column(modifier = Modifier.padding(paddingValues)) {
            // Date navigation header
            DateHeader(
                date = viewModel.date.collectAsState().value,
                onPrevious = viewModel::prevDay,
                onNext = viewModel::nextDay
            )

            Spacer(modifier = Modifier.height(8.dp))

            rollItems.forEach { println(it) }

            // Table with horizontal scroll + vertical scrolling inside
            RollTable(rollItems = rollItems)
        }
    }
}