package com.example.hotelroll.ui.gantt

import android.content.SharedPreferences
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.hotelroll.data.dto.GanttRoomRow
import com.example.hotelroll.repository.HotelRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.stateIn
import java.time.YearMonth

private const val KEY_ZOOM = "gantt_zoom_level"

@OptIn(ExperimentalCoroutinesApi::class)
class GanttViewModel(
    private val repository: HotelRepository,
    private val prefs: SharedPreferences
) : ViewModel() {

    private val _month = MutableStateFlow(YearMonth.now())
    val month: StateFlow<YearMonth> = _month

    val ganttRows: StateFlow<List<GanttRoomRow>> = _month
        .flatMapLatest { ym -> repository.getGanttRooms(ym) }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun nextMonth() { _month.value = _month.value.plusMonths(1) }
    fun prevMonth() { _month.value = _month.value.minusMonths(1) }

    private val _zoomLevel = MutableStateFlow(
        prefs.getFloat(KEY_ZOOM, 1f).coerceIn(GANTT_MIN_ZOOM, GANTT_MAX_ZOOM)
    )
    val zoomLevel: StateFlow<Float> = _zoomLevel

    fun setZoom(zoom: Float) {
        val clamped = zoom.coerceIn(GANTT_MIN_ZOOM, GANTT_MAX_ZOOM)
        _zoomLevel.value = clamped
        prefs.edit().putFloat(KEY_ZOOM, clamped).apply()
    }
}
