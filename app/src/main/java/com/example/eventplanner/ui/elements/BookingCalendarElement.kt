package com.example.eventplanner.ui.elements

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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.eventplanner.data.models.Booking
import java.time.LocalDate

@Composable
fun BookingCalendarElement(
    bookings: List<Booking>,
    selectedDate: LocalDate?,
    onDateSelected: (LocalDate) -> Unit
) {
    val today = LocalDate.now()
    var currentMonth by remember { mutableStateOf(today.withDayOfMonth(1)) }
    val bookedDates = bookings.mapNotNull { runCatching {LocalDate.parse(it.date)}.getOrNull()}

    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        //month, year abd month navigations
        Row(
           horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier.fillMaxWidth().padding(8.dp)
        ) {
            Button(onClick = { currentMonth = currentMonth.minusMonths(1)}) { Text("<")}
            Text("${currentMonth.month.name} ${currentMonth.year}")
            Button(onClick = { currentMonth = currentMonth.plusMonths(1)}) { Text(">")}
        }
        //header with days of the week
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            listOf("Mon", "Tue", "Wed", "Thu", "Fri", "Sat", "Sun").forEach{
                Text(it, style = MaterialTheme.typography.bodyMedium)
            }
        }

        //days grid
        val firstDay = currentMonth.dayOfWeek.value %7
        val daysInMonth = currentMonth.lengthOfMonth()
        val days = (1..daysInMonth).map { currentMonth.withDayOfMonth(it)}

        LazyVerticalGrid(columns = GridCells.Fixed(7)){
            //offset for first day
            items(firstDay) { Spacer(Modifier.size(40.dp))}
            //days
            items(days) { date ->
                val isBooked = bookedDates.count { it == date }
                val isSelected = date == selectedDate
                val bgColor = when{
                    isSelected -> Color(0xFF3B7A00)
                    isBooked > 0-> Color(0xFFFFC0CB)
                    else -> Color(0xFFE0E0E0)
                }
                Box(
                    modifier = Modifier
                        .padding(4.dp)
                        .size(40.dp)
                        .background(bgColor, RoundedCornerShape(8.dp))
                        .clickable { onDateSelected(date) },
                    contentAlignment = Alignment.Center
                ) {
                    Text(date.dayOfMonth.toString())
                }
            }
        }
    }
}