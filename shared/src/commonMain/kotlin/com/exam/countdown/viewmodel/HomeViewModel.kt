package com.exam.countdown.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.exam.countdown.countdown.CountdownEngine
import com.exam.countdown.model.Exam
import com.exam.countdown.model.ExamUiState
import com.exam.countdown.repository.ExamRepository
import com.exam.countdown.utils.DateTimeUtils
import com.exam.countdown.utils.SampleData
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class HomeViewModel(private val repository: ExamRepository) : ViewModel() {

    val searchQuery = MutableStateFlow("")
    val archiveVisible = MutableStateFlow(false)

    val upcomingExams: StateFlow<List<ExamUiState>> = combine(
        repository.observeUpcoming(),
        CountdownEngine.ticker,
        searchQuery,
    ) { exams, nowMillis, query ->
        exams
            .filter { exam ->
                query.isBlank() ||
                    exam.name.contains(query, ignoreCase = true) ||
                    exam.subject.contains(query, ignoreCase = true) ||
                    exam.semester.contains(query, ignoreCase = true)
            }
            .map { exam ->
                ExamUiState(
                    exam = exam,
                    countdown = DateTimeUtils.computeCountdown(nowMillis, exam.examTimeEpochMillis),
                )
            }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    val archivedExams: StateFlow<List<ExamUiState>> = combine(
        repository.observeArchived(),
        CountdownEngine.ticker,
    ) { exams, nowMillis ->
        exams.map { exam ->
            ExamUiState(exam = exam, countdown = DateTimeUtils.computeCountdown(nowMillis, exam.examTimeEpochMillis))
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    init {
        // Seed sample data when the DB is empty (wait for first real emission)
        viewModelScope.launch {
            val firstLoad = repository.observeUpcoming().first()
            if (firstLoad.isEmpty()) {
                repository.insertAll(SampleData.exams)
            }
        }
    }

    fun onSearchQueryChanged(query: String) = searchQuery.update { query }
    fun toggleArchiveVisible() = archiveVisible.update { !it }
    fun archiveExam(exam: Exam) = viewModelScope.launch { repository.archive(exam.id) }
    fun unarchiveExam(exam: Exam) = viewModelScope.launch { repository.unarchive(exam.id) }
    fun deleteExam(exam: Exam) = viewModelScope.launch { repository.delete(exam.id) }
}
