package com.example.hotelroll.ui.roll

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Divider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.graphics.Color
import com.example.hotelroll.data.dto.RollItem




@Composable
fun RollTable(rollItems: List<RollItem>) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp)
    ) {
        // Header
        RollTableHeader()

        // Vertical scrolling for rows
        LazyColumn(
            modifier = Modifier.fillMaxSize()
        ) {
            items(rollItems) { item ->
                RollRow(item)
            }
        }
    }
}

