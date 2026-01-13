package com.example.eventplanner.ui.elements

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.eventplanner.viewmodel.EventDateViewModel
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.MarkerState
import com.google.maps.android.compose.rememberCameraPositionState


@SuppressLint("DefaultLocale")
@Composable
fun MapRoutePickerElement(
    viewModel: EventDateViewModel,
    apiKey: String
) {
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
        AutocompleteTextFieldElement(
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
                viewModel.fetchRoute(startPoint, endPoint!!, apiKey)
            }
        }

        routeDistance?.let {
            Text(
                String.format("Distance: %.1f km", it / 1000.0),
                modifier = Modifier.padding(16.dp)
            )
        }
        Button(onClick = { viewModel.updateFormState(4) }) {
            Text("Continue")
        }
    }
}