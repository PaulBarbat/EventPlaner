package com.example.eventplanner

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import com.example.eventplanner.ui.theme.EventPlannerTheme
import com.example.eventplanner.ui.screens.MainScreen
import com.example.eventplanner.viewmodel.EventDateViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            EventPlannerTheme {
                MainScreen()
            }
        }
    }
}