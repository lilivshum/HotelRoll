package com.example.hotelroll.ui.gantt

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ZoomIn
import androidx.compose.material.icons.filled.ZoomOut
import androidx.compose.material3.FilledTonalIconButton
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp
import java.time.LocalDate
import java.time.YearMonth

@Composable
fun GanttScreen(viewModel: GanttViewModel) {
    val month by viewModel.month.collectAsState()
    val rows by viewModel.ganttRows.collectAsState()
    val zoomLevel by viewModel.zoomLevel.collectAsState()
    val scrollState = rememberScrollState()
    val today = LocalDate.now()
    val density = LocalDensity.current

    // Scroll to today when on the current month, otherwise reset to day 1
    LaunchedEffect(month) {
        val targetDay = if (month == YearMonth.from(today)) today.dayOfMonth - 1 else 0
        val cellWidthPx = with(density) { GANTT_CELL_WIDTH.toPx() } * zoomLevel
        scrollState.animateScrollTo((targetDay * cellWidthPx).toInt())
    }

    Box(modifier = Modifier.fillMaxSize()) {
        GanttTable(
            rows = rows,
            month = month,
            scrollState = scrollState,
            today = today,
            zoomLevel = zoomLevel,
            modifier = Modifier.fillMaxSize()
        )

        // Zoom controls — bottom-end corner
        Column(
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(12.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            FilledTonalIconButton(
                onClick = { viewModel.setZoom(zoomLevel + GANTT_ZOOM_STEP) },
                enabled = zoomLevel < GANTT_MAX_ZOOM
            ) {
                Icon(Icons.Default.ZoomIn, contentDescription = "Zoom in")
            }
            FilledTonalIconButton(
                onClick = { viewModel.setZoom(zoomLevel - GANTT_ZOOM_STEP) },
                enabled = zoomLevel > GANTT_MIN_ZOOM
            ) {
                Icon(Icons.Default.ZoomOut, contentDescription = "Zoom out")
            }
        }
    }
}
