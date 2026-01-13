package com.example.eventplanner.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.DividerDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.ListItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.eventplanner.ui.SelectableImageTile
import com.example.eventplanner.viewmodel.EventDateViewModel


@Composable
fun ServiceListScreen(viewModel: EventDateViewModel)
{
    val services = viewModel.services()
    val allImages = viewModel.allImages()
    val selectedServiceId by viewModel.selectedServiceId.collectAsState()
    val selectedImage by viewModel.selectedImage.collectAsState()

    val allowed = selectedServiceId
        ?.let { viewModel.allowedImageNamesFor(it).toSet()}
        ?: emptySet()
    val outdated by viewModel.configOutdated.collectAsState()

    Column(
        modifier = Modifier.fillMaxSize()
            .padding(top = 20.dp,bottom = 10.dp),
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        Button(onClick = { viewModel.updateFormState(3) },
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 16.dp)) {
            Text("Back")
        }
        Row(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
        ){
            //Serevices
            LazyColumn (
                modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight()
                    .padding(8.dp)
            ){
                items(services, key = { it.id }) {svc ->
                    val isSelected = svc.id == selectedServiceId
                    ListItem(
                        headlineContent = { Text(svc.displayName)},
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp)
                            .clickable { viewModel.selectService(svc.id)},
                        tonalElevation = if (isSelected) 4.dp else 0.dp
                    )
                    HorizontalDivider(Modifier, DividerDefaults.Thickness, DividerDefaults.color)
                }
            }
            //Images
            LazyColumn (
                modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight()
                    .padding(8.dp),
                horizontalAlignment = Alignment.End
            ){
                items(allImages, key = { it.id }) { tuktuk ->
                    val resId = viewModel.resIdOf(tuktuk.id)
                    val enabled = tuktuk.id in allowed && selectedServiceId != null
                    val selected = tuktuk.id == selectedImage

                    SelectableImageTile(
                        name = tuktuk.displayName,
                        resId = resId,
                        selected = selected,
                        enabled = enabled,
                        onClick = {viewModel.toggleImage(tuktuk.id)},
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp)
                    )
                }
            }
        }
        if (outdated){
            Text(
                "Prices may be outdated",
                color = Color.Red,
                modifier = Modifier.padding(8.dp)
            )
        }
        val canSave = selectedServiceId != null && selectedImage != null
        Button(onClick = { viewModel.saveSelectedService() },
            enabled = canSave,
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 16.dp)) {
            Text("Add Selected Service")
        }
        Button(onClick = { viewModel.updateFormState(5) },
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 16.dp)) {
            Text("Continue")
        }
    }
}
