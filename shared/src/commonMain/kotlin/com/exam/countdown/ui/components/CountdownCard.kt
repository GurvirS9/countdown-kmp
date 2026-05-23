package com.exam.countdown.ui.components

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.exam.countdown.model.CountdownState
import com.exam.countdown.model.ExamStatus
import com.exam.countdown.model.ExamUiState
import com.exam.countdown.ui.settings.AppSettings
import com.exam.countdown.ui.settings.TimeFormat
import com.exam.countdown.utils.DateTimeUtils

/** Parse hex color string (e.g. "#6750A4") to Compose Color. */
fun parseColor(hex: String): Color = runCatching {
    Color(("FF" + hex.trimStart('#')).toLong(16))
}.getOrDefault(Color(0xFF007AFF)) // Default to System Blue

@Composable
fun CountdownCard(
    uiState: ExamUiState,
    isHighlighted: Boolean = false,
    onArchive: () -> Unit = {},
    onEdit: () -> Unit = {},
    onDelete: () -> Unit = {},
    modifier: Modifier = Modifier,
) {
    val tagColor = parseColor(uiState.exam.colorTag)
    val cardBg by animateColorAsState(
        if (uiState.countdown.isFinished) MaterialTheme.colorScheme.surfaceVariant
        else MaterialTheme.colorScheme.surface,
        animationSpec = tween(300), label = "cardBg"
    )
    val shape = RoundedCornerShape(16.dp)

    Card(
        modifier = modifier.fillMaxWidth(),
        shape = shape,
        colors = CardDefaults.cardColors(containerColor = cardBg),
        elevation = CardDefaults.cardElevation(if (isHighlighted) 4.dp else 0.dp),
        border = if (isHighlighted) BorderStroke(1.dp, tagColor) else null
    ) {
        Column(Modifier.padding(16.dp)) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth(),
            ) {
                Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
                    Box(
                        Modifier
                            .size(8.dp)
                            .clip(RoundedCornerShape(50))
                            .background(tagColor)
                    )
                    Spacer(Modifier.width(10.dp))
                    Column {
                        Text(
                            uiState.exam.name, 
                            style = MaterialTheme.typography.titleMedium, 
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            "${uiState.exam.subject} • ${uiState.exam.semester}",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                        )
                        Text(
                            DateTimeUtils.formatExamDateTime(uiState.exam.examTimeEpochMillis),
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.75f),
                            fontSize = 11.sp,
                        )
                    }
                }
                ExamStatusChip(status = uiState.countdown.status, tagColor = tagColor)
            }

            Spacer(Modifier.height(16.dp))

            when (uiState.countdown.status) {
                ExamStatus.UPCOMING -> CountdownDigits(uiState.countdown, MaterialTheme.colorScheme.onSurface)
                ExamStatus.STARTED -> Text("Exam in progress", style = MaterialTheme.typography.titleMedium, color = tagColor)
                ExamStatus.COMPLETED -> Text("Completed", style = MaterialTheme.typography.titleMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }

            if (uiState.countdown.status == ExamStatus.UPCOMING && uiState.countdown.progressFraction > 0f) {
                Spacer(Modifier.height(12.dp))
                LinearProgressIndicator(
                    progress = { uiState.countdown.progressFraction },
                    modifier = Modifier.fillMaxWidth().height(4.dp).clip(RoundedCornerShape(50)),
                    color = tagColor,
                    trackColor = MaterialTheme.colorScheme.surfaceVariant,
                )
            }

            Spacer(Modifier.height(8.dp))
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
                TextButton(onClick = onEdit) { Text("Edit", fontSize = 14.sp) }
                TextButton(onClick = onArchive) {
                    Text(if (uiState.exam.archived) "Unarchive" else "Archive", fontSize = 14.sp)
                }
                TextButton(onClick = onDelete) {
                    Text("Delete", color = MaterialTheme.colorScheme.error, fontSize = 14.sp)
                }
            }
        }
    }
}

@Composable
private fun CountdownDigits(state: CountdownState, textColor: Color) {
    val timeFormat by AppSettings.timeFormat.collectAsState()
    Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.fillMaxWidth()) {
        when (timeFormat) {
            TimeFormat.FULL -> {
                CountdownUnit(state.days, "DAYS", textColor)
                CountdownUnit(state.hours, "HRS", textColor)
                CountdownUnit(state.minutes, "MIN", textColor)
                CountdownUnit(state.seconds, "SEC", textColor)
            }
            TimeFormat.HHMMSS -> {
                val totalHours = state.days * 24 + state.hours
                CountdownUnit(totalHours, "HRS", textColor)
                CountdownUnit(state.minutes, "MIN", textColor)
                CountdownUnit(state.seconds, "SEC", textColor)
            }
        }
    }
}

@Composable
private fun RowScope.CountdownUnit(value: Long, label: String, textColor: Color) {
    Column(modifier = Modifier.weight(1f), horizontalAlignment = Alignment.CenterHorizontally) {
        AnimatedContent(
            targetState = value,
            transitionSpec = { fadeIn(tween(150)) togetherWith fadeOut(tween(150)) },
            label = label,
        ) { v ->
            Text(
                text = v.toString().padStart(2, '0'),
                style = MaterialTheme.typography.headlineSmall,
                color = textColor,
            )
        }
        Text(label, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
    }
}
