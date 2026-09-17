package com.example.hotelroll.ui.settings

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.Scope
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

const val PREFS_NAME = "hotelroll_settings"
const val KEY_IS_MASTER = "is_master"
private const val KEY_LAST_BACKUP = "last_backup"

// drive.file scope: app can only see files it created — cannot touch user's personal Drive
private const val DRIVE_FILE_SCOPE = "https://www.googleapis.com/auth/drive.file"

sealed class DriveState {
    object NotConnected : DriveState()
    object Loading : DriveState()
    data class Connected(
        val accountEmail: String,
        val lastBackup: String? = null
    ) : DriveState()
}

class SettingsViewModel(
    private val prefs: SharedPreferences,
    private val appContext: Context
) : ViewModel() {

    private val _isMaster = MutableStateFlow(prefs.getBoolean(KEY_IS_MASTER, false))
    val isMaster: StateFlow<Boolean> = _isMaster

    private val _driveState = MutableStateFlow<DriveState>(DriveState.NotConnected)
    val driveState: StateFlow<DriveState> = _driveState

    // Screen collects this to launch the system sign-in chooser
    private val _signInIntent = MutableSharedFlow<Intent>(extraBufferCapacity = 1)
    val signInIntent: SharedFlow<Intent> = _signInIntent

    private val signInClient: GoogleSignInClient

    init {
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestEmail()
            .requestScopes(Scope(DRIVE_FILE_SCOPE))
            .build()
        signInClient = GoogleSignIn.getClient(appContext, gso)

        // Restore state if already signed in from a previous session
        val account = GoogleSignIn.getLastSignedInAccount(appContext)
        if (account != null && account.email != null) {
            _driveState.value = DriveState.Connected(
                accountEmail = account.email!!,
                lastBackup = prefs.getString(KEY_LAST_BACKUP, null)
            )
        }
    }

    fun setMaster(enabled: Boolean) {
        _isMaster.value = enabled
        prefs.edit().putBoolean(KEY_IS_MASTER, enabled).apply()
    }

    fun signInWithGoogle() {
        viewModelScope.launch {
            _driveState.value = DriveState.Loading
            _signInIntent.emit(signInClient.signInIntent)
        }
    }

    /** Called by the screen after the sign-in activity returns. */
    fun onSignInResult(account: GoogleSignInAccount?) {
        if (account?.email != null) {
            _driveState.value = DriveState.Connected(
                accountEmail = account.email!!,
                lastBackup = prefs.getString(KEY_LAST_BACKUP, null)
            )
        } else {
            _driveState.value = DriveState.NotConnected
        }
    }

    fun signOut() {
        signInClient.signOut().addOnCompleteListener {
            _driveState.value = DriveState.NotConnected
        }
    }

    fun backupNow() { /* TODO: step 2 */ }
    fun syncNow() { /* TODO: step 2 */ }
}
