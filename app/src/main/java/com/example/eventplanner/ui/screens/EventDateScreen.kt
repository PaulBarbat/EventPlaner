package com.example.eventplanner.ui.screens

import com.example.eventplanner.R
import android.app.DatePickerDialog
import android.content.res.Configuration
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.eventplanner.ui.theme.EventPlannerTheme
import com.example.eventplanner.viewmodel.EventDateViewModel
import java.time.LocalDate
import java.util.*

@Preview(showBackground = true, name = "Light Mode")
@Preview(showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_YES, name = "Dark Mode")
@Composable
fun FormPreview(){
    val dummyViewModel = EventDateViewModel()

    EventPlannerTheme {
        EventDateScreen(viewModel = dummyViewModel)
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NumberDropdown(
    selectedNumber: Int,
    onNumberSelected: (Int) -> Unit,
    numberList: List<Int> = listOf(2, 4, 6, 8, 10, 12)
) {
    var expanded by remember {mutableStateOf(false) }

    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { expanded = !expanded},
        modifier = Modifier.fillMaxWidth()
    ){
        TextField(
            value = selectedNumber.toString(),
            onValueChange = { },
            readOnly = true,
            label = {Text("Numar de Ore")},
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
            modifier = Modifier.menuAnchor()
        )

        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = {expanded = false}
        ){
            numberList.forEach { number ->
                DropdownMenuItem(
                    text = { Text(number.toString()) },
                    onClick = {
                        onNumberSelected(number)
                        expanded = false
                    }
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EventDateScreen(viewModel: EventDateViewModel) {
    val selectedDate by viewModel.selectedDate.collectAsState()
    val selectedNumber by viewModel.selectedNumber.collectAsState()
    val selectedHours by viewModel.selectedHours.collectAsState()

    val context = LocalContext.current

    // For date picker
    val calendar = Calendar.getInstance()
    val datePickerDialog = DatePickerDialog(
        context,
        { _, year, month, dayOfMonth ->
            viewModel.updateDate(LocalDate.of(year, month + 1, dayOfMonth))
        },
        calendar.get(Calendar.YEAR),
        calendar.get(Calendar.MONTH),
        calendar.get(Calendar.DAY_OF_MONTH)
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF676937)),
        contentAlignment = Alignment.TopCenter
    ){
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.Top,
            horizontalAlignment = Alignment.CenterHorizontally
        ){
            Icon(
                painter = painterResource(id = R.drawable.icon_olive),
                contentDescription = null,
                tint = Color.Unspecified,
                modifier = Modifier
                    .size(300.dp)
                    .padding(bottom = 16.dp),
            )
            Card(
                colors = CardDefaults.cardColors(
                    containerColor = Color(0xFF9EC156)
                ),
                modifier = Modifier
                    .fillMaxWidth(0.9f)
                    .wrapContentHeight(),
                elevation = CardDefaults.cardElevation(8.dp)
            ){
                Column(
                    modifier = Modifier
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Button(onClick = { datePickerDialog.show() }) {
                        Text("Alege Data")
                    }

                    OutlinedTextField(
                        value = selectedNumber.toString(),
                        onValueChange = { input ->
                            val number = input.toIntOrNull()
                            if(number != null && number in 1..1000){
                                viewModel.updateNumber(number)
                            }
                        },
                        label = { Text("Numar de persoane")},
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                    NumberDropdown(
                        selectedNumber = selectedNumber,
                        onNumberSelected = { viewModel.updateNumber(it) },
                        numberList = listOf(4, 6, 8)
                    )
                    Text("Data Evenimentului: $selectedDate")
                    Text("Numar de persoane: $selectedNumber")
                    Text("Ore: $selectedHours")
                }
            }
        }
    }
}
