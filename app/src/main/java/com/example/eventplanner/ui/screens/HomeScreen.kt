package com.example.eventplanner.ui.screens

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.example.eventplanner.R
import com.example.eventplanner.viewmodel.EventDateViewModel

@OptIn(ExperimentalMaterial3Api::class, ExperimentalAnimationApi::class)
@Composable
fun HomeScreen(viewModel: EventDateViewModel) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(viewModel.theme.collectAsState().value.secondary),
        contentAlignment = Alignment.Center
    ) {
        Icon(
            painter = painterResource(id = viewModel.theme.collectAsState().value.icon),
            contentDescription = null,
            tint = Color.Unspecified,
            modifier = Modifier
                .size(300.dp)
                .padding(bottom = 16.dp),
        )
    }
}
