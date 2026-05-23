package com.exam.countdown.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.exam.countdown.model.COLOR_TAGS
import com.exam.countdown.model.Exam
import com.exam.countdown.repository.ExamRepository
import com.exam.countdown.utils.DateTimeUtils
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.datetime.Clock
import kotlinx.datetime.Instant
import kotlinx.datetime.toLocalDateTime

class AddExamViewModel(private val repository: ExamRepository) : ViewModel() {

    val name = MutableStateFlow("")
    val subject = MutableStateFlow("")
    val semester = MutableStateFlow("")
    val selectedColor = MutableStateFlow(COLOR_TAGS.first())

    private val today = DateTimeUtils.todayInIST()
    val selectedYear = MutableStateFlow(today.first)
    val selectedMonth = MutableStateFlow(today.second)
    val selectedDay = MutableStateFlow(today.third)
    val selectedHour = MutableStateFlow(9)
    val selectedMinute = MutableStateFlow(0)

    val nameError = MutableStateFlow<String?>(null)
    val subjectError = MutableStateFlow<String?>(null)
    val dateError = MutableStateFlow<String?>(null)
    val saveSuccess = MutableStateFlow(false)

    fun loadExam(exam: Exam) {
        name.update { exam.name }
        subject.update { exam.subject }
        semester.update { exam.semester }
        selectedColor.update { exam.colorTag }
        val dt = Instant.fromEpochMilliseconds(exam.examTimeEpochMillis)
            .toLocalDateTime(DateTimeUtils.IST)
        selectedYear.update { dt.year }
        selectedMonth.update { dt.monthNumber }
        selectedDay.update { dt.dayOfMonth }
        selectedHour.update { dt.hour }
        selectedMinute.update { dt.minute }
    }

    fun onNameChanged(v: String) { name.update { v }; nameError.update { null } }
    fun onSubjectChanged(v: String) { subject.update { v }; subjectError.update { null } }
    fun onSemesterChanged(v: String) = semester.update { v }
    fun onColorChanged(v: String) = selectedColor.update { v }
    fun onDateSelected(y: Int, m: Int, d: Int) {
        selectedYear.update { y }; selectedMonth.update { m }; selectedDay.update { d }
        dateError.update { null }
    }
    fun onTimeSelected(h: Int, min: Int) {
        selectedHour.update { h }; selectedMinute.update { min }
    }

    fun saveExam(editingId: Long? = null) {
        var valid = true
        if (name.value.isBlank()) { nameError.update { "Name is required" }; valid = false }
        if (subject.value.isBlank()) { subjectError.update { "Subject is required" }; valid = false }

        val epochMillis = try {
            DateTimeUtils.toEpochMillis(
                selectedYear.value, selectedMonth.value, selectedDay.value,
                selectedHour.value, selectedMinute.value,
            )
        } catch (e: Exception) {
            dateError.update { "Invalid date/time: ${e.message}" }; valid = false; return
        }

        if (epochMillis < Clock.System.now().toEpochMilliseconds()) {
            dateError.update { "Exam date must be in the future" }; valid = false
        }

        if (!valid) return

        val exam = Exam(
            id = editingId ?: 0L,
            name = name.value.trim(),
            subject = subject.value.trim(),
            semester = semester.value.trim(),
            examTimeEpochMillis = epochMillis,
            colorTag = selectedColor.value,
        )
        viewModelScope.launch {
            if (editingId != null) repository.update(exam) else repository.insert(exam)
            saveSuccess.update { true }
        }
    }
}
