package com.example.hotelroll.ui.gantt

import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.hotelroll.data.dto.GanttRoomRow
import com.example.hotelroll.data.model.Stay
import com.example.hotelroll.data.model.StayStatus
import java.time.LocalDate
import java.time.YearMonth

val GANTT_CELL_WIDTH = 36.dp
val GANTT_LABEL_WIDTH = 52.dp

@Composable
fun GanttRow(
    row: GanttRoomRow,
    month: YearMonth,
    scrollState: ScrollState,
    today: LocalDate,
    cellWidth: Dp
) {
    val monthStart = month.atDay(1)
    val monthEnd = month.atEndOfMonth()

    // Pre-build day → stay map for O(1) lookup per cell
    val stayByDay: Map<LocalDate, Stay> = buildMap {
        row.stays.forEach { stay ->
            var d = maxOf(stay.checkInDate, monthStart)
            val end = minOf(stay.checkOutDate.minusDays(1), monthEnd)
            while (d <= end) {
                put(d, stay)
                d = d.plusDays(1)
            }
        }
    }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(36.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Fixed room label — does not scroll
        Box(
            modifier = Modifier
                .width(GANTT_LABEL_WIDTH)
                .fillMaxHeight()
                .background(
                    if (row.isBlocked) Color(0xFFFF9800).copy(alpha = 0.18f)
                    else Color.Transparent
                ),
            contentAlignment = Alignment.CenterStart
        ) {
            Text(
                text = row.roomNumber,
                modifier = Modifier.padding(start = 8.dp),
                style = MaterialTheme.typography.bodySmall
            )
        }

        // Scrollable day cells
        Row(modifier = Modifier.horizontalScroll(scrollState)) {
            for (day in 1..month.lengthOfMonth()) {
                val date = monthStart.plusDays(day.toLong() - 1)
                val stay = stayByDay[date]
                val isToday = date == today
                val isBarStart = stay != null &&
                        (date == stay.checkInDate || date == monthStart)

                val bgColor = when {
                    stay != null && stay.status == StayStatus.CONFIRMED ->
                        MaterialTheme.colorScheme.primary.copy(alpha = 0.75f)
                    stay != null ->
                        MaterialTheme.colorScheme.primary.copy(alpha = 0.28f)
                    row.isBlocked ->
                        Color(0xFFFF9800).copy(alpha = 0.18f)
                    isToday ->
                        MaterialTheme.colorScheme.surfaceVariant
                    else -> Color.Transparent
                }

                Box(
                    modifier = Modifier
                        .width(cellWidth)
                        .fillMaxHeight()
                        .background(bgColor),
                    contentAlignment = Alignment.CenterStart
                ) {
                    if (isBarStart) {
                        Text(
                            text = row.stayDisplayNames[stay!!.stayId] ?: "",
                            style = MaterialTheme.typography.labelSmall.copy(fontSize = 9.sp),
                            maxLines = 1,
                            overflow = TextOverflow.Clip,
                            modifier = Modifier.padding(start = 2.dp),
                            color = if (stay.status == StayStatus.CONFIRMED)
                                MaterialTheme.colorScheme.onPrimary
                            else
                                MaterialTheme.colorScheme.onSurface
                        )
                    }
                }
            }
        }
    }

    HorizontalDivider(color = Color.Gray.copy(alpha = 0.25f), thickness = 0.5.dp)
}
