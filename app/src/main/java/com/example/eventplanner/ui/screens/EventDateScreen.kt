package com.example.eventplanner.ui.screens

import android.app.DatePickerDialog
import android.util.Log
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.slideOutVertically
import androidx.compose.animation.with
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ModifierLocalBeyondBoundsLayout
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
import com.example.eventplanner.BuildConfig
import com.example.eventplanner.ui.SelectableImageTile

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

// --- MapRoutePicker ---
@Composable
fun MapRoutePicker(
    viewModel: EventDateViewModel,
    apiKey: String
) {
    val scope = rememberCoroutineScope()

    val startPoint by viewModel.startPoint.collectAsState()
    val endPoint by viewModel.endPoint.collectAsState()

    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(
            LatLng(45.9432, 24.9668), // Romania center
            6f
        )
    }

    val suggestions by viewModel.suggestions.collectAsState()
    val routeDistance by viewModel.distance.collectAsState()
    var query by remember { mutableStateOf("") }

    Column(modifier = Modifier.fillMaxSize()) {
        AutocompleteTextField(
            query = query,
            onQueryChange = {
                query =it
                if (query.length > 2) {
                    viewModel.searchPlaces(query)
                }
            },
            suggestions = suggestions,
            onSuggestionClick = { suggestion ->
                viewModel.updateEndPoint(suggestion.second)
            }
        )

        GoogleMap(
            modifier = Modifier.weight(1f),
            cameraPositionState = cameraPositionState,
            onMapClick = { latLng ->
                viewModel.updateEndPoint(latLng)
            }
        ) {
            Marker(state = MarkerState(position = startPoint), title = "Start")

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
        Button(onClick = { viewModel.updateFormState(3) }) {
            Text("Continue")
        }
    }
}


// --- NumberDropdown ---
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NumberDropdown(
    selectedHours: Int,
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
            value = selectedHours.toString(),
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

//FirstForm

@Composable
fun FirstForm(viewModel: EventDateViewModel)
{
    val selectedDate by viewModel.selectedDate.collectAsState()
    val selectedNumber by viewModel.selectedNumber.collectAsState()
    val selectedHours by viewModel.selectedHours.collectAsState()

    val calendar = Calendar.getInstance()

    val context = LocalContext.current
    val datePickerDialog = DatePickerDialog(
        context,
        { _, year, month, dayOfMonth ->
            viewModel.updateDate(LocalDate.of(year, month + 1, dayOfMonth))
        },
        calendar.get(Calendar.YEAR),
        calendar.get(Calendar.MONTH),
        calendar.get(Calendar.DAY_OF_MONTH)
    )
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
            selectedHours = selectedHours,
            onNumberSelected = { viewModel.updateHours(it) },
            numberList = listOf(4, 6, 8)
        )

        Text("Data Evenimentului: $selectedDate")
        Text("Numar de persoane: $selectedNumber")
        Text("Ore: $selectedHours")

        Button(onClick = { viewModel.updateFormState(2) }) {
            Text("Continue")
        }
    }
}

//SecondForm
@Composable
fun SecondForm(viewModel: EventDateViewModel)
{
    Column(
        modifier = Modifier.padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Button(onClick = { viewModel.updateFormState(1) }) {
            Text("Back")
        }
        MapRoutePicker(viewModel, apiKey = BuildConfig.ORS_KEY)
    }
}

//Services Form
@Composable
fun ServicesForm(viewModel: EventDateViewModel)
{
    val services = viewModel.services()
    val allImages = viewModel.allImages()
    val selectedServiceId by viewModel.selectedServiceId.collectAsState()
    val selectedImage by viewModel.selectedImage.collectAsState()

    val allowed = selectedServiceId
        ?.let { viewModel.allowedImageNamesFor(it).toSet()}
        ?: emptySet()
    val outdated by viewModel.configOutdated.collectAsState()

    Column(
        modifier = Modifier.fillMaxSize()
            .padding(top = 20.dp,bottom = 10.dp),
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        Button(onClick = { viewModel.updateFormState(2) },
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 16.dp)) {
            Text("Back")
        }
        Row(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
        ){
            //Serevices
            LazyColumn (
                modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight()
                    .padding(8.dp)
            ){
                items(services, key = { it.id }) {svc ->
                    val isSelected = svc.id == selectedServiceId
                    ListItem(
                        headlineContent = { Text(svc.displayName)},
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp)
                            .clickable { viewModel.selectService(svc.id)},
                        tonalElevation = if (isSelected) 4.dp else 0.dp
                    )
                    Divider()
                }
            }
            //Images
            LazyColumn (
                modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight()
                    .padding(8.dp),
                horizontalAlignment = Alignment.End
            ){
                items(allImages, key = { it.id }) { tuktuk ->
                    val resId = viewModel.resIdOf(tuktuk.id)
                    val enabled = tuktuk.id in allowed && selectedServiceId != null
                    val selected = tuktuk.id == selectedImage

                    SelectableImageTile(
                        name = tuktuk.displayName,
                        resId = resId,
                        selected = selected,
                        enabled = enabled,
                        onClick = {viewModel.toggleImage(tuktuk.id)},
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp)
                    )
                }
            }
        }
        if (outdated){
            Text(
                "Prices may be outdated",
                color = Color.Red,
                modifier = Modifier.padding(8.dp)
            )
        }
        val canSave = selectedServiceId != null && selectedImage != null
        Button(onClick = { viewModel.saveSelectedService() },
            enabled = canSave,
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 16.dp)) {
            Text("Add Selected Service")
        }
        Button(onClick = { viewModel.updateFormState(4) },
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 16.dp)) {
            Text("Continue")
        }
    }
}

//CompletionForm

@Composable
fun CompletionForm(viewModel: EventDateViewModel)
{
    val selectedDate by viewModel.selectedDate.collectAsState()
    val selectedNumber by viewModel.selectedNumber.collectAsState()
    val selectedHours by viewModel.selectedHours.collectAsState()
    val routeDistance by viewModel.distance.collectAsState()
    val context = LocalContext.current
    val services = viewModel.selectedServices

    Column(
        modifier = Modifier.padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Button(onClick = { viewModel.updateFormState(3) }) {
            Text("Back")
        }

        Text("Data Evenimentului: $selectedDate", modifier = Modifier.padding(4.dp))
        Text("Numar de persoane: $selectedNumber", modifier = Modifier.padding(4.dp))
        Text("Ore: $selectedHours", modifier = Modifier.padding(4.dp))
        routeDistance?.let {
            Text(
                String.format("Distanta pana la eveniment:", it / 1000.0),
                modifier = Modifier.padding(14.dp)
            )
        }
        if(services.isEmpty()){
            Text("Nu ati selectat servicii!", modifier = Modifier.padding(16.dp))
        }else{
            Text("Serviciile selectate", modifier = Modifier.padding(16.dp))
            LazyColumn(modifier = Modifier.fillMaxWidth()) {
                items(services){ service->
                    Row(
                        modifier= Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp),
                        horizontalArrangement =  Arrangement.SpaceBetween
                    ) {
                        Text(service.first.displayName)
                        Text("pe ${service.second.displayName}")
                    }
                    Divider()
                }
            }
        }
    }
}
// --- Main EventDateScreen ---
@OptIn(ExperimentalMaterial3Api::class, ExperimentalAnimationApi::class)
@Composable
fun EventDateScreen(viewModel: EventDateViewModel) {
    val formState by viewModel.formState.collectAsState()
    val expanded = (formState>=3)
    val padding = if (expanded) 0.dp else 16.dp
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF676937)),
        contentAlignment = Alignment.TopCenter
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
            verticalArrangement = Arrangement.Top,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            if(!expanded) {
                Icon(
                    painter = painterResource(id = R.drawable.icon_olive),
                    contentDescription = null,
                    tint = androidx.compose.ui.graphics.Color.Unspecified,
                    modifier = Modifier
                        .size(300.dp)
                        .padding(bottom = 16.dp),
                )
            }
            Card(
                colors = CardDefaults.cardColors(
                    containerColor = Color(0xFF9EC156)
                ),
                modifier = if (expanded) {
                        Modifier
                            .fillMaxWidth()
                            .fillMaxHeight()
                    }
                    else
                    {
                        Modifier
                            .fillMaxWidth(0.9f)
                            .wrapContentHeight()
                    },
                elevation = CardDefaults.cardElevation(8.dp)
            ) {
                AnimatedContent(
                    targetState = formState,
                    transitionSpec = {
                        if (targetState > initialState) {
                            (slideInVertically { height -> height } + fadeIn()) with
                            (slideOutVertically { height -> -height } + fadeOut())
                        } else {
                            (slideInVertically { height -> -height } + fadeIn()) with
                            (slideOutVertically { height -> height } + fadeOut())
                        }
                    },
                    label = "FormTransition"
                ) { step ->
                    when (step) {
                        0 -> {
                            Column(
                                modifier = Modifier.padding(16.dp),
                                verticalArrangement = Arrangement.spacedBy(16.dp)
                            ) {
                                Text(text = "Welcome to THE TUK")
                                Button(onClick = { viewModel.updateFormState(1) }) {//TODO change when we have the next screen
                                    Text("Book Event")
                                }
                            }
                        }
                        1 -> FirstForm (
                            viewModel = viewModel
                        )
                        2 -> SecondForm (
                            viewModel = viewModel
                        )
                        3 -> ServicesForm (
                            viewModel = viewModel
                        )
                        4 -> CompletionForm (
                            viewModel = viewModel
                        )
                    }

                }
            }
        }
    }
}
