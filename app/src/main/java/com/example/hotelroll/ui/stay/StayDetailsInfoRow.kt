package com.example.hotelroll.ui.stay

import androidx.compose.runtime.Composable
import androidx.compose.foundation.layout.Row
import androidx.compose.material3.Text
import androidx.compose.material3.MaterialTheme
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.material3.TextField
import androidx.compose.foundation.layout.padding
import androidx.compose.ui.unit.dp


@Composable
fun StayDetailsInfoRow(
    label: String,
    value: String
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodyLarge
        )
        Text(
            text = value,
            style = MaterialTheme.typography.bodyLarge,
            fontWeight = FontWeight.Bold
        )
    }
}

@Composable
fun StayNotesSection(notes: String?) {
    TextField(
        value = notes ?: "",
        onValueChange = {},          // no-op (read-only)
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        label = { Text("Notes") },
        readOnly = true,
        singleLine = false,
        minLines = 3
    )
}

