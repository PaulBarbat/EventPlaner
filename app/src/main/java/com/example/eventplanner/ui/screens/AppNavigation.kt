package com.example.eventplanner.ui.screens

import androidx.compose.runtime.Composable
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.eventplanner.viewmodel.EventDateViewModel

@Composable
fun AppNavigation() {
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = "event_form"
    ) {
        composable("event_form") {
            val viewModel: EventDateViewModel = hiltViewModel()
            MainScreen(
                viewModel = viewModel,
                onViewBookings = { navController.navigate("bookings")}
            )
        }
        composable("bookings") {
            val viewModel: EventDateViewModel = hiltViewModel()
            BookingsScreen(
                viewModel = viewModel,
                onBack = { navController.popBackStack()}
            )
        }
    }
}