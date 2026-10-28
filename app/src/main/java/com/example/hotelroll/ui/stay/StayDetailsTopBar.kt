package com.example.hotelroll.ui.stay
import androidx.compose.runtime.Composable
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.Text
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material.icons.Icons
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.HowToReg
import androidx.compose.material.icons.filled.Logout
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material3.MaterialTheme

// idk what this is? its experimental?
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StayDetailsTopBar(
    title: String,
    onBack: (() -> Unit)? = null,
    onCheckIn: (() -> Unit)? = null,
    onUndoCheckIn: (() -> Unit)? = null,
    onCheckOut: (() -> Unit)? = null,
    onRoomReady: (() -> Unit)? = null,
    onEdit: (() -> Unit)? = null,
) {
    CenterAlignedTopAppBar(
        title = { Text(title) },
        navigationIcon = {
            if (onBack != null) {
                IconButton(onClick = onBack) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Back"
                    )
                }
            }
        },
        actions = {
            if (onCheckIn != null) {
                IconButton(onClick = onCheckIn) {
                    Icon(Icons.Default.HowToReg, contentDescription = "Check in",
                        tint = MaterialTheme.colorScheme.primary)
                }
            }
            if (onUndoCheckIn != null) {
                IconButton(onClick = onUndoCheckIn) {
                    Icon(Icons.Default.HowToReg, contentDescription = "Undo check-in",
                        tint = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            }
            if (onCheckOut != null) {
                IconButton(onClick = onCheckOut) {
                    Icon(Icons.Default.Logout, contentDescription = "Check out",
                        tint = MaterialTheme.colorScheme.primary)
                }
            }
            if (onRoomReady != null) {
                IconButton(onClick = onRoomReady) {
                    Icon(Icons.Default.CheckCircle, contentDescription = "Room ready",
                        tint = MaterialTheme.colorScheme.primary)
                }
            }
            if (onEdit != null) {
                IconButton(onClick = onEdit) {
                    Icon(
                        Icons.Default.Edit,
                        contentDescription = "Edit",
                        tint = MaterialTheme.colorScheme.primary
                    )
                }
            }
        }
    )
}
