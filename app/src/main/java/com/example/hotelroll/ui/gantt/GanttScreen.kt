package com.example.hotelroll.ui.gantt

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.rememberScrollState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalDensity
import java.time.LocalDate
import java.time.YearMonth

@Composable
fun GanttScreen(viewModel: GanttViewModel) {
    val month by viewModel.month.collectAsState()
    val rows by viewModel.ganttRows.collectAsState()
    val scrollState = rememberScrollState()
    val today = LocalDate.now()
    val cellWidthPx = with(LocalDensity.current) { GANTT_CELL_WIDTH.toPx() }

    // Scroll to today when on the current month, otherwise reset to day 1
    LaunchedEffect(month) {
        val targetDay = if (month == YearMonth.from(today)) today.dayOfMonth - 1 else 0
        scrollState.animateScrollTo((targetDay * cellWidthPx).toInt())
    }

    GanttTable(
        rows = rows,
        month = month,
        scrollState = scrollState,
        today = today,
        modifier = Modifier.fillMaxSize()
    )
}
