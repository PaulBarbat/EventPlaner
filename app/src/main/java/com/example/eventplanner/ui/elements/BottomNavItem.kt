package com.example.eventplanner.ui.elements

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddCircle
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Settings
import androidx.compose.ui.graphics.vector.ImageVector

sealed class BottomNavItem(
    val route: String,
    val label: String,
    val icon: ImageVector
) {
    object Settings : BottomNavItem("settings", "Settings", Icons.Default.Settings)
    object AddBooking : BottomNavItem("add", "Add", Icons.Default.AddCircle)
    object Bookings : BottomNavItem("bookings", "Bookings", Icons.Default.DateRange)
}

val bottomNavItems = listOf(
    BottomNavItem.Settings,
    BottomNavItem.AddBooking,
    BottomNavItem.Bookings
)