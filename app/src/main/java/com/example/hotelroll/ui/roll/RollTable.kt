package com.example.hotelroll.ui.roll

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
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
    viewModel: RollViewModel,
    showFooterStats: Boolean = false,
    occupiedRooms: Int = 0,
    totalAdults: Int = 0,
    totalKids: Int = 0
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

            if (showFooterStats) {
                item {
                    val pax = if (totalKids > 0) "$totalAdults+$totalKids" else "$totalAdults"
                    Column {
                        HorizontalDivider(modifier = Modifier.padding(top = 8.dp))
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 8.dp, vertical = 12.dp),
                            verticalArrangement = Arrangement.spacedBy(2.dp)
                        ) {
                            Text(
                                text = "Rooms occupied: $occupiedRooms",
                                style = MaterialTheme.typography.labelMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Text(
                                text = "People: $pax",
                                style = MaterialTheme.typography.labelMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            }
        }
    }
}
