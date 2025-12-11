package com.example.eventplanner.ui.screens

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.animation.with
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.Arrangement
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
    val expanded = (formState>=2)
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
                        0 -> EventDataScreen (
                            viewModel = viewModel
                        )
                        1 -> LocationScreen (
                            viewModel = viewModel
                        )
                        2 -> ServiceListScreen (
                            viewModel = viewModel
                        )
                        3 -> ConfirmationScreen (
                            viewModel = viewModel
                        )
                    }

                }
            }
        }
    }
}
