package com.example.unitconverter.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Divider
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.HorizontalDivider
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.example.unitconverter.data.UnitItem

@Composable
fun UnitPickerSheet(
    title: String,
    units: List<UnitItem>,
    onPick: (UnitItem) -> Unit
) {
    var query by remember { mutableStateOf("") }
    val filtered = remember(query, units) {
        if (query.isBlank()) units
        else units.filter {
            it.name.contains(query, true) || it.symbol.contains(query, true)
        }
    }

    Column(Modifier.fillMaxWidth().padding(16.dp)) {
        Text(text = title, style = MaterialTheme.typography.headlineMedium)
        Spacer(Modifier.height(12.dp))
        OutlinedTextField(
            value = query,
            onValueChange = { query = it },
            singleLine = true,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text),
            label = { Text("Search unit…") },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(Modifier.height(12.dp))
        LazyColumn {
            items(filtered) { u ->
                ListItem(
                    headlineContent = { Text(u.name) },
                    supportingContent = { Text(u.symbol) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { onPick(u) }
                        .padding(vertical = 8.dp)
                )
                HorizontalDivider()
            }
        }
    }
}
