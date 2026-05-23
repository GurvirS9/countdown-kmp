package com.exam.countdown.repository

import app.cash.sqldelight.coroutines.asFlow
import app.cash.sqldelight.coroutines.mapToList
import com.exam.countdown.database.ExamDatabase
import com.exam.countdown.model.Exam
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import com.exam.countdown.database.Exam as DbExam

/**
 * SQLDelight-backed implementation. Used on Android, iOS, Desktop.
 * The [archived] column is stored as INTEGER (0/1) — mapped to Boolean here.
 */
class SqlDelightExamRepository(private val db: ExamDatabase) : ExamRepository {

    private val q = db.examQueries

    override fun observeUpcoming(): Flow<List<Exam>> =
        q.selectUpcoming().asFlow().mapToList(Dispatchers.Default).map { list -> list.map { it.toDomain() } }

    override fun observeArchived(): Flow<List<Exam>> =
        q.selectArchived().asFlow().mapToList(Dispatchers.Default).map { list -> list.map { it.toDomain() } }

    override suspend fun getById(id: Long): Exam? = withContext(Dispatchers.Default) {
        q.selectById(id).executeAsOneOrNull()?.toDomain()
    }

    override suspend fun insert(exam: Exam): Long = withContext(Dispatchers.Default) {
        q.insert(
            name = exam.name,
            subject = exam.subject,
            semester = exam.semester,
            examTimeEpochMillis = exam.examTimeEpochMillis,
            timezone = exam.timezone,
            colorTag = exam.colorTag,
        )
        // SQLite last_insert_rowid() — available via a custom transactionWithResult
        db.transactionWithResult {
            q.selectAll().executeAsList().lastOrNull()?.id ?: 0L
        }
    }

    override suspend fun update(exam: Exam) = withContext(Dispatchers.Default) {
        q.update(
            name = exam.name,
            subject = exam.subject,
            semester = exam.semester,
            examTimeEpochMillis = exam.examTimeEpochMillis,
            timezone = exam.timezone,
            colorTag = exam.colorTag,
            id = exam.id,
        )
    }

    override suspend fun archive(id: Long) = withContext(Dispatchers.Default) { q.archive(id) }
    override suspend fun unarchive(id: Long) = withContext(Dispatchers.Default) { q.unarchive(id) }
    override suspend fun delete(id: Long) = withContext(Dispatchers.Default) { q.deleteById(id) }

    override suspend fun insertAll(exams: List<Exam>) = withContext(Dispatchers.Default) {
        db.transaction {
            exams.forEach { exam ->
                q.insert(
                    name = exam.name,
                    subject = exam.subject,
                    semester = exam.semester,
                    examTimeEpochMillis = exam.examTimeEpochMillis,
                    timezone = exam.timezone,
                    colorTag = exam.colorTag,
                )
            }
        }
    }

    private fun DbExam.toDomain() = Exam(
        id = id,
        name = name,
        subject = subject,
        semester = semester,
        examTimeEpochMillis = examTimeEpochMillis,
        timezone = timezone,
        colorTag = colorTag,
        archived = archived != 0L,
    )
}
