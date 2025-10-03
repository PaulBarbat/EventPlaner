package com.example.eventplanner.ui

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp

@Composable
fun SelectableImageTile(
    name: String,
    resId: Int,
    selected: Boolean,
    enabled: Boolean,
    onClick: () -> Unit,
    modifier: Modifier= Modifier
) {
    val alpha = if( enabled) 1f else 0.35f
    Card(
        modifier = modifier
            .clip(RoundedCornerShape(16.dp))
            .graphicsLayer(alpha = alpha)
            .clickable(enabled = enabled, onClick = onClick),
        border = if (selected) BorderStroke(3.dp, Color(0xFF3B7A00)) else null
    ) {
        Box{
            Image(
                painter = painterResource(resId),
                contentDescription = name,
                modifier = Modifier.fillMaxWidth().height(140.dp),
                contentScale = ContentScale.Crop
            )
            if(selected) {
                Box(Modifier.matchParentSize().background(Color.Black.copy(alpha = 0.2f)))
                Box(
                    Modifier.align(Alignment.TopEnd).padding(8.dp).size(24.dp)
                        .background(Color.White, RoundedCornerShape(12.dp)),
                    contentAlignment = Alignment.Center
                ) { Icon(Icons.Default.Check, contentDescription = null) }
            }
            if (!enabled) {
                Box(Modifier.matchParentSize().background(Color.Gray.copy(alpha=0.12f)))
            }
        }
        Text(name, modifier = Modifier.padding(8.dp), maxLines = 1)
    }
}