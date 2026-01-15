package com.example.eventplanner.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.eventplanner.viewmodel.EventDateViewModel
import java.time.LocalDate

@Composable
fun CalendarScreen(viewModel: EventDateViewModel){
    val selectedDate by viewModel.selectedDate.collectAsState()
    val bookings by viewModel.bookings.collectAsState()

    var isDateSelected by remember { mutableStateOf(false)}
    val today = LocalDate.now()
    var currentMonth by remember { mutableStateOf(today.withDayOfMonth(1)) }
    val bookedDates = bookings.mapNotNull { runCatching {LocalDate.parse(it.date)}.getOrNull()}

    val firstDay = currentMonth.dayOfWeek.value %7
    val daysInMonth = currentMonth.lengthOfMonth()
    val days = (1..daysInMonth).map { currentMonth.withDayOfMonth(it)}

    LaunchedEffect(Unit) {
        viewModel.loadBookings()
    }

    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Row(
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier.fillMaxWidth().padding(8.dp)
        ) {
            Button(onClick = { currentMonth = currentMonth.minusMonths(1)}) { Text("<")}
            Text("${currentMonth.month.name} ${currentMonth.year}")
            Button(onClick = { currentMonth = currentMonth.plusMonths(1)}) { Text(">")}
        }
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            listOf("Mon", "Tue", "Wed", "Thu", "Fri", "Sat", "Sun").forEach{
                Text(it, style = MaterialTheme.typography.bodyMedium)
            }
        }

        LazyVerticalGrid(columns = GridCells.Fixed(7)){
            //offset for first day
            items(firstDay) { Spacer(Modifier.size(40.dp))}
            //days
            items(days) { date ->
                val isBooked = bookedDates.count { it == date }
                val isSelected = date == selectedDate
                val bgColor = when{
                    isSelected -> viewModel.theme.collectAsState().value.primary
                    isBooked > 0-> viewModel.theme.collectAsState().value.secondary
                    else -> viewModel.theme.collectAsState().value.tertiary
                }
                Box(
                    modifier = Modifier
                        .padding(4.dp)
                        .size(40.dp)
                        .background(bgColor, RoundedCornerShape(8.dp))
                        .clickable {
                            viewModel.updateDate(date)
                            isDateSelected=true},
                    contentAlignment = Alignment.Center
                ) {
                    Text(date.dayOfMonth.toString())
                }
            }
        }
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ){
            Button(onClick = { viewModel.updateFormState(2) },
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .padding(top = 16.dp)) {
                Text("Skip")
            }
            Button(onClick = {
                viewModel.updateFormState(2)
            },
                enabled = isDateSelected,
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .padding(top = 16.dp)) {
                Text("Ok")
            }
        }
    }
}