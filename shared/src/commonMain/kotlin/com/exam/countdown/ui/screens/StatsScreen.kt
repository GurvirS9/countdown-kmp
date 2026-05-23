package com.exam.countdown.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.exam.countdown.model.ExamStatus
import com.exam.countdown.viewmodel.HomeViewModel
import org.koin.compose.viewmodel.koinViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StatsScreen(
    onBack: () -> Unit,
    viewModel: HomeViewModel = koinViewModel(),
) {
    val upcoming by viewModel.upcomingExams.collectAsStateWithLifecycle()
    val archived by viewModel.archivedExams.collectAsStateWithLifecycle()

    val upcomingCount = upcoming.count { it.countdown.status == ExamStatus.UPCOMING }
    val startedCount = upcoming.count { it.countdown.status == ExamStatus.STARTED }
    val completedCount = archived.size

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Statistics") },
                navigationIcon = { IconButton(onClick = onBack) { Icon(Icons.AutoMirrored.Filled.ArrowBack, "Back") } },
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 20.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            Spacer(Modifier.height(8.dp))
            Text("Overview", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)

            // Stats cards row
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp), modifier = Modifier.fillMaxWidth()) {
                StatCard("Upcoming", upcomingCount, Color(0xFF82B1FF), modifier = Modifier.weight(1f))
                StatCard("In Progress", startedCount, Color(0xFFFFD166), modifier = Modifier.weight(1f))
                StatCard("Done", completedCount, Color(0xFF69F0AE), modifier = Modifier.weight(1f))
            }

            // Next exam details
            upcoming.firstOrNull()?.let { next ->
                Spacer(Modifier.height(8.dp))
                Text("Next Exam", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                ) {
                    Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        Text(next.exam.name, fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleMedium)
                        Text("${next.exam.subject} · ${next.exam.semester}", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        Spacer(Modifier.height(4.dp))
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            BigStat(next.countdown.days.toString(), "Days")
                            BigStat(next.countdown.hours.toString(), "Hours")
                            BigStat(next.countdown.minutes.toString(), "Mins")
                        }
                    }
                }
            }

            // Semester breakdown
            val bySemester = (upcoming + archived)
                .groupBy { it.exam.semester }
                .filterKeys { it.isNotBlank() }
            if (bySemester.isNotEmpty()) {
                Text("By Semester", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
                bySemester.forEach { (sem, list) ->
                    Row(
                        Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Text(sem.ifBlank { "Unspecified" }, style = MaterialTheme.typography.bodyMedium)
                        Text("${list.size} exam${if (list.size != 1) "s" else ""}", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                    HorizontalDivider(thickness = 0.5.dp, color = MaterialTheme.colorScheme.surfaceVariant)
                }
            }

            Spacer(Modifier.height(32.dp))
        }
    }
}

@Composable
private fun StatCard(label: String, count: Int, color: Color, modifier: Modifier = Modifier) {
    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
    ) {
        Column(
            Modifier.padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
        ) {
            Text(count.toString(), style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold, color = color)
            Text(label, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
    }
}

@Composable
private fun BigStat(value: String, label: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(value, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
        Text(label, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
    }
}
