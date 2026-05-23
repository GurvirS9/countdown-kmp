package com.exam.countdown.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.exam.countdown.parser.ParseResult
import com.exam.countdown.utils.DateTimeUtils
import com.exam.countdown.viewmodel.ImportViewModel
import org.koin.compose.viewmodel.koinViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ImportScreen(
    onBack: () -> Unit,
    viewModel: ImportViewModel = koinViewModel(),
) {
    val rawText by viewModel.rawText.collectAsStateWithLifecycle()
    val results by viewModel.parseResults.collectAsStateWithLifecycle()
    val importDone by viewModel.importDone.collectAsStateWithLifecycle()
    val isImporting by viewModel.isImporting.collectAsStateWithLifecycle()

    LaunchedEffect(importDone) { if (importDone) onBack() }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Import Datesheet") },
                navigationIcon = {
                    IconButton(onClick = onBack) { Icon(Icons.AutoMirrored.Filled.ArrowBack, "Back") }
                },
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 20.dp),
        ) {
            Spacer(Modifier.height(8.dp))

            Text("Supported formats:", style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant)
            Spacer(Modifier.height(4.dp))
            listOf(
                "Physics - 24 May 2026 - 9:00 AM",
                "Chemistry: 26/05/2026 14:30",
                "Maths 30 May 2026 10 AM",
                "English | 02 Jun 2026 | 2 PM",
            ).forEach { Text("  • $it", style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant) }

            Spacer(Modifier.height(12.dp))

            OutlinedTextField(
                value = rawText,
                onValueChange = viewModel::onTextChanged,
                modifier = Modifier.fillMaxWidth().height(180.dp),
                placeholder = { Text("Paste your datesheet here...") },
                label = { Text("Datesheet Text") },
            )

            Spacer(Modifier.height(12.dp))

            Button(
                onClick = viewModel::parsePreview,
                modifier = Modifier.fillMaxWidth(),
                enabled = rawText.isNotBlank(),
            ) { Text("Preview") }

            if (results.isNotEmpty()) {
                Spacer(Modifier.height(12.dp))
                Text(
                    "Preview: ${viewModel.successCount} valid · ${viewModel.errorCount} errors",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.SemiBold,
                )
                Spacer(Modifier.height(8.dp))

                LazyColumn(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(6.dp),
                ) {
                    items(results) { result -> ParseResultRow(result) }
                }

                Spacer(Modifier.height(12.dp))
                Button(
                    onClick = viewModel::importValid,
                    enabled = viewModel.successCount > 0 && !isImporting,
                    modifier = Modifier.fillMaxWidth(),
                ) {
                    if (isImporting) {
                        CircularProgressIndicator(Modifier.size(20.dp), strokeWidth = 2.dp)
                    } else {
                        Text("Import ${viewModel.successCount} Exams")
                    }
                }
                Spacer(Modifier.height(16.dp))
            }
        }
    }
}

@Composable
private fun ParseResultRow(result: ParseResult) {
    val isSuccess = result is ParseResult.Success
    val bg = if (isSuccess) Color(0xFF1B3A2A) else Color(0xFF3A1B1B)
    val iconTint = if (isSuccess) Color(0xFF69F0AE) else Color(0xFFFF6B6B)
    val text = when (result) {
        is ParseResult.Success -> result.exam.name
        is ParseResult.Error -> result.line
    }
    val detail = when (result) {
        is ParseResult.Success -> DateTimeUtils.formatExamDateTime(result.exam.examTimeEpochMillis)
        is ParseResult.Error -> result.reason
    }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(10.dp))
            .background(bg)
            .padding(10.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Icon(
            imageVector = if (isSuccess) Icons.Filled.CheckCircle else Icons.Filled.Warning,
            contentDescription = null,
            tint = iconTint,
            modifier = Modifier.size(20.dp),
        )
        Spacer(Modifier.width(10.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(text, style = MaterialTheme.typography.bodySmall, fontWeight = FontWeight.Medium)
            Text(detail, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
    }
}
