package com.example.hotelroll.ui.roll

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.hotelroll.data.dto.RollItem
import com.example.hotelroll.ui.navigation.StayMode
import com.example.hotelroll.ui.utilities.RoomRow
import java.time.LocalDate

@Composable
fun RollTable(
    rollItems: List<RollItem>,
    onStayClick: (Long, String, String) -> Unit,
    onEmptyClick: (Long, String, String, StayMode, Long?) -> Unit,
    date: LocalDate,
    viewModel: RollViewModel
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp)
    ) {
        RollTableHeader()

        LazyColumn(
            modifier = Modifier.fillMaxSize()
        ) {
            items(
                items = rollItems,
                key = { it.roomId }
            ) { item ->
                RoomRow(item, viewModel, onStayClick, onEmptyClick, date)
            }
        }
    }
}
