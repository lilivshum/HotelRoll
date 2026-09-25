package com.example.hotelroll.ui.roll

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import com.example.hotelroll.ui.utilities.ConfirmActionDialog
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.hotelroll.HotelApplication
import com.example.hotelroll.ui.gantt.GanttScreen
import com.example.hotelroll.ui.gantt.GanttViewModel
import com.example.hotelroll.ui.gantt.GanttViewModelFactory
import com.example.hotelroll.ui.navigation.StayMode
import com.example.hotelroll.ui.roll.DateHeader

@Composable
fun RollScreen(onStayClick: (Long, String, String) -> Unit,
               app: HotelApplication = LocalContext.current.applicationContext as HotelApplication,
               onMenuClick: () -> Unit,
               onEmptyClick: (Long, String, String, StayMode, Long?) -> Unit
) {
    val viewModel: RollViewModel = viewModel(factory = RollViewModelFactory(app.repository))
    val ganttViewModel: GanttViewModel = viewModel(factory = GanttViewModelFactory(app.repository, app))
    val rollItems by viewModel.roll.collectAsState(initial = emptyList())
    val activeUser by viewModel.activeUser.collectAsState()
    val ganttMonth by ganttViewModel.month.collectAsState()

    // Scaffold is optional, but useful for padding/top bars
    Scaffold { paddingValues ->
        val date = viewModel.date.collectAsState().value
        Column(modifier = Modifier.padding(paddingValues)) {
            // Date navigation header

            DateHeader(
                date = date,
                onPrevious = viewModel::prevDay,
                onNext = viewModel::nextDay,
                onMenuClick = onMenuClick,
                viewModel = viewModel,
                isGanttView = viewModel.showGantt,
                onToggleView = viewModel::toggleGantt,
                ganttMonth = ganttMonth,
                onPrevMonth = ganttViewModel::prevMonth,
                onNextMonth = ganttViewModel::nextMonth
            )

            if (viewModel.showGantt) {
                GanttScreen(viewModel = ganttViewModel)
            } else {
                // Move mode cancel banner
                if (viewModel.selectedItem != null) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Moving room ${viewModel.selectedItem!!.roomNumber} — tap a destination",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.weight(1f)
                        )
                        IconButton(onClick = viewModel::clearSelection) {
                            Icon(
                                imageVector = Icons.Default.Close,
                                contentDescription = "Cancel move",
                                tint = MaterialTheme.colorScheme.primary
                            )
                        }
                    }
                } else {
                    Spacer(modifier = Modifier.height(8.dp))
                }

                rollItems.forEach { println(it) }

                // Table with horizontal scroll + vertical scrolling inside
                RollTable(rollItems = rollItems, onStayClick, onEmptyClick, date, viewModel)
            }
        }
    }

    val pendingAction = viewModel.pendingActionItem
    val pendingActionDate = viewModel.pendingActionDate
    if (pendingAction != null && pendingActionDate != null) {
        AlertDialog(
            onDismissRequest = { viewModel.dismissActionPicker() },
            title = { Text("Move stay in Room ${pendingAction.roomNumber}") },
            text = { Text("Move the entire stay, or split it here and move from ${pendingActionDate.monthValue}/${pendingActionDate.dayOfMonth} onward?") },
            confirmButton = {
                TextButton(onClick = { viewModel.confirmAction(pendingActionDate) }) {
                    Text("Split from ${pendingActionDate.monthValue}/${pendingActionDate.dayOfMonth}")
                }
            },
            dismissButton = {
                TextButton(onClick = { viewModel.confirmAction(null) }) {
                    Text("Move entire stay")
                }
            }
        )
    }

    val pendingBlock = viewModel.pendingBlockItem
    if (pendingBlock != null) {
        val isCurrentlyBlocked = pendingBlock.roomStatus == com.example.hotelroll.data.model.RoomStatus.BLOCKED
        AlertDialog(
            onDismissRequest = { viewModel.dismissBlockDialog() },
            title = { Text(if (isCurrentlyBlocked) "Unblock Room ${pendingBlock.roomNumber}?" else "Block Room ${pendingBlock.roomNumber}?") },
            text = { Text(if (isCurrentlyBlocked) "This room will be marked as available again." else "This room will be marked as blocked. It will still appear in the roll but cannot receive move-drop assignments.") },
            confirmButton = {
                TextButton(onClick = { viewModel.confirmBlockToggle() }) {
                    Text(if (isCurrentlyBlocked) "Unblock" else "Block")
                }
            },
            dismissButton = {
                TextButton(onClick = { viewModel.dismissBlockDialog() }) { Text("Cancel") }
            }
        )
    }

    val pendingTarget = viewModel.pendingMoveTarget
    val source = viewModel.selectedItem
    if (pendingTarget != null && source != null) {
        val split = viewModel.splitDate
        val isSplit = split != null && split != source.checkInDate
        val dialogBody = if (isSplit)
            "Stay from Room ${source.roomNumber} will be split on ${split!!.monthValue}/${split.dayOfMonth}. From that date it will continue in Room ${pendingTarget.roomNumber}."
        else
            "Stay from Room ${source.roomNumber} will be moved to Room ${pendingTarget.roomNumber}."
        ConfirmActionDialog(
            title = if (isSplit) "Split stay?" else "Move stay?",
            body = dialogBody,
            activeUserName = activeUser?.name ?: "Unknown",
            onConfirm = {
                viewModel.moveStayToRoom(source, pendingTarget.roomId) {}
                viewModel.clearSelection()
            },
            onDismiss = {
                viewModel.clearPendingMove()
            }
        )
    }
}