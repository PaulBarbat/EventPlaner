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
fun AddBookingScreen(
    viewModel: EventDateViewModel) {
    val formState by viewModel.formState.collectAsState()
    val expanded = (formState>=3)
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF676937)),
        contentAlignment = Alignment.TopCenter
    ) {
        if(!expanded) {
            Icon(
                painter = painterResource(id = R.drawable.icon_olive),
                contentDescription = null,
                tint = Color.Unspecified,
                modifier = Modifier
                    .size(300.dp)
                    .padding(bottom = 16.dp),
            )
        }
        Card(
            colors = CardDefaults.cardColors(containerColor = Color(0xFF9EC156)),
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.BottomCenter)
                .padding(
                    top = if (!expanded)200.dp else 0.dp, // optional space for icon
                    start = 0.dp,
                    end = 0.dp,
                    bottom = 0.dp
                ),
            elevation = CardDefaults.cardElevation(8.dp)
        ) {
            AnimatedContent(
                targetState = formState,
                transitionSpec = {
                    if (targetState > initialState) {
                        (slideInVertically { height -> height } + fadeIn()).togetherWith(
                            slideOutVertically { height -> -height } + fadeOut())
                    } else {
                        (slideInVertically { height -> -height } + fadeIn()).togetherWith(
                            slideOutVertically { height -> height } + fadeOut())
                    }
                },
                label = "FormTransition"
            ) { step ->
                when (step) {
                    1->CalendarScreen(
                        viewModel = viewModel
                    )
                    2 -> EventDataScreen (
                        viewModel = viewModel
                    )
                    3 -> LocationScreen (
                        viewModel = viewModel
                    )
                    4 -> ServiceListScreen (
                        viewModel = viewModel
                    )
                    5 -> ConfirmationScreen (
                        viewModel = viewModel
                    )
                }
            }
        }
    }
}
