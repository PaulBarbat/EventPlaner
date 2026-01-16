package com.example.eventplanner.ui.screens

import android.util.Log
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import com.example.eventplanner.viewmodel.EventDateViewModel
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.PopupProperties
import com.example.eventplanner.ui.theme.EventPlannerTheme
import com.example.eventplanner.ui.theme.UITheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    viewModel: EventDateViewModel){
    Log.d("Settings", "${viewModel.distance.collectAsState().value}")
    var expanded by remember { mutableStateOf(false) }
    var selectedLabel by remember { mutableStateOf("Select Theme") }

    val options : Map<String, UITheme> = mapOf(
        Pair("Olive Light", UITheme.OLIVE_LIGHT),
        Pair("Olive Dark", UITheme.OLIVE_DARK),
        Pair("Blue Light", UITheme.BLUE_LIGHT),
        Pair("Blue Dark", UITheme.BLUE_DARK),
        Pair("Mint Light", UITheme.MINT_LIGHT),
        Pair("Mint Dark", UITheme.MINT_DARK)
    )
    Column(
        modifier = Modifier.padding(16.dp)
            .fillMaxWidth(),
        verticalArrangement = Arrangement.SpaceEvenly
    ) {
        ExposedDropdownMenuBox(
            expanded = expanded,
            onExpandedChange = { expanded = !expanded }
        ) {
            TextField(
                value = selectedLabel,
                onValueChange = {},
                readOnly = true,
                label = { Text("App Theme") },
                trailingIcon = {
                    ExposedDropdownMenuDefaults.TrailingIcon(expanded)
                },
                modifier = Modifier
                    .menuAnchor()
                    .fillMaxWidth()
            )

            ExposedDropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false }
            ) {
                options.forEach { (label, theme) ->
                    DropdownMenuItem(
                        text = { Text(label) },
                        onClick = {
                            selectedLabel = label
                            viewModel.changeTheme(theme)
                            expanded = false
                        }
                    )
                }
            }
        }
    }
}