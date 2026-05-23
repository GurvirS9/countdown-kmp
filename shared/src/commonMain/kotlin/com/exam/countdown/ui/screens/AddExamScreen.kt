package com.exam.countdown.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.exam.countdown.model.Exam
import com.exam.countdown.ui.components.ColorPicker
import com.exam.countdown.utils.DateTimeUtils
import com.exam.countdown.viewmodel.AddExamViewModel
import kotlinx.datetime.Instant
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import org.koin.compose.viewmodel.koinViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddExamScreen(
    editingExam: Exam? = null,
    onBack: () -> Unit,
    viewModel: AddExamViewModel = koinViewModel(),
) {
    LaunchedEffect(editingExam) { editingExam?.let { viewModel.loadExam(it) } }

    val name by viewModel.name.collectAsStateWithLifecycle()
    val subject by viewModel.subject.collectAsStateWithLifecycle()
    val semester by viewModel.semester.collectAsStateWithLifecycle()
    val selectedColor by viewModel.selectedColor.collectAsStateWithLifecycle()
    val nameError by viewModel.nameError.collectAsStateWithLifecycle()
    val subjectError by viewModel.subjectError.collectAsStateWithLifecycle()
    val dateError by viewModel.dateError.collectAsStateWithLifecycle()
    val saveSuccess by viewModel.saveSuccess.collectAsStateWithLifecycle()
    val year by viewModel.selectedYear.collectAsStateWithLifecycle()
    val month by viewModel.selectedMonth.collectAsStateWithLifecycle()
    val day by viewModel.selectedDay.collectAsStateWithLifecycle()
    val hour by viewModel.selectedHour.collectAsStateWithLifecycle()
    val minute by viewModel.selectedMinute.collectAsStateWithLifecycle()

    LaunchedEffect(saveSuccess) { if (saveSuccess) onBack() }

    var showDatePicker by remember { mutableStateOf(false) }
    var showTimePicker by remember { mutableStateOf(false) }
    val datePickerState = rememberDatePickerState()
    val timePickerState = rememberTimePickerState(initialHour = hour, initialMinute = minute)

    if (showDatePicker) {
        DatePickerDialog(
            onDismissRequest = { showDatePicker = false },
            confirmButton = {
                TextButton(onClick = {
                    datePickerState.selectedDateMillis?.let { millis ->
                        val dt = Instant.fromEpochMilliseconds(millis)
                            .toLocalDateTime(TimeZone.UTC)
                        viewModel.onDateSelected(dt.year, dt.monthNumber, dt.dayOfMonth)
                    }
                    showDatePicker = false
                }) { Text("OK") }
            },
            dismissButton = { TextButton(onClick = { showDatePicker = false }) { Text("Cancel") } },
        ) { DatePicker(state = datePickerState) }
    }

    if (showTimePicker) {
        AlertDialog(
            onDismissRequest = { showTimePicker = false },
            title = { Text("Select Time (IST)") },
            text = { TimePicker(state = timePickerState) },
            confirmButton = {
                TextButton(onClick = {
                    viewModel.onTimeSelected(timePickerState.hour, timePickerState.minute)
                    showTimePicker = false
                }) { Text("OK") }
            },
            dismissButton = { TextButton(onClick = { showTimePicker = false }) { Text("Cancel") } },
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(if (editingExam != null) "Edit Exam" else "Add Exam") },
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
                .padding(horizontal = 20.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            Spacer(Modifier.height(8.dp))

            OutlinedTextField(
                value = name, onValueChange = viewModel::onNameChanged,
                label = { Text("Exam Name *") },
                isError = nameError != null,
                supportingText = { nameError?.let { Text(it, color = MaterialTheme.colorScheme.error) } },
                modifier = Modifier.fillMaxWidth(),
                keyboardOptions = KeyboardOptions(capitalization = KeyboardCapitalization.Words),
                singleLine = true,
            )

            OutlinedTextField(
                value = subject, onValueChange = viewModel::onSubjectChanged,
                label = { Text("Subject *") },
                isError = subjectError != null,
                supportingText = { subjectError?.let { Text(it, color = MaterialTheme.colorScheme.error) } },
                modifier = Modifier.fillMaxWidth(),
                keyboardOptions = KeyboardOptions(capitalization = KeyboardCapitalization.Words),
                singleLine = true,
            )

            OutlinedTextField(
                value = semester, onValueChange = viewModel::onSemesterChanged,
                label = { Text("Semester") },
                placeholder = { Text("e.g. Sem 6, Year 2") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
            )

            Column {
                Text("Exam Date *", style = MaterialTheme.typography.labelLarge)
                Spacer(Modifier.height(4.dp))
                OutlinedButton(onClick = { showDatePicker = true }, modifier = Modifier.fillMaxWidth()) {
                    Text("$day / $month / $year")
                }
                dateError?.let {
                    Text(it, color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.bodySmall)
                }
            }

            Column {
                Text("Exam Time * (IST)", style = MaterialTheme.typography.labelLarge)
                Spacer(Modifier.height(4.dp))
                val h12 = if (hour % 12 == 0) 12 else hour % 12
                val amPm = if (hour < 12) "AM" else "PM"
                OutlinedButton(onClick = { showTimePicker = true }, modifier = Modifier.fillMaxWidth()) {
                    Text("$h12:${minute.toString().padStart(2,'0')} $amPm")
                }
            }

            Column {
                Text("Color Tag", style = MaterialTheme.typography.labelLarge)
                Spacer(Modifier.height(8.dp))
                ColorPicker(selectedColor = selectedColor, onColorSelected = viewModel::onColorChanged)
            }

            Spacer(Modifier.height(8.dp))

            Button(
                onClick = { viewModel.saveExam(editingId = editingExam?.id) },
                modifier = Modifier.fillMaxWidth().height(52.dp),
            ) {
                Text(
                    if (editingExam != null) "Save Changes" else "Add Exam",
                    style = MaterialTheme.typography.titleMedium,
                )
            }
            Spacer(Modifier.height(32.dp))
        }
    }
}
