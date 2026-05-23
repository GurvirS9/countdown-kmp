package com.exam.countdown.utils

import com.exam.countdown.model.COLOR_TAGS
import com.exam.countdown.model.Exam
import com.exam.countdown.utils.DateTimeUtils.toEpochMillis

/** Sample exams for demo / first-launch experience. */
object SampleData {

    val exams: List<Exam> = listOf(
        Exam(
            id = 0L,
            name = "PSLP",
            subject = "PSLP",
            semester = "4th sem",
            examTimeEpochMillis = toEpochMillis(2026, 5, 25, 8, 0),
            colorTag = COLOR_TAGS[0],
        ),
        Exam(
            id = 0L,
            name = "TOC",
            subject = "TOC",
            semester = "4th sem",
            examTimeEpochMillis = toEpochMillis(2026, 5, 28, 8, 0),
            colorTag = COLOR_TAGS[1],
        ),
        Exam(
            id = 0L,
            name = "CAS",
            subject = "CAS",
            semester = "4th sem",
            examTimeEpochMillis = toEpochMillis(2026, 6, 1, 8, 0),
            colorTag = COLOR_TAGS[2],
        ),
        Exam(
            id = 0L,
            name = "DBMS",
            subject = "DBMS",
            semester = "4th sem",
            examTimeEpochMillis = toEpochMillis(2026, 6, 3, 8, 0),
            colorTag = COLOR_TAGS[3],
        ),
        Exam(
            id = 0L,
            name = "AM2",
            subject = "AM2",
            semester = "2nd sem",
            examTimeEpochMillis = toEpochMillis(2026, 6, 4, 8, 0),
            colorTag = COLOR_TAGS[4],
        ),
        Exam(
            id = 0L,
            name = "Java",
            subject = "Java",
            semester = "4th sem",
            examTimeEpochMillis = toEpochMillis(2026, 6, 5, 8, 0),
            colorTag = COLOR_TAGS[5],
        ),
    )
}
