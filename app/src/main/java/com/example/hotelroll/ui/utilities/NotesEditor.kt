package com.example.hotelroll.ui.utilities

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

@Composable
fun NotesEditor(
    notes: String,
    onNotesChanged: (String) -> Unit
) {

    Column {
        Text("Notes")

        OutlinedTextField(
            value = notes,
            onValueChange = onNotesChanged,
            modifier = Modifier.fillMaxWidth(),
            minLines = 3,
            label = { Text("Notes")}
        )

    }
}
