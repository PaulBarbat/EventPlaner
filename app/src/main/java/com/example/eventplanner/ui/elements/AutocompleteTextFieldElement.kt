package com.example.eventplanner.ui.elements

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.window.PopupProperties
import com.google.android.gms.maps.model.LatLng


@Composable
fun AutocompleteTextFieldElement(
    query: String,
    onQueryChange: (String) -> Unit,
    suggestions: List<Pair<String, LatLng>>,
    onSuggestionClick: (Pair<String, LatLng>) -> Unit
) {
    val focusRequester = remember { FocusRequester() }
    var expanded by remember { mutableStateOf(false) }
    val focusManager = LocalFocusManager.current

    Box(modifier = Modifier.fillMaxWidth()) {
        TextField(
            value = query,
            onValueChange = {
                onQueryChange(it)
                expanded = it.length > 2 && suggestions.isNotEmpty()
            },
            label = { Text("Search destination") },
            modifier = Modifier
                .fillMaxWidth()
                .focusRequester(focusRequester),
            singleLine = true
        )

        LaunchedEffect(expanded) {
            if (expanded) {
                // request focus back to the TextField
                focusRequester.requestFocus()
            }
        }

        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false },
            modifier = Modifier.fillMaxWidth(),
            properties = PopupProperties(focusable = false)
        ) {
            suggestions.forEach { suggestion ->
                DropdownMenuItem(
                    text = { Text(suggestion.first) },
                    onClick = {
                        onSuggestionClick(suggestion)
                        expanded = false
                        focusManager.clearFocus()
                    }
                )
            }
        }
    }
}