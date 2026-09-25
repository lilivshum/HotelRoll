package com.example.hotelroll.ui.gantt

import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.hotelroll.data.dto.GanttRoomRow
import java.time.LocalDate
import java.time.YearMonth

@Composable
fun GanttTable(
    rows: List<GanttRoomRow>,
    month: YearMonth,
    scrollState: ScrollState,
    today: LocalDate,
    zoomLevel: Float = 1f,
    modifier: Modifier = Modifier
) {
    val daysInMonth = month.lengthOfMonth()

    BoxWithConstraints(modifier = modifier) {
        // Cell width scales with zoom; never smaller than the stretch-to-fill size
        val cellWidth = maxOf(GANTT_CELL_WIDTH * zoomLevel, (maxWidth - GANTT_LABEL_WIDTH) / daysInMonth)

        Column(modifier = Modifier.fillMaxSize()) {
            // Sticky day-number header
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(MaterialTheme.colorScheme.surfaceVariant),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Spacer(modifier = Modifier.width(GANTT_LABEL_WIDTH))
                Row(modifier = Modifier.horizontalScroll(scrollState)) {
                    for (day in 1..daysInMonth) {
                        val date = month.atDay(day)
                        val isToday = date == today
                        Text(
                            text = day.toString(),
                            modifier = Modifier
                                .width(cellWidth)
                                .padding(vertical = 4.dp),
                            textAlign = TextAlign.Center,
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = if (isToday) FontWeight.Bold else FontWeight.Normal,
                            color = if (isToday)
                                MaterialTheme.colorScheme.primary
                            else
                                MaterialTheme.colorScheme.onSurface
                        )
                    }
                }
            }
            HorizontalDivider(thickness = 2.dp)

            LazyColumn(modifier = Modifier.fillMaxSize()) {
                items(rows, key = { it.roomId }) { row ->
                    GanttRow(
                        row = row,
                        month = month,
                        scrollState = scrollState,
                        today = today,
                        cellWidth = cellWidth
                    )
                }
            }
        }
    }
}
