package com.example.hotelroll.ui.settings

import android.content.Context
import android.content.SharedPreferences
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

const val PREFS_NAME = "hotelroll_settings"
const val KEY_IS_MASTER = "is_master"

sealed class DriveState {
    object NotConnected : DriveState()
    object Loading : DriveState()
    data class Connected(
        val accountEmail: String,
        val lastBackup: String? = null
    ) : DriveState()
}

class SettingsViewModel(private val prefs: SharedPreferences) : ViewModel() {

    private val _isMaster = MutableStateFlow(prefs.getBoolean(KEY_IS_MASTER, false))
    val isMaster: StateFlow<Boolean> = _isMaster

    // Drive integration is not yet wired — always NotConnected for now
    private val _driveState = MutableStateFlow<DriveState>(DriveState.NotConnected)
    val driveState: StateFlow<DriveState> = _driveState

    fun setMaster(enabled: Boolean) {
        _isMaster.value = enabled
        prefs.edit().putBoolean(KEY_IS_MASTER, enabled).apply()
    }

    // Stubs — filled in when Drive integration is implemented
    fun signInWithGoogle() { /* TODO */ }
    fun signOut() { /* TODO */ }
    fun backupNow() { /* TODO */ }
    fun syncNow() { /* TODO */ }
}
