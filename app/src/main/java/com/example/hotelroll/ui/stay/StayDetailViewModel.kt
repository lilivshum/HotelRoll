package com.example.hotelroll.ui.stay

import androidx.compose.runtime.collectAsState
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.SavedStateHandle
import com.example.hotelroll.repository.HotelRepository
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.hotelroll.HotelApplication
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import com.example.hotelroll.data.model.Stay
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged


@OptIn(ExperimentalCoroutinesApi::class, FlowPreview::class)
class StayDetailViewModel (
    private val repository: HotelRepository,
    savedStateHandle: SavedStateHandle

): ViewModel() {

    // 1️⃣ ViewModel OWNS the stayId
    private val stayId: Long=
        checkNotNull(savedStateHandle["stayId"]) {
            "stayId is required"
        }

    private val _stay = MutableStateFlow<Stay?>(null)
    val stay: StateFlow<Stay?> = _stay

    private val notesFlow = MutableStateFlow("")
    val notes = notesFlow.asStateFlow()

    init {
        loadStay()

        // 🔹 Auto-save with debounce
        viewModelScope.launch {
            notesFlow
                .debounce(500)        // wait for typing to stop
                .distinctUntilChanged()
                .collect { newNotes ->
                    val current = _stay.value ?: return@collect
                    repository.updateStayNotes(current.stayId, newNotes)
                }
        }
    }

    fun loadStay() {
        viewModelScope.launch {
            _stay.value = repository.getStayById(stayId)
            notesFlow.value = _stay.value?.notes.orEmpty()
        }
    }

    fun onNotesChanged(text: String){
        notesFlow.value = text
    }

    fun deleteStay(onDone: () -> Unit) {
        viewModelScope.launch {
            _stay.value?.let { repository.deleteStay(it.stayId) }
            onDone()
        }
    }

}