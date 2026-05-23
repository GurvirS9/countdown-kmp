package com.exam.countdown.repository

import com.exam.countdown.model.Exam
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.update

/**
 * In-memory ExamRepository for WasmJS (browser) target.
 * Data is lost on page refresh — suitable for demo. A localStorage layer
 * can be layered on top in a future iteration.
 */
class InMemoryExamRepository : ExamRepository {

    private val _exams = MutableStateFlow<List<Exam>>(emptyList())
    private var nextId = 1L

    override fun observeUpcoming(): Flow<List<Exam>> =
        _exams.map { list -> list.filter { !it.archived }.sortedBy { it.examTimeEpochMillis } }

    override fun observeArchived(): Flow<List<Exam>> =
        _exams.map { list -> list.filter { it.archived }.sortedByDescending { it.examTimeEpochMillis } }

    override suspend fun getById(id: Long): Exam? = _exams.value.find { it.id == id }

    override suspend fun insert(exam: Exam): Long {
        val newId = nextId++
        _exams.update { it + exam.copy(id = newId) }
        return newId
    }

    override suspend fun update(exam: Exam) {
        _exams.update { list -> list.map { if (it.id == exam.id) exam else it } }
    }

    override suspend fun archive(id: Long) {
        _exams.update { list -> list.map { if (it.id == id) it.copy(archived = true) else it } }
    }

    override suspend fun unarchive(id: Long) {
        _exams.update { list -> list.map { if (it.id == id) it.copy(archived = false) else it } }
    }

    override suspend fun delete(id: Long) {
        _exams.update { list -> list.filter { it.id != id } }
    }

    override suspend fun insertAll(exams: List<Exam>) {
        exams.forEach { insert(it) }
    }
}
