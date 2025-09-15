package com.example.eventplanner.ui.screens

import android.app.DatePickerDialog
import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.PopupProperties
import com.example.eventplanner.R
import com.example.eventplanner.viewmodel.EventDateViewModel
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.util.Calendar

// --- AutocompleteTextField ---
@Composable
fun AutocompleteTextField(
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

// --- MapRoutePicker ---
@Composable
fun MapRoutePicker(
    viewModel: EventDateViewModel,
    apiKey: String
) {
    val scope = rememberCoroutineScope()
    val brasovStart = LatLng(45.642680, 25.617590)
    var startPoint by remember { mutableStateOf(brasovStart) }
    var endPoint by remember { mutableStateOf<LatLng?>(null) }

    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(
            LatLng(45.9432, 24.9668), // Romania center
            6f
        )
    }

    val suggestions by viewModel.suggestions.collectAsState()
    val routeDistance by viewModel.distance.collectAsState()

    Column(modifier = Modifier.fillMaxSize()) {
        AutocompleteTextField(
            query = "",
            onQueryChange = { query ->
                if (query.length > 2) {
                    viewModel.searchPlaces(query)
                }
            },
            suggestions = suggestions,
            onSuggestionClick = { suggestion ->
                endPoint = suggestion.second
            }
        )

        GoogleMap(
            modifier = Modifier.weight(1f),
            cameraPositionState = cameraPositionState,
            onMapClick = { latLng ->
                when {
                    endPoint == null -> endPoint = latLng
                    else -> {
                        startPoint = latLng
                        endPoint = null
                        viewModel.fetchRoute(startPoint, startPoint, apiKey) // reset
                    }
                }
            }
        ) {
            Marker(state = MarkerState(position = brasovStart), title = "Start")

            LaunchedEffect(endPoint) {
                endPoint?.let {
                    cameraPositionState.animate(
                        update = CameraUpdateFactory.newLatLngZoom(it, 14f),
                        durationMs = 1000
                    )
                }
            }

            endPoint?.let {
                Marker(state = MarkerState(position = it), title = "End")
            }
        }

        Button(
            onClick = {
                if (endPoint != null) {
                    viewModel.fetchRoute(startPoint, endPoint!!, apiKey)
                }
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Calculate Route Distance")
        }

        routeDistance?.let {
            Text(
                String.format("Distance: %.1f km", it / 1000.0),
                modifier = Modifier.padding(16.dp)
            )
        }
    }
}


// --- NumberDropdown ---
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NumberDropdown(
    selectedNumber: Int,
    onNumberSelected: (Int) -> Unit,
    numberList: List<Int> = listOf(2, 4, 6, 8, 10, 12)
) {
    var expanded by remember { mutableStateOf(false) }

    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { expanded = !expanded },
        modifier = Modifier.fillMaxWidth()
    ) {
        TextField(
            value = selectedNumber.toString(),
            onValueChange = { },
            readOnly = true,
            label = { Text("Numar de Ore") },
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
            modifier = Modifier.menuAnchor()
        )

        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            numberList.forEach { number ->
                DropdownMenuItem(
                    text = { Text(number.toString()) },
                    onClick = {
                        onNumberSelected(number)
                        expanded = false
                    }
                )
            }
        }
    }
}

// --- Main EventDateScreen ---
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EventDateScreen(viewModel: EventDateViewModel) {
    val selectedDate by viewModel.selectedDate.collectAsState()
    val selectedNumber by viewModel.selectedNumber.collectAsState()
    val selectedHours by viewModel.selectedHours.collectAsState()
    var showMapPicker by remember { mutableStateOf(false) }

    val context = LocalContext.current
    val calendar = Calendar.getInstance()
    val datePickerDialog = DatePickerDialog(
        context,
        { _, year, month, dayOfMonth ->
            viewModel.updateDate(LocalDate.of(year, month + 1, dayOfMonth))
        },
        calendar.get(Calendar.YEAR),
        calendar.get(Calendar.MONTH),
        calendar.get(Calendar.DAY_OF_MONTH)
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.primary),
        contentAlignment = Alignment.TopCenter
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.Top,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                painter = painterResource(id = R.drawable.icon_olive),
                contentDescription = null,
                tint = androidx.compose.ui.graphics.Color.Unspecified,
                modifier = Modifier
                    .size(300.dp)
                    .padding(bottom = 16.dp),
            )
            Card(
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.secondary
                ),
                modifier = Modifier
                    .fillMaxWidth(0.9f)
                    .wrapContentHeight(),
                elevation = CardDefaults.cardElevation(8.dp)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Button(onClick = { datePickerDialog.show() }) {
                        Text("Alege Data")
                    }

                    OutlinedTextField(
                        value = selectedNumber.toString(),
                        onValueChange = { input ->
                            val number = input.toIntOrNull()
                            if (number != null && number in 1..1000) {
                                viewModel.updateNumber(number)
                            }
                        },
                        label = { Text("Numar de persoane") },
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier.fillMaxWidth()
                    )

                    NumberDropdown(
                        selectedNumber = selectedNumber,
                        onNumberSelected = { viewModel.updateNumber(it) },
                        numberList = listOf(4, 6, 8)
                    )

                    Button(onClick = { showMapPicker = true }) {
                        Text("Pick Route")
                    }

                    if (showMapPicker) {
                        MapRoutePicker(viewModel, apiKey = "YOUR_API_KEY")
                    }

                    Text("Data Evenimentului: $selectedDate")
                    Text("Numar de persoane: $selectedNumber")
                    Text("Ore: $selectedHours")
                }
            }
        }
    }
}
