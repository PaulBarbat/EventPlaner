package com.example.eventplanner.ui.screens

import com.example.eventplanner.R
import android.app.DatePickerDialog
import android.content.res.Configuration
import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.PopupProperties
import com.example.eventplanner.ui.theme.EventPlannerTheme
import com.example.eventplanner.viewmodel.EventDateViewModel
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.CameraPositionState
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.MarkerState
import com.google.maps.android.compose.Polyline
import com.google.maps.android.compose.rememberCameraPositionState
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Body
import retrofit2.http.Header
import retrofit2.http.POST
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.http.GET
import retrofit2.http.Headers
import retrofit2.http.Query
import java.time.LocalDate
import java.util.*

@Preview(showBackground = true, name = "Light Mode")
@Preview(showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_YES, name = "Dark Mode")
@Composable
fun FormPreview(){
    val dummyViewModel = EventDateViewModel()

    EventPlannerTheme {
        EventDateScreen(viewModel = dummyViewModel)
    }
}

//Open Route service
data class ORSResponse(
    val routes: List<Route>
)

data class Route(
    val summary: Summary,
    val geometry: String
)

data class Summary(
    val distance: Double, // meters
    val duration: Double  // seconds
)

data class ORSRequest(
    val coordinates: List<List<Double>>
)
interface ORSApi {
    @Headers("Content-Type: application/json")
    @POST("v2/directions/driving-car")
    suspend fun getRoute(
        @Body body: ORSRequest,
        @Header("Authorization") apiKey: String
    ): ORSResponse
}
val logging = HttpLoggingInterceptor().apply { level = HttpLoggingInterceptor.Level.BODY }
val client = OkHttpClient.Builder()
    .addInterceptor(logging)
    .build()
val retrofit = Retrofit.Builder()
    .baseUrl("https://api.openrouteservice.org/")
    .client(client)
    .addConverterFactory(GsonConverterFactory.create())
    .build()

val orsApi = retrofit.create(ORSApi::class.java)

//Photon
data class PhotonFeature(
    val geometry: Geometry,
    val properties: Properties
)

data class Geometry(val coordinates: List<Double>)
data class Properties(val name: String? = null, val city: String? = null)
data class PhotonResponse(val features: List<PhotonFeature>)

interface PhotonApi {
    @GET("api/")
    suspend fun search(
        @Query("q") query: String,
        @Query("limit") limit: Int = 5,
        @Query("lang") lang: String = "en",
    ): PhotonResponse
}

val photonRetrofit = Retrofit.Builder()
    .baseUrl("https://photon.komoot.io/")
    .client(client)
    .addConverterFactory(GsonConverterFactory.create())
    .build()

val photonApi = photonRetrofit.create(PhotonApi::class.java)
//END Photon

//Autocomplete

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
        // Text field with a FocusRequester tied to it
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

        // keep keyboard / focus on the TextField when the menu opens
        LaunchedEffect(expanded) {
            if (expanded) {
                // request focus back to the TextField
                focusRequester.requestFocus()
            }
        }

        // Dropdown anchored under the TextField; make the popup non-focusable
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
                        // remove keyboard/focus once a suggestion is chosen (optional)
                        focusManager.clearFocus()
                    }
                )
            }
        }
    }
}
//END Autocomplete
@Composable
fun MapRoutePicker(
    viewModel: EventDateViewModel,
    apiKey: String
) {
    val scope = rememberCoroutineScope()
    val brasovStart = LatLng(45.642680, 25.617590)
    var startPoint by remember { mutableStateOf(brasovStart) }
    var endPoint by remember { mutableStateOf<LatLng?>(null) }
    var routeDistance by remember { mutableStateOf<Double?>(null) }
    var query by remember { mutableStateOf("") }
    var suggestions by remember { mutableStateOf<List<Pair<String, LatLng>>>(emptyList())}
    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(
            LatLng(45.9432, 24.9668), // Center of Romania
            6f //Zoom Level
        )
    }

    Column(modifier = Modifier.fillMaxSize()) {
        AutocompleteTextField(
            query = query,
            onQueryChange = { newValue ->
                query = newValue
                if (query.length > 2) {
                    scope.launch {
                        val response = photonApi.search(query)
                        suggestions = response.features.map { f ->
                            val coords = f.geometry.coordinates
                            val name = f.properties.name ?: "Unknown"
                            Pair(name, LatLng(coords[1], coords[0]))
                        }
                    }
                }
            },
            suggestions = suggestions,
            onSuggestionClick = { suggestion ->
                endPoint = suggestion.second
                query = suggestion.first
            }
        )
        GoogleMap(
            modifier = Modifier.weight(1f),
            cameraPositionState = cameraPositionState,
            onMapClick = { latLng ->
                when {
                    endPoint ==null -> endPoint = latLng
                    else -> {
                        startPoint = latLng
                        endPoint = null
                        routeDistance = null
                    }
                }
            }
        ){
            Marker(
                state = MarkerState(position = brasovStart),
                title = "Start"
            )
            LaunchedEffect(endPoint) {
                endPoint?.let {
                    cameraPositionState.animate(
                        update = CameraUpdateFactory.newLatLngZoom(it, 14f),
                        durationMs = 1000
                    )
                }
            }
            endPoint?.let {
                Marker(
                    state = MarkerState(position = it),
                    title = "End"
                )
            }
        }

        Button(
            onClick = {
                if(startPoint != null && endPoint != null) {
                    val coordinates = listOf(
                        listOf(startPoint!!.longitude, startPoint!!.latitude),
                        listOf(endPoint!!.longitude, endPoint!!.latitude)
                    )
                    val body = ORSRequest(coordinates)

                    CoroutineScope(Dispatchers.IO).launch {
                        try {
                            val response = orsApi.getRoute(body, apiKey)
                            val distanceMeters = response.routes.first().summary.distance
                            routeDistance = distanceMeters

                            viewModel.updateDistance(distanceMeters)
                        } catch (e:Exception){
                            Log.e("ORS", "Failed to fetch route",e)
                        }
                    }
                }
            },
            modifier = Modifier.fillMaxWidth()
        ){
            Text("Calculate Route Distance")
        }

        routeDistance?.let {
            val distance = it/1000.0
            Text(String.format("Distance: %.1f km", distance),
                 modifier = Modifier.padding(16.dp)
            )
        }
    }
}
//end ORS

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NumberDropdown(
    selectedNumber: Int,
    onNumberSelected: (Int) -> Unit,
    numberList: List<Int> = listOf(2, 4, 6, 8, 10, 12)
) {
    var expanded by remember {mutableStateOf(false) }

    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { expanded = !expanded},
        modifier = Modifier.fillMaxWidth()
    ){
        TextField(
            value = selectedNumber.toString(),
            onValueChange = { },
            readOnly = true,
            label = {Text("Numar de Ore")},
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
            modifier = Modifier.menuAnchor()
        )

        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = {expanded = false}
        ){
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EventDateScreen(viewModel: EventDateViewModel) {
    val selectedDate by viewModel.selectedDate.collectAsState()
    val selectedNumber by viewModel.selectedNumber.collectAsState()
    val selectedHours by viewModel.selectedHours.collectAsState()
    var showMapPicker by remember { mutableStateOf(false) }

    val context = LocalContext.current

    // For date picker
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
            .background(Color(0xFF676937)),
        contentAlignment = Alignment.TopCenter
    ){
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.Top,
            horizontalAlignment = Alignment.CenterHorizontally
        ){
            Icon(
                painter = painterResource(id = R.drawable.icon_olive),
                contentDescription = null,
                tint = Color.Unspecified,
                modifier = Modifier
                    .size(300.dp)
                    .padding(bottom = 16.dp),
            )
            Card(
                colors = CardDefaults.cardColors(
                    containerColor = Color(0xFF9EC156)
                ),
                modifier = Modifier
                    .fillMaxWidth(0.9f)
                    .wrapContentHeight(),
                elevation = CardDefaults.cardElevation(8.dp)
            ){
                Column(
                    modifier = Modifier
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Button(onClick = { datePickerDialog.show() }) {
                        Text("Alege Data")
                    }

                    OutlinedTextField(
                        value = selectedNumber.toString(),
                        onValueChange = { input ->
                            val number = input.toIntOrNull()
                            if(number != null && number in 1..1000){
                                viewModel.updateNumber(number)
                            }
                        },
                        label = { Text("Numar de persoane")},
                        singleLine = true,
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
                        MapRoutePicker(viewModel, apiKey = "eyJvcmciOiI1YjNjZTM1OTc4NTExMTAwMDFjZjYyNDgiLCJpZCI6Ijk2OWE3ODQyOTRjMTQwMzBiYzk3NjRhNTIzY2Q1ZDEyIiwiaCI6Im11cm11cjY0In0=")
                    }
                    Text("Data Evenimentului: $selectedDate")
                    Text("Numar de persoane: $selectedNumber")
                    Text("Ore: $selectedHours")
                }
            }
        }
    }
}
