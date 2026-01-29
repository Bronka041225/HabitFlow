package com.example.habitflow.ui.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.example.habitflow.R
import com.example.habitflow.data.entity.HabitRecordEntity
import java.time.LocalDate
import java.time.format.DateTimeFormatter

@Composable
fun EditRecordDialog(
    record: HabitRecordEntity,
    onDismiss: () -> Unit,
    onSave: (Int, String?) -> Unit,
    onDelete: () -> Unit
) {
    var count by remember { mutableStateOf(record.count.toString()) }
    var note by remember { mutableStateOf(record.note ?: "") }
    val dateStr = LocalDate.ofEpochDay(record.date).format(DateTimeFormatter.ofPattern("MMM d, yyyy"))

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Edit Record ($dateStr)") },
        text = {
            Column {
                Text(
                    text = "Count",
                    style = MaterialTheme.typography.labelMedium
                )
                OutlinedTextField(
                    value = count,
                    onValueChange = { count = it },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                
                Spacer(modifier = Modifier.height(16.dp))
                
                Text(
                    text = "Note",
                    style = MaterialTheme.typography.labelMedium
                )
                OutlinedTextField(
                    value = note,
                    onValueChange = { note = it },
                    modifier = Modifier.fillMaxWidth(),
                    maxLines = 3
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val countInt = count.toIntOrNull()
                    if (countInt != null && countInt > 0) {
                        onSave(countInt, note.ifBlank { null })
                    }
                }
            ) {
                Text("Save")
            }
        },
        dismissButton = {
            Row {
                 TextButton(
                    onClick = onDelete,
                    colors = ButtonDefaults.textButtonColors(contentColor = MaterialTheme.colorScheme.error)
                ) {
                    Text("Delete Record")
                }
                TextButton(onClick = onDismiss) {
                    Text(stringResource(R.string.cancel))
                }
            }
        }
    )
}
