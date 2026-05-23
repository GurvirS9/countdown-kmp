package com.exam.countdown.ui.screens

import androidx.compose.animation.*
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.exam.countdown.model.Exam
import com.exam.countdown.model.ExamUiState
import com.exam.countdown.ui.components.CountdownCard
import com.exam.countdown.ui.components.parseColor
import com.exam.countdown.viewmodel.HomeViewModel
import org.koin.compose.viewmodel.koinViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    onNavigateToAdd: () -> Unit,
    onNavigateToImport: () -> Unit,
    onNavigateToSettings: () -> Unit,
    onNavigateToEdit: (Exam) -> Unit,
    viewModel: HomeViewModel = koinViewModel(),
) {
    val upcoming by viewModel.upcomingExams.collectAsStateWithLifecycle()
    val archived by viewModel.archivedExams.collectAsStateWithLifecycle()
    val searchQuery by viewModel.searchQuery.collectAsStateWithLifecycle()
    val archiveVisible by viewModel.archiveVisible.collectAsStateWithLifecycle()
    val nextExam = upcoming.firstOrNull()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { },
                actions = {
                    IconButton(onClick = onNavigateToImport) {
                        Icon(Icons.Filled.ArrowForward, contentDescription = "Import")
                    }
                    IconButton(onClick = onNavigateToSettings) {
                        Icon(Icons.Filled.Settings, contentDescription = "Settings")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background,
                )
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = onNavigateToAdd,
                containerColor = MaterialTheme.colorScheme.primary,
                shape = RoundedCornerShape(16.dp)
            ) { Icon(Icons.Filled.Add, "Add Exam", tint = MaterialTheme.colorScheme.onPrimary) }
        },
        containerColor = MaterialTheme.colorScheme.background,
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 20.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            item {
                Spacer(Modifier.height(8.dp))
                // One UI style large header
                Text(
                    "Exams",
                    style = MaterialTheme.typography.displayMedium,
                    color = MaterialTheme.colorScheme.onBackground,
                )
                Spacer(Modifier.height(8.dp))
            }

            item {
                SearchField(query = searchQuery, onQueryChanged = viewModel::onSearchQueryChanged)
                Spacer(Modifier.height(16.dp))
            }

            nextExam?.let { next ->
                item {
                    Text(
                        "Next Exam",
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                    Spacer(Modifier.height(8.dp))
                    HeroCard(uiState = next)
                    Spacer(Modifier.height(16.dp))
                }
            }

            if (upcoming.isNotEmpty()) {
                item {
                    Text(
                        "Upcoming",
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                    Spacer(Modifier.height(8.dp))
                }
            } else {
                item {
                    Box(Modifier.fillMaxWidth().padding(32.dp), contentAlignment = Alignment.Center) {
                        Text("No upcoming exams", color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                }
            }

            items(upcoming, key = { it.exam.id }) { uiState ->
                CountdownCard(
                    uiState = uiState,
                    isHighlighted = false,
                    onArchive = { viewModel.archiveExam(uiState.exam) },
                    onEdit = { onNavigateToEdit(uiState.exam) },
                    onDelete = { viewModel.deleteExam(uiState.exam) },
                )
            }

            if (archived.isNotEmpty()) {
                item {
                    Spacer(Modifier.height(8.dp))
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.fillMaxWidth(),
                    ) {
                        Text(
                            "Archived (${archived.size})",
                            style = MaterialTheme.typography.titleMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.weight(1f),
                        )
                        TextButton(onClick = viewModel::toggleArchiveVisible) {
                            Text(if (archiveVisible) "Hide" else "Show")
                        }
                    }
                }

                if (archiveVisible) {
                    items(archived, key = { "arc_${it.exam.id}" }) { uiState ->
                        CountdownCard(
                            uiState = uiState,
                            onArchive = { viewModel.unarchiveExam(uiState.exam) },
                            onEdit = { onNavigateToEdit(uiState.exam) },
                            onDelete = { viewModel.deleteExam(uiState.exam) },
                        )
                    }
                }
            }

            item { Spacer(Modifier.height(96.dp)) }
        }
    }
}

@Composable
private fun SearchField(query: String, onQueryChanged: (String) -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(50)) // iOS pill style
            .background(MaterialTheme.colorScheme.surfaceVariant)
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Icon(Icons.Filled.Search, "Search",
            tint = MaterialTheme.colorScheme.onSurfaceVariant, modifier = Modifier.size(20.dp))
        Spacer(Modifier.width(10.dp))
        BasicTextField(
            value = query,
            onValueChange = onQueryChanged,
            modifier = Modifier.weight(1f),
            textStyle = MaterialTheme.typography.bodyMedium.copy(color = MaterialTheme.colorScheme.onSurface),
            decorationBox = { inner ->
                if (query.isEmpty()) Text("Search",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant)
                inner()
            },
        )
        if (query.isNotEmpty()) {
            IconButton(onClick = { onQueryChanged("") }, modifier = Modifier.size(20.dp)) {
                Icon(Icons.Filled.Close, "Clear", modifier = Modifier.size(16.dp))
            }
        }
    }
}

@Composable
private fun HeroCard(uiState: ExamUiState) {
    val timeFormat by com.exam.countdown.ui.settings.AppSettings.timeFormat.collectAsState()
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer,
            contentColor = MaterialTheme.colorScheme.onPrimaryContainer
        ),
        elevation = CardDefaults.cardElevation(0.dp)
    ) {
        Column(Modifier.padding(24.dp)) {
            Text(uiState.exam.name, style = MaterialTheme.typography.headlineMedium)
            Spacer(Modifier.height(4.dp))
            Text("${uiState.exam.subject} · ${uiState.exam.semester}",
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.8f))
            Spacer(Modifier.height(4.dp))
            Text(
                com.exam.countdown.utils.DateTimeUtils.formatExamDateTime(uiState.exam.examTimeEpochMillis),
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.65f),
            )

            Spacer(Modifier.height(24.dp))

            when (timeFormat) {
                com.exam.countdown.ui.settings.TimeFormat.FULL -> Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    HeroUnit(uiState.countdown.days, "DAYS")
                    HeroUnit(uiState.countdown.hours, "HRS")
                    HeroUnit(uiState.countdown.minutes, "MIN")
                    HeroUnit(uiState.countdown.seconds, "SEC")
                }
                com.exam.countdown.ui.settings.TimeFormat.HHMMSS -> {
                    val totalHours = uiState.countdown.days * 24 + uiState.countdown.hours
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        HeroUnit(totalHours, "HRS")
                        HeroUnit(uiState.countdown.minutes, "MIN")
                        HeroUnit(uiState.countdown.seconds, "SEC")
                    }
                }
            }
        }
    }
}

@Composable
private fun HeroUnit(value: Long, label: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        AnimatedContent(
            targetState = value,
            transitionSpec = { fadeIn(tween(300)) togetherWith fadeOut(tween(300)) },
            label = "hero_$label",
        ) { v ->
            Text(
                v.toString().padStart(2, '0'), 
                style = MaterialTheme.typography.headlineLarge,
                color = MaterialTheme.colorScheme.primary
            )
        }
        Text(
            label, 
            style = MaterialTheme.typography.labelSmall, 
            color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.7f)
        )
    }
}

