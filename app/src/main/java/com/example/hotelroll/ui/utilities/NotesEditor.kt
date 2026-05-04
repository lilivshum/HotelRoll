package com.example.hotelroll.ui.utilities

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.unit.dp

@Composable
fun NotesEditor(
    notes: String,
    onNotesChanged: (String) -> Unit
) {
    val minHeight = (LocalConfiguration.current.screenHeightDp * 0.15f).dp

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
    ) {
        Text(
            text = "Notes",
            style = MaterialTheme.typography.titleMedium,
            modifier = Modifier.padding(bottom = 8.dp)
        )

        TextField(
            value = notes,
            onValueChange = onNotesChanged,
            modifier = Modifier
                .fillMaxWidth()
                .heightIn(min = minHeight),
            placeholder = { Text("Enter notes about this stay…") },
            singleLine = false,
            maxLines = 10
        )
    }
}
