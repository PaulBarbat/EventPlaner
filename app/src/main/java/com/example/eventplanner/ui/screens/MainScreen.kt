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
fun MainScreen(
    viewModel: EventDateViewModel,
    onViewBookings: () -> Unit) {
    val formState by viewModel.formState.collectAsState()
    val expanded = (formState>=3)
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
                        0 -> {
                            Column(
                                modifier = Modifier.padding(16.dp),
                                verticalArrangement = Arrangement.spacedBy(16.dp)
                            ) {
                                Text(text = "Welcome to THE TUK")
                                Row(modifier = Modifier.padding(6.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.SpaceBetween) {
                                    Button(onClick = { viewModel.updateFormState(1) }) {//TODO change when we have the next screen
                                        Text("Book Event")
                                    }
                                    Button(onClick = { onViewBookings() }) {
                                        Text("Bookings")
                                    }
                                }

                            }
                        }
                        1 -> EventDataScreen (
                            viewModel = viewModel,
                            viewBookings = onViewBookings
                        )
                        2 -> LocationScreen (
                            viewModel = viewModel
                        )
                        3 -> ServiceListScreen (
                            viewModel = viewModel
                        )
                        4 -> ConfirmationScreen (
                            viewModel = viewModel,
                            viewBookings = onViewBookings
                        )
                    }

                }
            }
        }
    }
}
