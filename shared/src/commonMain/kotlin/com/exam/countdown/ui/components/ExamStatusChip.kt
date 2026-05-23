package com.exam.countdown.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.exam.countdown.model.ExamStatus

@Composable
fun ExamStatusChip(
    status: ExamStatus, 
    modifier: Modifier = Modifier,
    tagColor: Color = Color.Gray
) {
    val (label, bg, textColor) = when (status) {
        ExamStatus.UPCOMING -> Triple(
            "Upcoming", 
            tagColor.copy(alpha = 0.15f), 
            tagColor
        )
        ExamStatus.STARTED -> Triple(
            "In Progress", 
            Color(0xFF34C759).copy(alpha = 0.15f), // iOS System Green
            Color(0xFF34C759)
        )
        ExamStatus.COMPLETED -> Triple(
            "Done", 
            MaterialTheme.colorScheme.surfaceVariant,
            MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
    
    Text(
        text = label,
        modifier = modifier
            .clip(RoundedCornerShape(50))
            .background(bg)
            .padding(horizontal = 12.dp, vertical = 6.dp),
        color = textColor,
        fontSize = 12.sp,
        fontWeight = FontWeight.Medium,
    )
}
