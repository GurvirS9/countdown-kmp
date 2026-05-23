package com.exam.countdown.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.Icon
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.exam.countdown.model.COLOR_TAGS

@Composable
fun ColorPicker(
    selectedColor: String,
    onColorSelected: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    LazyRow(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(10.dp),
        contentPadding = PaddingValues(horizontal = 4.dp),
    ) {
        items(COLOR_TAGS) { hex ->
            val color = parseColor(hex)
            val isSelected = hex == selectedColor
            val borderColor by animateColorAsState(
                if (isSelected) Color.White else Color.Transparent,
                animationSpec = tween(200), label = "border"
            )
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .clip(CircleShape)
                    .background(color)
                    .border(2.dp, borderColor, CircleShape)
                    .clickable { onColorSelected(hex) },
                contentAlignment = Alignment.Center,
            ) {
                if (isSelected) {
                    Icon(
                        Icons.Filled.Check, "Selected",
                        tint = Color.White, modifier = Modifier.size(16.dp)
                    )
                }
            }
        }
    }
}
