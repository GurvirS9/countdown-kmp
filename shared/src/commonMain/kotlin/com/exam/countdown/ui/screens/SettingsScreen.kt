package com.exam.countdown.ui.screens

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.exam.countdown.model.ExamStatus
import com.exam.countdown.ui.settings.AppSettings
import com.exam.countdown.ui.settings.ThemeMode
import com.exam.countdown.ui.settings.TimeFormat
import com.exam.countdown.viewmodel.HomeViewModel
import org.koin.compose.viewmodel.koinViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    onBack: () -> Unit,
    viewModel: HomeViewModel = koinViewModel(),
) {
    val upcoming by viewModel.upcomingExams.collectAsStateWithLifecycle()
    val archived by viewModel.archivedExams.collectAsStateWithLifecycle()
    val themeMode by AppSettings.themeMode.collectAsState()
    val timeFormat by AppSettings.timeFormat.collectAsState()

    val upcomingCount = upcoming.count { it.countdown.status == ExamStatus.UPCOMING }
    val startedCount  = upcoming.count { it.countdown.status == ExamStatus.STARTED }
    val completedCount = archived.size
    val totalCount = upcomingCount + startedCount + completedCount

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Settings") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background,
                ),
            )
        },
        containerColor = MaterialTheme.colorScheme.background,
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 20.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(24.dp),
        ) {
            Spacer(Modifier.height(4.dp))

            // ─── Appearance ───────────────────────────────────────────────
            SettingsSection(title = "Appearance") {
                SettingsLabel("Theme")
                Spacer(Modifier.height(8.dp))
                ThemeSegmentedButtons(current = themeMode, onSelect = AppSettings::setThemeMode)
            }

            // ─── Countdown Format ─────────────────────────────────────────
            SettingsSection(title = "Countdown Format") {
                SettingsLabel("Time Display")
                Spacer(Modifier.height(8.dp))
                TimeFormatSegmentedButtons(current = timeFormat, onSelect = AppSettings::setTimeFormat)
                Spacer(Modifier.height(4.dp))
                Text(
                    when (timeFormat) {
                        TimeFormat.FULL   -> "Shows days, hours, minutes and seconds — e.g. 02:14:30:05"
                        TimeFormat.HHMMSS -> "Shows hours, minutes and seconds only — e.g. 62:30:05"
                    },
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }

            // ─── Statistics ───────────────────────────────────────────────
            SettingsSection(title = "Statistics") {
                // Overview chips row
                Row(
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                    modifier = Modifier.fillMaxWidth(),
                ) {
                    StatChip("Upcoming",   upcomingCount,  Color(0xFF82B1FF), modifier = Modifier.weight(1f))
                    StatChip("In Progress", startedCount,  Color(0xFFFFD166), modifier = Modifier.weight(1f))
                    StatChip("Done",       completedCount, Color(0xFF69F0AE), modifier = Modifier.weight(1f))
                }

                Spacer(Modifier.height(8.dp))

                // Total row
                SettingsRow(
                    icon = Icons.Default.Info,
                    label = "Total Exams",
                    value = totalCount.toString(),
                )

                // Next exam
                upcoming.firstOrNull()?.let { next ->
                    HorizontalDivider(
                        thickness = 0.5.dp,
                        color = MaterialTheme.colorScheme.surfaceVariant,
                        modifier = Modifier.padding(vertical = 4.dp),
                    )
                    SettingsRow(
                        icon = Icons.Default.DateRange,
                        label = "Next Exam",
                        value = next.exam.name,
                    )
                    Text(
                        "${next.exam.subject} · ${next.exam.semester}",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.padding(start = 44.dp),
                    )
                    Spacer(Modifier.height(6.dp))
                    // Quick countdown summary
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(16.dp),
                        modifier = Modifier.padding(start = 44.dp),
                    ) {
                        MiniStat(next.countdown.days.toString(), "Days")
                        MiniStat(next.countdown.hours.toString(), "Hrs")
                        MiniStat(next.countdown.minutes.toString(), "Min")
                    }
                }

                // Semester breakdown
                val bySemester = (upcoming + archived)
                    .groupBy { it.exam.semester }
                    .filterKeys { it.isNotBlank() }
                if (bySemester.isNotEmpty()) {
                    HorizontalDivider(
                        thickness = 0.5.dp,
                        color = MaterialTheme.colorScheme.surfaceVariant,
                        modifier = Modifier.padding(vertical = 8.dp),
                    )
                    Text(
                        "By Semester",
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        fontWeight = FontWeight.SemiBold,
                    )
                    Spacer(Modifier.height(4.dp))
                    bySemester.forEach { (sem, list) ->
                        Row(
                            Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically,
                        ) {
                            Text(sem.ifBlank { "Unspecified" }, style = MaterialTheme.typography.bodyMedium)
                            Text(
                                "${list.size} exam${if (list.size != 1) "s" else ""}",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                            )
                        }
                        HorizontalDivider(thickness = 0.3.dp, color = MaterialTheme.colorScheme.surfaceVariant)
                    }
                }
            }

            Spacer(Modifier.height(32.dp))
        }
    }
}

// ─── Reusable settings UI primitives ─────────────────────────────────────────

@Composable
private fun SettingsSection(
    title: String,
    content: @Composable ColumnScope.() -> Unit,
) {
    Column {
        Text(
            title.uppercase(),
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.primary,
            fontWeight = FontWeight.SemiBold,
            modifier = Modifier.padding(bottom = 8.dp),
        )
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            elevation = CardDefaults.cardElevation(0.dp),
        ) {
            Column(Modifier.padding(16.dp), content = content)
        }
    }
}

@Composable
private fun SettingsLabel(text: String) {
    Text(
        text,
        style = MaterialTheme.typography.bodyMedium,
        fontWeight = FontWeight.Medium,
        color = MaterialTheme.colorScheme.onSurface,
    )
}

@Composable
private fun SettingsRow(icon: ImageVector, label: String, value: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Icon(
            icon,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.primary,
            modifier = Modifier.size(20.dp),
        )
        Spacer(Modifier.width(12.dp))
        Text(label, style = MaterialTheme.typography.bodyMedium, modifier = Modifier.weight(1f))
        Text(value, style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.SemiBold,
            color = MaterialTheme.colorScheme.onSurfaceVariant)
    }
}

@Composable
private fun ThemeSegmentedButtons(current: ThemeMode, onSelect: (ThemeMode) -> Unit) {
    Row(
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        modifier = Modifier.fillMaxWidth(),
    ) {
        ThemeOption(
            label = "System",
            icon = Icons.Default.Settings,
            selected = current == ThemeMode.SYSTEM,
            onClick = { onSelect(ThemeMode.SYSTEM) },
            modifier = Modifier.weight(1f),
        )
        ThemeOption(
            label = "Light",
            icon = Icons.Default.Star,
            selected = current == ThemeMode.LIGHT,
            onClick = { onSelect(ThemeMode.LIGHT) },
            modifier = Modifier.weight(1f),
        )
        ThemeOption(
            label = "Dark",
            icon = Icons.Default.Face,
            selected = current == ThemeMode.DARK,
            onClick = { onSelect(ThemeMode.DARK) },
            modifier = Modifier.weight(1f),
        )
    }
}

@Composable
private fun ThemeOption(
    label: String,
    icon: ImageVector,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val bg by animateColorAsState(
        if (selected) MaterialTheme.colorScheme.primaryContainer
        else MaterialTheme.colorScheme.surfaceVariant,
        animationSpec = tween(200), label = "themeBg"
    )
    val contentColor by animateColorAsState(
        if (selected) MaterialTheme.colorScheme.primary
        else MaterialTheme.colorScheme.onSurfaceVariant,
        animationSpec = tween(200), label = "themeContent"
    )
    Surface(
        onClick = onClick,
        modifier = modifier,
        shape = RoundedCornerShape(12.dp),
        color = bg,
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.padding(vertical = 10.dp),
        ) {
            Icon(icon, contentDescription = label, tint = contentColor, modifier = Modifier.size(20.dp))
            Spacer(Modifier.height(4.dp))
            Text(
                label,
                style = MaterialTheme.typography.labelSmall,
                color = contentColor,
                fontWeight = if (selected) FontWeight.SemiBold else FontWeight.Normal,
            )
        }
    }
}

@Composable
private fun TimeFormatSegmentedButtons(current: TimeFormat, onSelect: (TimeFormat) -> Unit) {
    Row(
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        modifier = Modifier.fillMaxWidth(),
    ) {
        FormatOption(
            label = "DD:HH:MM:SS",
            selected = current == TimeFormat.FULL,
            onClick = { onSelect(TimeFormat.FULL) },
            modifier = Modifier.weight(1f),
        )
        FormatOption(
            label = "HH:MM:SS",
            selected = current == TimeFormat.HHMMSS,
            onClick = { onSelect(TimeFormat.HHMMSS) },
            modifier = Modifier.weight(1f),
        )
    }
}

@Composable
private fun FormatOption(
    label: String,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val bg by animateColorAsState(
        if (selected) MaterialTheme.colorScheme.primaryContainer
        else MaterialTheme.colorScheme.surfaceVariant,
        animationSpec = tween(200), label = "fmtBg"
    )
    val contentColor by animateColorAsState(
        if (selected) MaterialTheme.colorScheme.primary
        else MaterialTheme.colorScheme.onSurfaceVariant,
        animationSpec = tween(200), label = "fmtContent"
    )
    Surface(
        onClick = onClick,
        modifier = modifier,
        shape = RoundedCornerShape(12.dp),
        color = bg,
    ) {
        Box(contentAlignment = Alignment.Center, modifier = Modifier.padding(12.dp)) {
            Text(
                label,
                style = MaterialTheme.typography.labelMedium,
                color = contentColor,
                fontWeight = if (selected) FontWeight.Bold else FontWeight.Normal,
            )
        }
    }
}

@Composable
private fun StatChip(label: String, count: Int, color: Color, modifier: Modifier = Modifier) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
        elevation = CardDefaults.cardElevation(0.dp),
    ) {
        Column(
            Modifier.padding(10.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Text(count.toString(), style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold, color = color)
            Text(label, style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
    }
}

@Composable
private fun MiniStat(value: String, label: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(value, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary)
        Text(label, style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant)
    }
}
