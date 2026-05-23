package com.exam.countdown.repository

import com.exam.countdown.model.Exam
import kotlinx.coroutines.flow.Flow

/** All exam data operations. Implementations are platform-specific via DI. */
interface ExamRepository {
    fun observeUpcoming(): Flow<List<Exam>>
    fun observeArchived(): Flow<List<Exam>>
    suspend fun getById(id: Long): Exam?
    suspend fun insert(exam: Exam): Long
    suspend fun update(exam: Exam)
    suspend fun archive(id: Long)
    suspend fun unarchive(id: Long)
    suspend fun delete(id: Long)
    suspend fun insertAll(exams: List<Exam>)
}
