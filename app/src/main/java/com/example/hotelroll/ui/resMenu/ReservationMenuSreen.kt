package com.example.hotelroll.ui.resMenu

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.SwapHoriz
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.hotelroll.data.model.User


@Composable
fun ReservationMenuScreen(
    viewModel: ReservationViewModel,
    onReservationClick: (Long) -> Unit,
    onAddReservationClick: () -> Unit
) {
    val reservations by viewModel.reservations.collectAsState()
    val activeUser by viewModel.activeUser.collectAsState()
    val allUsers by viewModel.allUsers.collectAsState()

    var showSwitcherDialog by remember { mutableStateOf(false) }
    var pendingUser by remember { mutableStateOf<User?>(null) }

    Column(
        modifier = Modifier
            .fillMaxHeight()
            .width(280.dp)
            .padding(16.dp)
    ) {
        // Active user indicator
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clickable { showSwitcherDialog = true }
                .padding(vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Icon(
                imageVector = Icons.Default.Person,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary
            )
            Text(
                text = activeUser?.name ?: "...",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.weight(1f)
            )
            Icon(
                imageVector = Icons.Default.SwapHoriz,
                contentDescription = "Switch user",
                tint = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        HorizontalDivider(modifier = Modifier.padding(bottom = 12.dp))

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Reservations (${reservations.size})",
                style = MaterialTheme.typography.titleLarge,
                modifier = Modifier.weight(1f)
            )
            IconButton(onClick = onAddReservationClick) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "Add reservation"
                )
            }
        }

        LazyColumn {
            items(reservations) { item ->
                Text(
                    text = item.resName,
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { onReservationClick(item.id) }
                        .padding(vertical = 12.dp)
                )
            }
        }
    }

    // User switcher dialog
    if (showSwitcherDialog) {
        AlertDialog(
            onDismissRequest = { showSwitcherDialog = false },
            title = { Text("Switch user") },
            text = {
                LazyColumn {
                    items(allUsers) { user ->
                        val isActive = user.id == activeUser?.id
                        Text(
                            text = if (isActive) "${user.name} ✓" else user.name,
                            style = MaterialTheme.typography.bodyLarge,
                            color = if (isActive)
                                MaterialTheme.colorScheme.primary
                            else
                                MaterialTheme.colorScheme.onSurface,
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable(enabled = !isActive) {
                                    pendingUser = user
                                    showSwitcherDialog = false
                                }
                                .padding(vertical = 12.dp)
                        )
                    }
                }
            },
            confirmButton = {},
            dismissButton = {
                TextButton(onClick = { showSwitcherDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }

    // Confirmation dialog
    pendingUser?.let { user ->
        AlertDialog(
            onDismissRequest = { pendingUser = null },
            title = { Text("Switch user?") },
            text = { Text("Switch to ${user.name}?") },
            confirmButton = {
                TextButton(onClick = {
                    viewModel.switchUser(user)
                    pendingUser = null
                }) {
                    Text("Switch")
                }
            },
            dismissButton = {
                TextButton(onClick = { pendingUser = null }) {
                    Text("Cancel")
                }
            }
        )
    }
}
