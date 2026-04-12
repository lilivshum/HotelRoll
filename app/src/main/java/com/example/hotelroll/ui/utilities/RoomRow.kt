package com.example.hotelroll.ui.utilities

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.hotelroll.data.dto.RollItem
import com.example.hotelroll.data.model.Currency
import com.example.hotelroll.data.model.StayStatus
import com.example.hotelroll.data.model.TariffType
import com.example.hotelroll.ui.navigation.StayMode
import com.example.hotelroll.ui.roll.RollColumns
import com.example.hotelroll.ui.roll.RollViewModel
import java.time.LocalDate

// each room row in the roll — long press to enter move mode, tap destination to place
@OptIn(ExperimentalFoundationApi::class)
@Composable
fun RoomRow(
    item: RollItem,
    viewModel: RollViewModel,
    onStayClick: (Long, String, String) -> Unit,
    onEmptyClick: (Long, String, String, StayMode, Long?) -> Unit,
    date: LocalDate
) {
    val selectedItem = viewModel.selectedItem
    val isSource = selectedItem?.roomId == item.roomId
    val isDropTarget = selectedItem != null && !isSource

    val backgroundColor = when {
        isDropTarget -> MaterialTheme.colorScheme.primary.copy(alpha = 0.12f)
        isSource -> MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.3f)
        item.stayStatus == StayStatus.CONFIRMED -> MaterialTheme.colorScheme.primary.copy(alpha = 0.08f)
        else -> Color.Transparent
    }

    val borderColor = when {
        isDropTarget -> MaterialTheme.colorScheme.primary
        isSource -> MaterialTheme.colorScheme.primary.copy(alpha = 0.5f)
        else -> Color.Transparent
    }

    val textStyle = if (item.stayStatus == StayStatus.CONFIRMED)
        MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Bold)
    else
        MaterialTheme.typography.bodyLarge

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 12.dp)
            .background(backgroundColor)
            .border(1.dp, borderColor, RoundedCornerShape(8.dp))
            .clip(RoundedCornerShape(8.dp))
            .combinedClickable(
                onClick = {
                    when {
                        // tap source row → deselect
                        isSource -> viewModel.clearSelection()
                        // tap a destination row → move stay
                        isDropTarget -> {
                            viewModel.moveStayToRoom(selectedItem!!, item.roomId) {}
                            viewModel.clearSelection()
                        }
                        // normal navigation when nothing is selected
                        item.stayId != null && item.stayStatus == StayStatus.CONFIRMED ->
                            onStayClick(item.stayId, item.roomNumber, item.reservationName ?: "")
                        item.stayId != null ->
                            onEmptyClick(item.roomId, item.roomNumber, date.toString(), StayMode.EDIT, item.stayId)
                        else ->
                            onEmptyClick(item.roomId, item.roomNumber, date.toString(), StayMode.CREATE, null)
                    }
                },
                onLongClick = {
                    if (item.stayId != null) viewModel.selectItem(item)
                }
            ),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = item.roomNumber,
            modifier = Modifier.weight(RollColumns.ROOM),
            style = textStyle
        )

        Box(modifier = Modifier.weight(RollColumns.NAME)) {
            if (item.stayId != null) {
                StayCard(item, viewModel)
            }
        }

        val pax = item.peopleInRoom?.let {
            if ((item.kidsInRoom ?: 0) > 0) "${item.peopleInRoom}+${item.kidsInRoom}"
            else item.peopleInRoom.toString()
        } ?: ""

        val tariffString = item.peopleInRoom?.let {
            val currencySymbol = when (item.currency) {
                Currency.USD -> "$"
                Currency.CRC -> "₡"
                else -> "$"
            }
            val base = "$currencySymbol${"%.2f".format(item.tariff)}"
            if (item.tariffType == TariffType.NET) "$base.net" else "$base+tax"
        } ?: ""

        Text(
            text = pax,
            modifier = Modifier.weight(RollColumns.PEOPLE),
            textAlign = TextAlign.Center,
            style = MaterialTheme.typography.bodyLarge
        )

        Text(
            text = tariffString,
            modifier = Modifier.weight(RollColumns.TARIFF),
            textAlign = TextAlign.End,
            style = MaterialTheme.typography.bodyLarge
        )
    }
    HorizontalDivider(
        color = Color.Gray,
        thickness = 1.dp,
        modifier = Modifier.padding(horizontal = 16.dp)
    )
}
