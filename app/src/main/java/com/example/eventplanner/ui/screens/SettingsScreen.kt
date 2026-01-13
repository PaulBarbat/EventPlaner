package com.example.eventplanner.ui.screens

import android.util.Log
import androidx.compose.runtime.Composable
import com.example.eventplanner.viewmodel.EventDateViewModel
import androidx.compose.runtime.collectAsState

@Composable
fun SettingsScreen(
    viewModel: EventDateViewModel){
    Log.d("Settings", "${viewModel.distance.collectAsState().value}")
}