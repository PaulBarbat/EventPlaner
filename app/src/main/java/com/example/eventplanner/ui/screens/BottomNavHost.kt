package com.example.eventplanner.ui.screens

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.eventplanner.ui.elements.BottomNavItem
import com.example.eventplanner.viewmodel.EventDateViewModel

@Composable
fun BottomNavHost(navController: NavHostController, padding: PaddingValues, viewModel: EventDateViewModel) {
    NavHost(
        navController = navController,
        startDestination = BottomNavItem.Home.route,
        modifier = Modifier.padding(padding)
    ) {
        composable(BottomNavItem.Home.route) { HomeScreen() }
        composable(BottomNavItem.Settings.route) { SettingsScreen(viewModel) }
        composable(BottomNavItem.AddBooking.route) { AddBookingScreen(viewModel) }
        composable(BottomNavItem.Bookings.route) { BookingsScreen(viewModel) }
    }
}
