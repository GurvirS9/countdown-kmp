package com.exam.countdown.parser

import com.exam.countdown.model.APP_TIMEZONE
import com.exam.countdown.model.COLOR_TAGS
import com.exam.countdown.model.Exam
import com.exam.countdown.utils.DateTimeUtils

/** Represents the outcome of parsing a single line from a datesheet. */
sealed class ParseResult {
    data class Success(val exam: Exam) : ParseResult()
    data class Error(val line: String, val reason: String) : ParseResult()
}

/**
 * Parses free-form datesheet text into [Exam] objects.
 *
 * Supported formats (case-insensitive):
 *  1. "Physics - 24 May 2026 - 9:00 AM"
 *  2. "Chemistry: 26/05/2026 14:30"
 *  3. "Maths 30 May 2026 10 AM"
 *  4. "English | 02 Jun 2026 | 2 PM"
 */
object DatesheetParser {

    private val MONTH_MAP = mapOf(
        "jan" to 1, "feb" to 2, "mar" to 3, "apr" to 4,
        "may" to 5, "jun" to 6, "jul" to 7, "aug" to 8,
        "sep" to 9, "oct" to 10, "nov" to 11, "dec" to 12,
    )

    // Matches: "Subject - DD Mon YYYY - H:MM AM/PM" or "Subject | ... | H:MM AM/PM"
    private val FORMAT_NAMED_MONTH_SEPARATOR = Regex(
        """^(.+?)[\s\-:|]+(\d{1,2})\s+([A-Za-z]{3,9})\s+(\d{4})[\s\-:|]+(\d{1,2})(?::(\d{2}))?\s*(AM|PM)?\s*$""",
        RegexOption.IGNORE_CASE
    )

    // Matches: "Subject: DD/MM/YYYY HH:MM" or "Subject: DD-MM-YYYY HH:MM"
    private val FORMAT_NUMERIC_DATE = Regex(
        """^(.+?)[\s:]+(\d{1,2})[/\-](\d{1,2})[/\-](\d{4})\s+(\d{1,2}):(\d{2})\s*$""",
        RegexOption.IGNORE_CASE
    )

    fun parse(rawText: String): List<ParseResult> {
        return rawText.lines()
            .map { it.trim() }
            .filter { it.isNotBlank() }
            .mapIndexed { index, line -> parseLine(line, index) }
    }

    private fun parseLine(line: String, index: Int): ParseResult {
        // Try named-month format first
        FORMAT_NAMED_MONTH_SEPARATOR.matchEntire(line)?.let { m ->
            val subject = m.groupValues[1].trim().trimEnd('-', '|', ':').trim()
            val day = m.groupValues[2].toIntOrNull() ?: return ParseResult.Error(line, "Invalid day")
            val monthStr = m.groupValues[3].lowercase().take(3)
            val month = MONTH_MAP[monthStr] ?: return ParseResult.Error(line, "Unknown month: $monthStr")
            val year = m.groupValues[4].toIntOrNull() ?: return ParseResult.Error(line, "Invalid year")
            val hourRaw = m.groupValues[5].toIntOrNull() ?: return ParseResult.Error(line, "Invalid hour")
            val minute = m.groupValues[6].toIntOrNull() ?: 0
            val amPm = m.groupValues[7].uppercase()
            val hour = when {
                amPm == "PM" && hourRaw != 12 -> hourRaw + 12
                amPm == "AM" && hourRaw == 12 -> 0
                else -> hourRaw
            }
            return buildSuccess(subject, day, month, year, hour, minute, index, line)
        }

        // Try numeric date format
        FORMAT_NUMERIC_DATE.matchEntire(line)?.let { m ->
            val subject = m.groupValues[1].trim().trimEnd(':', '-', '|').trim()
            val day = m.groupValues[2].toIntOrNull() ?: return ParseResult.Error(line, "Invalid day")
            val month = m.groupValues[3].toIntOrNull() ?: return ParseResult.Error(line, "Invalid month")
            val year = m.groupValues[4].toIntOrNull() ?: return ParseResult.Error(line, "Invalid year")
            val hour = m.groupValues[5].toIntOrNull() ?: return ParseResult.Error(line, "Invalid hour")
            val minute = m.groupValues[6].toIntOrNull() ?: return ParseResult.Error(line, "Invalid minute")
            return buildSuccess(subject, day, month, year, hour, minute, index, line)
        }

        return ParseResult.Error(line, "Unrecognised format")
    }

    private fun buildSuccess(
        subject: String, day: Int, month: Int, year: Int,
        hour: Int, minute: Int, index: Int, line: String,
    ): ParseResult {
        return try {
            val epochMillis = DateTimeUtils.toEpochMillis(year, month, day, hour, minute)
            val color = COLOR_TAGS[index % COLOR_TAGS.size]
            ParseResult.Success(
                Exam(
                    name = subject,
                    subject = subject,
                    semester = "",
                    examTimeEpochMillis = epochMillis,
                    timezone = APP_TIMEZONE,
                    colorTag = color,
                )
            )
        } catch (e: Exception) {
            ParseResult.Error(line, "Date out of range: ${e.message}")
        }
    }
}
