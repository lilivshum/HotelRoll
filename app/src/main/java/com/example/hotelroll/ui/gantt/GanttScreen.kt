package com.example.hotelroll.ui.gantt

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.BoxWithConstraints
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

    BoxWithConstraints(modifier = Modifier.fillMaxSize()) {
        // Width of the scrollable area (excludes the fixed room label column)
        val scrollViewportPx = with(density) { (maxWidth - GANTT_LABEL_WIDTH).toPx() }

        fun zoomTo(newZoom: Float) {
            val oldZoom = viewModel.zoomLevel.value
            val clamped = newZoom.coerceIn(GANTT_MIN_ZOOM, GANTT_MAX_ZOOM)
            if (clamped == oldZoom) return

            // Use actual cell widths (accounting for stretch-to-fill floor) for the ratio,
            // not just the raw zoom values — otherwise the formula is wrong on wide screens
            val baseCellPx = with(density) { GANTT_CELL_WIDTH.toPx() }
            val stretchFloorPx = scrollViewportPx / month.lengthOfMonth()
            val oldCellPx = maxOf(baseCellPx * oldZoom, stretchFloorPx)
            val newCellPx = maxOf(baseCellPx * clamped, stretchFloorPx)
            val ratio = newCellPx / oldCellPx

            // Keep the visible center on the same day:
            //   newScroll = (currentScroll + halfViewport) * ratio - halfViewport
            val halfViewport = scrollViewportPx / 2f
            val delta = (scrollState.value + halfViewport) * ratio - halfViewport - scrollState.value

            // dispatchRawDelta is synchronous — updates scroll state in the same frame as setZoom
            scrollState.dispatchRawDelta(delta)
            viewModel.setZoom(clamped)
        }

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
                onClick = { zoomTo(zoomLevel + GANTT_ZOOM_STEP) },
                enabled = zoomLevel < GANTT_MAX_ZOOM
            ) {
                Icon(Icons.Default.ZoomIn, contentDescription = "Zoom in")
            }
            FilledTonalIconButton(
                onClick = { zoomTo(zoomLevel - GANTT_ZOOM_STEP) },
                enabled = zoomLevel > GANTT_MIN_ZOOM
            ) {
                Icon(Icons.Default.ZoomOut, contentDescription = "Zoom out")
            }
        }
    }
}
